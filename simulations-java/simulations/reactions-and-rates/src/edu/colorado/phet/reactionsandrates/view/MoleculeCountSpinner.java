/* Copyright 2003-2009, University of Colorado */

package edu.colorado.phet.reactionsandrates.view;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.reactionsandrates.MRConfig;
import edu.colorado.phet.reactionsandrates.model.*;

/**
 * MoleculeCounterSpinner
 * <p/>
 * A JSpinner that controls and displays the number of molecules of a specified type.
 *
 * @author Ron LeMaster
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
    private int min, max;
    private boolean hasFocus = false;

    /**
     * @param moleculeClass
     * @param model
     */
    public MoleculeCountSpinner( final Class moleculeClass, final MRModel model, int min, int max ) {
        this.min = min;
        this.max = max;

        JFormattedTextField tf = ( (JSpinner.DefaultEditor)getEditor() ).getTextField();
        tf.setColumns( 3 );

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
        setValue( new Integer( min ) );

        // Respond to changes in the spinner
        this.addChangeListener( new SpinnerChangeListener( moleculeClass, model ) );

        // Track focus
        ( (NumberEditor)getEditor() ).getTextField().addFocusListener( new FocusAdapter() {
            public void focusGained( FocusEvent e ) {
                //System.out.println( "MoleculeCountSpinner.focusGained" );
                //System.out.println( "moleculeClass = " + moleculeClass );
                hasFocus = true;
            }

            public void focusLost( FocusEvent e ) {
                //System.out.println( "MoleculeCountSpinner.focusLost" );
                //System.out.println( "moleculeClass = " + moleculeClass );
                hasFocus = false;
            }
        } );
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

        if( !hasFocus ) {
            setValue( new Integer( n ) );
        }
    }


    private void resetValue() {
        setValue( new Integer( min ) );
        showInvalidValueDialog();
    }
    
    private void showInvalidValueDialog() {
        String pattern = MRConfig.RESOURCES.getLocalizedString( "messages.invalidValue" );
        Object[] args = { new Integer( min ), new Integer( max ) };
        String message = MessageFormat.format( pattern, args );
        JOptionPane.showMessageDialog( this, message );
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

            int value = ( (Integer)getValue() ).intValue();
            if( value < MoleculeCountSpinner.this.min || value > MoleculeCountSpinner.this.max ) {
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

            // Transfer the focus away from the control so the value will be
            // updated from the model again:
//            requestFocus( false );
        }
    }
}
