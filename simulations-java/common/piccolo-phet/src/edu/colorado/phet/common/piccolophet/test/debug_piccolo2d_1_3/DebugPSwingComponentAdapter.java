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

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

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
        final PhetPCanvas canvas = new PhetPCanvas( new Dimension( 750, 750 ) );
        canvas.removeInputEventListener( canvas.getZoomEventHandler() );
        canvas.removeInputEventListener( canvas.getPanEventHandler() );
        setContentPane( canvas );
        
        // root node
        PNode rootNode = new PNode();
        canvas.addWorldChild( rootNode );
        rootNode.setOffset( 100, 100 );
        
        // control panel node
        LightControlPanel gunControlPanel = new LightControlPanel( model );
        rootNode.addChild( gunControlPanel );
        
        // ...called at startup, as in hydrogen-atom sim
        gunControlPanel.setTransitionWavelengths( null );
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
        
        public boolean isWhiteLightType() {
            return ( lightType == LightType.WHITE );
        }
        
        public boolean isMonochromaticLightType() {
            return ( lightType == LightType.MONOCHROMATIC );   
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
    public class LightControlPanel extends PhetPNode implements Observer {

        private final Light model;
        private final LightTypePanel lightTypePanel;
        private final JCheckBox checkBox;
        private final PSwing checkBoxNode;
        private boolean checkBoxFeatureEnabled;
        
        public LightControlPanel( Light model ) {
            super();
            
            this.model = model;
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
                    handleLightTypeChange();
                }
            } );

            // Sync with model
            updateAll();
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
        
        private void handleLightTypeChange() {
            System.out.println( "GunControlPanel.handleLightTypeChanged" );
            if ( lightTypePanel.isMonochromaticSelected() ) {
                checkBoxNode.setVisible( checkBoxFeatureEnabled );
                model.setLightType( LightType.MONOCHROMATIC );
            }
            else {
                checkBoxNode.setVisible( false );
                model.setLightType( LightType.WHITE );
            }
        }
        
        public void update( Observable o, Object arg ) {
            System.out.println( "GunControlPanel.update" );
            if ( o == model ) {
                if ( arg == Light.PROPERTY_LIGHT_TYPE ) {
                    lightTypePanel.setMonochromaticSelected( model.isMonochromaticLightType() );
                }
            }
        }
        
        private void updateAll() {
            System.out.println( "GunControlPanel.updateAll" );
            if ( model.isMonochromaticLightType() ) {
                lightTypePanel.setMonochromaticSelected( true );
            }
            else {
                lightTypePanel.setWhiteSelected( true );
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
