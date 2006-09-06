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

import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.Molecule;
import edu.colorado.phet.molecularreactions.model.MoleculeFactory;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.ModelElement;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.List;

/**
 * MoleculeCounter
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MoleculeCounter extends JTextField implements PublishingModel.ModelListener {
        private Class moleculeClass;
        private int cnt;
        // Flag to mark that we are adding or removing molecules from the model,
        // so that we don't respond to add/remove messages from the model
        private boolean selfUpdating;

        public MoleculeCounter( int columns, final Class moleculeClass, final MRModel model ) {
            super( columns );
            this.moleculeClass = moleculeClass;
            model.addListener( this );
            setText( "0" );

            this.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selfUpdating = true;
                    int diff = Integer.parseInt( getText() ) - cnt;

                    for( int i = 0; i < Math.abs( diff ); i++ ) {

                        // Do we need to add molecules?
                        if( diff > 0 ) {
                            Point2D p = new Point2D.Double( model.getBox().getMinX() + 20,
                                                            model.getBox().getMinY() + 20 );
                            Vector2D v = new Vector2D.Double( 2, 2 );
                            Molecule m = MoleculeFactory.createMolecule( moleculeClass,
                                                                         p,
                                                                         v );
                            model.addModelElement( m );
                            cnt++;
                        }

                        // Do we need to remove molecules?
                        else if( diff < 0 ) {
                            List modelElements = model.getModelElements();
                            for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                                Object o = modelElements.get( j );
                                if( moleculeClass.isInstance( o ) && !((Molecule)o).isPartOfComposite() ) {
                                    Molecule molecule = (Molecule)o;
                                    model.removeModelElement( molecule );
                                    cnt--;
                                    break;
                                }
                            }
                            // We need to set the value in the text field in case we were asked to remove a
                            // molecule that couldn't be removed
                            setText( Integer.toString( cnt ));
                        }
                    }

                    selfUpdating = false;
                }
            } );
        }

        public void modelElementAdded( ModelElement element ) {
            if( !selfUpdating && moleculeClass.isInstance( element ) ) {
                cnt = Integer.parseInt( getText() );
                setText( Integer.toString( ++cnt ) );
            }
        }

        public void modelElementRemoved( ModelElement element ) {
            if( !selfUpdating && moleculeClass.isInstance( element ) ) {
                int cnt = Integer.parseInt( getText() );
                setText( Integer.toString( --cnt ) );
            }
        }
    }
