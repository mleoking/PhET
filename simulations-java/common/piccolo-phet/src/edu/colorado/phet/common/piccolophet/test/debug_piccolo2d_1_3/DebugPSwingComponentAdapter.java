package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

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
        controlPanelNode.setMonochromaticFeatureParameters();
    }
    
    private enum LightType { WHITE, MONOCHROMATIC };
    
    /*
     * Model, notifies observers when light type changes.
     */
    private static class Light {
        
        private final ArrayList<ChangeListener> listeners;
        private LightType lightType;
        
        public Light() {
            listeners = new ArrayList<ChangeListener>();
            this.lightType = LightType.MONOCHROMATIC;
        }
        
        public LightType getLightType() {
            return lightType;
        }
        
        public void setLightType( LightType lightType ) {
            if ( lightType != this.lightType ) {
                this.lightType = lightType;
                fireStateChanged();
            }
        }
        
        public void addChangeListener( ChangeListener listener ) {
            listeners.add( listener );
        }

        private void fireStateChanged() {
            ChangeEvent event = new ChangeEvent( this ); 
            for ( ChangeListener listener : listeners ) {
                listener.stateChanged( event );
            }
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
            setMonochromaticSelected( false );
        }
        
        public void setMonochromaticSelected( boolean selected ) {
            whiteButton.setSelected( !selected );
            monochromaticButton.setSelected( selected );
        }
        
        public boolean isMonochromaticSelected() {
            return monochromaticButton.isSelected();
        }
        
        public void addChangeListener( ChangeListener listener ) {
            listeners.add( listener );
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
        
        // actual application has a means of totally disabling the check box via a setter, regardless of light type
        private boolean checkBoxFeatureEnabled;
        
        public LightControlPanelNode( final Light model ) {
            super();
            
            // connect to model
            this.model = model;
            model.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    updateView();
                }
            } );
            
            // light type control
            lightTypePanel = new LightTypePanel();
            
            // feature check box
            checkBoxFeatureEnabled = false;
            checkBox = new JCheckBox( "some monochromatic feature" ) {
                @Override
                public void setVisible( boolean b ) {
                    System.out.println( "JCheckBox.setVisible " + b );
                    super.setVisible( b );
                }
            };
            PSwing lightTypeControlWrapper = new PSwing( lightTypePanel ) {
                @Override
                public void setVisible( boolean b ) {
                    System.out.println( "PSwing.setVisible " + b );
                    super.setVisible( b );
                }
            };
            checkBoxNode = new PSwing( checkBox );

            // layout
            addChild( lightTypeControlWrapper );
            addChild( checkBoxNode );
            lightTypeControlWrapper.setOffset( 0, 0 );
            checkBoxNode.setOffset( 0, lightTypeControlWrapper.getFullBoundsReference().getMaxY() + 5 );

            // Event handling
            lightTypePanel.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    updateModel();
                }
            } );

            // sync with model
            updateView();
        }
        
        public void setMonochromaticFeatureParameters( /* actual application has some args here */) {
            // actual application did something with the args, then set check box visibility.
            checkBoxNode.setVisible( lightTypePanel.isMonochromaticSelected() );
        }
        
        private void updateModel() {
            if ( lightTypePanel.isMonochromaticSelected() ) {
                model.setLightType( LightType.MONOCHROMATIC );
            }
            else {
                model.setLightType( LightType.WHITE );
            }
        }

        private void updateView() {
            boolean isMonochromatic = ( model.getLightType() == LightType.MONOCHROMATIC );
            lightTypePanel.setMonochromaticSelected( isMonochromatic );
            checkBoxNode.setVisible( isMonochromatic && checkBoxFeatureEnabled );
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
