// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.test.debug_piccolo2d_1_3;

import java.awt.*;
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
 * Demonstrates a strange situation when PSwing's ComponentListener is repeatedly
 * called, alternating between calls to componentShown and componentHidden forever.
 * <p/>
 * See PhET Unfuddle #2140, Piccolo Issue 160.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DebugPSwingComponentAdapter extends JFrame {

    /*
    * =====================  WORKAROUNDS ========================
    * Two application workarounds have been identified.
    * See either (or both) of these flags to true.
    * And see the comments in the code where the flags are used.
    *
    * Note that a third workaround is to remove the ComponentListener that is
    * added in PSwing, and/or remove the override of PSwing.setVisible.
    */
    private static final boolean ENABLE_WORKAROUND_1 = false;
    private static final boolean ENABLE_WORKAROUND_2 = false;

    public DebugPSwingComponentAdapter() {
        setSize( new Dimension( 400, 200 ) );

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
        controlPanelNode.setOffset( 20, 20 );

        // configure control panel at startup, as in PhET hydrogen-atom sim
        controlPanelNode.setUp( /* some args */ );
    }

    private enum LightType {WHITE, MONOCHROMATIC}

    ;

    /*
    * Simple model of a light.
    * Listeners are notified when the light type changes.
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
    * Panel for selecting the light type via 2 radio buttons.
    * Listeners are notified when the selection changes.
    */
    private static class LightTypePanel extends JPanel {

        private final ArrayList<ChangeListener> listeners;
        private final JRadioButton whiteButton, monochromaticButton;

        public LightTypePanel() {

            listeners = new ArrayList<ChangeListener>();

            // buttons
            whiteButton = new JRadioButton( "white" );
            whiteButton.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( whiteButton.isSelected() ) {
                        fireStateChanged();
                    }
                }
            } );

            monochromaticButton = new JRadioButton( "monochromatic" );
            monochromaticButton.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( monochromaticButton.isSelected() ) {
                        fireStateChanged();
                    }
                }
            } );

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( whiteButton );
            buttonGroup.add( monochromaticButton );

            add( whiteButton );
            add( monochromaticButton );

            // Default state
            whiteButton.setSelected( true );
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
    * Node that provides controls for the light model.
    * Keeps the model and view synchronized.
    * Shows/hides controls that are specific to a light type.
    */
    public class LightControlPanelNode extends PhetPNode {

        private final Light model;
        private final LightTypePanel lightTypePanel;
        private final JCheckBox monochromaticFeatureCheckBox; // this control is specific to monochromatic lights
        private final PSwing monochromaticFeatureCheckBoxNode;

        // The actual application has a setter for disabling monochromatic-specific features via this flag.
        private boolean monochromaticFeatureEnabled;

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
            lightTypePanel.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    updateModel();
                }
            } );
            PSwing lightTypeControlWrapper = new PSwing( lightTypePanel );

            /*
            * A monochromatic feature that does nothing in this example.
            * This is the check box that has the setVisible problem, so I've
            * added debug output on System.out to demonstrate the calls to
            * setVisible, for both the JCheckBox and its PSwing.
            */
            monochromaticFeatureEnabled = true;
            monochromaticFeatureCheckBox = new JCheckBox( "some monochromatic feature" ) {
                @Override
                public void setVisible( boolean b ) {
                    System.out.println( "JCheckBox.setVisible " + b );
                    super.setVisible( b );
                }
            };
            monochromaticFeatureCheckBoxNode = new PSwing( monochromaticFeatureCheckBox ) {
                @Override
                public void setVisible( boolean b ) {
                    System.out.println( "PSwing.setVisible " + b );
                    super.setVisible( b );
                }
            };

            // layout
            addChild( lightTypeControlWrapper );
            addChild( monochromaticFeatureCheckBoxNode );
            lightTypeControlWrapper.setOffset( 0, 0 );
            monochromaticFeatureCheckBoxNode.setOffset( 0, lightTypeControlWrapper.getFullBoundsReference().getMaxY() + 5 );

            // sync with model
            updateView();
        }

        public void setUp( /* the actual application has some args here */ ) {
            // the actual application did something with the args, then set check box visibility.
            if ( ENABLE_WORKAROUND_2 ) {
                /*
                 * WORKAROUND 2:
                 * Visibility should be set based on the light type and whether the feature is enabled.
                 * This is a programming error and should be corrected in the actual application.
                 * I've left it in here because I don't understand why this is creating a problem.
                 */
                monochromaticFeatureCheckBoxNode.setVisible( lightTypePanel.isMonochromaticSelected() && monochromaticFeatureEnabled );
            }
            else {
                monochromaticFeatureCheckBoxNode.setVisible( lightTypePanel.isMonochromaticSelected() );
            }
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
            monochromaticFeatureCheckBoxNode.setVisible( isMonochromatic && monochromaticFeatureEnabled );
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
     *
     * @param args
     */
    public static void main( String[] args ) {
        if ( ENABLE_WORKAROUND_1 ) {
            /*
             * WORKAROUND 1:
             * Don't start the application in the AWT event dispatch thread.
             * Unfortunately all PhET application work this way, so this is
             * not a practical workaround.
             */
            startApplication();
        }
        else {
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
}
