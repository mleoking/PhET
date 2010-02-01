package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * See PhET Unfuddle #2140
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingComponentAdapter extends JFrame {
    
    public DebugPSwingComponentAdapter() {
        setSize( new Dimension( 1024, 768 ) );
        
        // model
        Light model = new Light();
        
        // canvas
        final PCanvas canvas = new PSwingCanvas();
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // control panel node
        LightControlPanelNode controlPanelNode = new LightControlPanelNode( model );
        canvas.getLayer().addChild( controlPanelNode );
        controlPanelNode.setOffset( 100, 100 );
        
        // ...called at startup, as in hydrogen-atom sim
        controlPanelNode.setTransitionWavelengths( null );
    }
    
    private enum LightType { WHITE, MONOCHROMATIC };
    
    /*
     * Model, notifies observers when light type changes.
     */
    private static class Light extends Observable {
        
        public static final String PROPERTY_LIGHT_TYPE = "lightType";
        
        private LightType lightType;
        
        public Light() {
            this.lightType = LightType.MONOCHROMATIC;
        }
        
        public LightType getLightType() {
            return lightType;
        }
        
        public void setLightType( LightType lightType ) {
            if ( lightType != this.lightType ) {
                this.lightType = lightType;
                notifyObservers( PROPERTY_LIGHT_TYPE );
            }
        }
        
        @Override
        public void notifyObservers() {
            setChanged();
            super.notifyObservers();
            clearChanged();
        }

        @Override
        public void notifyObservers( Object arg ) {
            setChanged();
            super.notifyObservers( arg );
            clearChanged();
        }
    }
    
    /*
     * Panel for selecting the light type via radio buttons.
     */
    private static class LightTypePanel extends JPanel {
        
        private final JRadioButton whiteButton, monochromaticButton;
        private final ArrayList<ChangeListener> listeners;
        
        public LightTypePanel() {
            
            // buttons
            whiteButton = new JRadioButton( "white" );
            monochromaticButton = new JRadioButton( "monochromatic" );
            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( whiteButton );
            buttonGroup.add( monochromaticButton );
            add( whiteButton );
            add( monochromaticButton );
            
            // event handling
            listeners = new ArrayList<ChangeListener>();
            whiteButton.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( whiteButton.isSelected() ) {
                        fireStateChanged();
                    }
                }      
            });
            monochromaticButton.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( monochromaticButton.isSelected() ) {
                        fireStateChanged();
                    }
                }
            } );
            
            // Default state
            setWhiteSelected( true );
        }
        
        public void setWhiteSelected( boolean selected ) {
            whiteButton.setSelected( selected );
            monochromaticButton.setSelected( !selected );
        }
        
        public boolean isWhiteSelected() {
            return whiteButton.isSelected();
        }
        
        public void setMonochromaticSelected( boolean selected ) {
            setWhiteSelected( !selected );
        }
        
        public boolean isMonochromaticSelected() {
            return !isWhiteSelected();
        }
        
        public void addChangeListener( ChangeListener listener ) {
            listeners.add( listener );
        }

        public void removeChangeListener( ChangeListener listener ) {
            listeners.remove( listener );
        }

        private void fireStateChanged() {
            ChangeEvent event = new ChangeEvent( this ); 
            for ( ChangeListener listener : listeners ) {
                listener.stateChanged( event );
            }
        }
    }
    
    /*
     * Node that provides controls for the model.
     * Select the light type and whether a monochromatic feature is enabled.
     */
    public class LightControlPanelNode extends PhetPNode {

        private final Light model;
        private final LightTypePanel lightTypePanel;
        private final JCheckBox checkBox;
        private final PSwing checkBoxNode;
        private boolean checkBoxFeatureEnabled;
        
        public LightControlPanelNode( final Light model ) {
            super();
            
            this.model = model;
            model.addObserver( new Observer() {
                public void update( Observable o, Object arg ) {
                    updateView();
                }
            } );
            
            checkBoxFeatureEnabled = false;
            
            lightTypePanel = new LightTypePanel();
            checkBox = new JCheckBox( "some monochromatic feature" ) {
                @Override
                public void setVisible( boolean b ) {
                    System.out.println( "transitionMarksControl.setVisible " + b );
                    super.setVisible( b );
                }
            };

            // Wrappers for Swing components
            PSwing lightTypeControlWrapper = new PSwing( lightTypePanel );
            checkBoxNode = new PSwing( checkBox );

            // Layering, back to front
            addChild( lightTypeControlWrapper );
            addChild( checkBoxNode );

            // Positioning
            lightTypeControlWrapper.setOffset( 0, 0 );
            checkBoxNode.setOffset( 0, lightTypeControlWrapper.getFullBoundsReference().getMaxY() + 5 );

            // Event handling
            lightTypePanel.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    updateModel();
                }
            } );

            // Sync with model
            updateView();
        }
        
        public void setTransitionWavelengths( double[] transitionWavelengths ) {
            System.out.println( "GunControlPanel.setTransitionWavelengths" );
            if ( lightTypePanel.isMonochromaticSelected() ) {
                checkBoxNode.setVisible( true );
            }
            else {
                checkBoxNode.setVisible( false );
            }
        }
        
        private void updateModel() {
            System.out.println( "GunControlPanel.handleLightTypeChanged" );
            if ( lightTypePanel.isMonochromaticSelected() ) {
                model.setLightType( LightType.MONOCHROMATIC );
            }
            else {
                model.setLightType( LightType.WHITE );
            }
        }

        private void updateView() {
            System.out.println( "GunControlPanel.updateView" );
            if ( model.getLightType() == LightType.MONOCHROMATIC ) {
                lightTypePanel.setMonochromaticSelected( true );
                checkBoxNode.setVisible( checkBoxFeatureEnabled );
            }
            else {
                lightTypePanel.setWhiteSelected( true );
                checkBoxNode.setVisible( false );
            }
        }
    }
    
    /*
     * Starts the application.
     */
    private static void startApplication() {
        JFrame frame = new DebugPSwingComponentAdapter();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        // center on the screen
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = (int) ( tk.getScreenSize().getWidth() / 2 - frame.getWidth() / 2 );
        int y = (int) ( tk.getScreenSize().getHeight() / 2 - frame.getHeight() / 2 );
        frame.setLocation( x, y );
        frame.setVisible( true );
    }
    
    /**
     * Executed synchronously on the AWT event dispatching thread, just like PhET applications.
     * @param args
     */
    public static void main( String[] args ) {
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {
                    startApplication();
                }
            } );
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
        }
        catch ( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
