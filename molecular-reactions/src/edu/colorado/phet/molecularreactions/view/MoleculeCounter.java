/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * MoleculeCounter
 * <p>
 * Listens to the model to detect when molecules are added and removed to the model,
 * and listens
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCounter extends JTextField implements PublishingModel.ModelListener,
                                                           Molecule.ClassListener {
    private Class moleculeClass;
    private MRModel model;
    private int cnt;
    // Flag to mark that we are adding or removing molecules from the model,
    // so that we don't respond to add/remove messages from the model
    private boolean selfUpdating;
    private MoleculeParamGenerator moleculeParamGenerator;

    /**
     * @param columns
     * @param moleculeClass
     * @param model
     */
    public MoleculeCounter( int columns, final Class moleculeClass, final MRModel model ) {
        super( columns );
        this.moleculeClass = moleculeClass;
        this.model = model;
        model.addListener( this );
        Molecule.addClassListener( this );

        Rectangle2D r = model.getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds, 5 );
        setText( "0" );

        this.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                selfUpdating = true;
                int diff = Integer.parseInt( getText() ) - cnt;

                for( int i = 0; i < Math.abs( diff ); i++ ) {

                    // Do we need to add molecules?
                    if( diff > 0 ) {
                        Point2D p = new Point2D.Double( model.getBox().getMinX() + 120,
                                                        model.getBox().getMinY() + 120 );
                        Vector2D v = new Vector2D.Double( 2, 2 );
                        Molecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                     moleculeParamGenerator );
                        addMoleculeToModel( m, model );
                        cnt++;
                    }

                    // Do we need to remove molecules?
                    else if( diff < 0 ) {
                        List modelElements = model.getModelElements();
                        for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                            Object o = modelElements.get( j );
                            if( moleculeClass.isInstance( o ) && !( (Molecule)o ).isPartOfComposite() ) {
                                Molecule molecule = (Molecule)o;
                                removeMoleculeFromModel( molecule, model );
                                cnt--;
                                break;
                            }
                        }
                        // We need to set the value in the text field in case we were asked to remove a
                        // molecule that couldn't be removed
                        setText( Integer.toString( cnt ) );
                    }
                }

                selfUpdating = false;
            }
        } );
    }

    private void addMoleculeToModel( Molecule m, MRModel model ) {
        model.addModelElement( m );
        if( m instanceof CompositeMolecule ) {
            SimpleMolecule[] components = m.getComponentMolecules();
            for( int j = 0; j < components.length; j++ ) {
                SimpleMolecule component = components[j];
                model.addModelElement( component );
            }
        }
    }

    private void removeMoleculeFromModel( Molecule molecule, MRModel model ) {
        model.removeModelElement( molecule );
        if( molecule instanceof CompositeMolecule ) {
            SimpleMolecule[] components = molecule.getComponentMolecules();
            for( int k = 0; k < components.length; k++ ) {
                SimpleMolecule component = components[k];
                model.removeModelElement( component );
            }
        }
    }

    private void setMoleculeCount() {
        List modelElements = model.getModelElements();
        int n = 0;
        for( int i = 0; i < modelElements.size(); i++ ) {
            Object o = modelElements.get( i );
            if( moleculeClass.isInstance( o ) && !( (Molecule)o ).isPartOfComposite() ) {
                n++;
            }
        }
        cnt = n;
        setText( Integer.toString( n ) );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PublishingModel.Listener
    //--------------------------------------------------------------------------------------------------

    public void modelElementAdded( ModelElement element ) {
        if( !selfUpdating && moleculeClass.isInstance( element ) ) {
            setMoleculeCount();
        }
    }

    public void modelElementRemoved( ModelElement element ) {
        if( !selfUpdating && moleculeClass.isInstance( element ) ) {
            setMoleculeCount();
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Molecule.ClassListener
    //--------------------------------------------------------------------------------------------------

    public void statusChanged( Molecule molecule ) {
        setMoleculeCount();
    }
}
