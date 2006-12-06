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

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.molecularreactions.model.*;
import edu.colorado.phet.molecularreactions.MRConfig;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * MoleculeCounterSpinner
 * <p/>
 * A JSpinner that controls and displays the number of molecules of a specified type.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCountSpinner extends JSpinner implements PublishingModel.ModelListener,
                                                              AbstractMolecule.ClassListener {
    private Class moleculeClass;
    private MRModel model;
    private int cnt;
    // Flag to mark that we are adding or removing molecules from the model,
    // so that we don't respond to add/remove messages from the model
    private boolean selfUpdating;
    private MoleculeParamGenerator moleculeParamGenerator;
    private int maxValue;
    private boolean hasFocus;

    /**
     * @param moleculeClass
     * @param model
     */
    public MoleculeCountSpinner( final Class moleculeClass, final MRModel model, int maxValue ) {
        this.maxValue = maxValue;

        JFormattedTextField tf = ( (JSpinner.DefaultEditor)getEditor() ).getTextField();
        tf.setColumns( 2 );

        this.moleculeClass = moleculeClass;
        this.model = model;
        model.addListener( this );
        AbstractMolecule.addClassListener( this );

        Rectangle2D r = model.getBox().getBounds();
        Rectangle2D generatorBounds = new Rectangle2D.Double( r.getMinX() + 20,
                                                              r.getMinY() + 20,
                                                              r.getWidth() - 40,
                                                              r.getHeight() - 40 );
        moleculeParamGenerator = new ConstantTemperatureMoleculeParamGenerator( generatorBounds,
                                                                   model,
                                                                   .1,
                                                                   0,
                                                                   Math.PI * 2,
                                                                   moleculeClass );
//        moleculeParamGenerator = new RandomMoleculeParamGenerator( generatorBounds,
//                                                                   MRConfig.MAX_SPEED,
//                                                                   .1,
//                                                                   0,
//                                                                   Math.PI * 2 );
        setValue( new Integer( 0 ) );

        // Respond to changes in the spinner
        this.addChangeListener( new SpinnerChangeListener( moleculeClass, model ) );

        // Track focus
        ( (NumberEditor)getEditor() ).getTextField().addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
//                System.out.println( "MoleculeCountSpinner.focusGained" );
//                System.out.println( "moleculeClass = " + moleculeClass );
                hasFocus = true;
            }

            public void focusLost( FocusEvent e ) {
//                System.out.println( "MoleculeCountSpinner.focusLost" );
//                System.out.println( "moleculeClass = " + moleculeClass );
                hasFocus = false;
            }
        } );
    }

    /**
     * Sets both the spinner and the text field, and make the text field look like
     * a normal text field when it's not editable.
     *
     * @param editable
     */
    public void setEditable( boolean editable ) {
        setEnabled( editable );
        JFormattedTextField tf = ( (JSpinner.DefaultEditor)getEditor() ).getTextField();
        tf.setEnabled( true );
        tf.setEditable( editable );
        tf.setForeground( Color.black );
    }

    private void addMoleculeToModel( AbstractMolecule m, MRModel model ) {
        model.addModelElement( m );
        if( m instanceof CompositeMolecule ) {
            SimpleMolecule[] components = m.getComponentMolecules();
            for( int j = 0; j < components.length; j++ ) {
                SimpleMolecule component = components[j];
                model.addModelElement( component );
            }
        }
    }

    private void removeMoleculeFromModel( AbstractMolecule molecule, MRModel model ) {
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
            if( moleculeClass.isInstance( o ) && !( (AbstractMolecule)o ).isPartOfComposite() ) {
                n++;
            }
        }
        cnt = n;
        setValue( new Integer( n ) );
    }


    private void resetValue() {
        setValue( new Integer( 0 ) );
        JOptionPane.showMessageDialog( this, SimStrings.get( "Util.maxValueExceeded" ) );
        requestFocus();
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of Molecule.ClassListener
    //--------------------------------------------------------------------------------------------------

    public void statusChanged( AbstractMolecule molecule ) {
        setMoleculeCount();
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

    public void endOfTimeStep( PublishingModel model, ClockEvent event ) {
        // noop
    }

    //--------------------------------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------------------------------

    private class SpinnerChangeListener implements ChangeListener {
        private final Class moleculeClass;
        private final MRModel model;

        public SpinnerChangeListener( Class moleculeClass, MRModel model ) {
            this.moleculeClass = moleculeClass;
            this.model = model;
        }

        public void stateChanged( ChangeEvent e ) {
            selfUpdating = true;

            if( ( (Integer)getValue() ).intValue() > MoleculeCountSpinner.this.maxValue ) {
                resetValue();
            }

            final int diff = ( (Integer)getValue() ).intValue() - cnt;
            for( int i = 0; i < Math.abs( diff ); i++ ) {

                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {

                        // Do we need to add molecules?
                        if( diff > 0 ) {
                            AbstractMolecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                                 moleculeParamGenerator );
                            addMoleculeToModel( m, model );
                        }

                        // Do we need to remove molecules?
                        else if( diff < 0 ) {
                            List modelElements = model.getModelElements();
                            for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                                Object o = modelElements.get( j );
                                if( moleculeClass.isInstance( o ) && !( (AbstractMolecule)o ).isPartOfComposite() ) {
                                    AbstractMolecule molecule = (AbstractMolecule)o;
                                    removeMoleculeFromModel( molecule, model );
                                    break;
                                }
                            }
                            // We need to set the value in the text field in case we were asked to remove a
                            // molecule that couldn't be removed
                            setMoleculeCount();
                        }
                    }
                } );
            }

            selfUpdating = false;
        }
    }
}
