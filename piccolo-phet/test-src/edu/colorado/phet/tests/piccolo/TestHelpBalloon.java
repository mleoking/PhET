/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.tests.piccolo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * TestHelpBalloon tests the features of HelpBalloon.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpBalloon extends PhetApplication {

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick
    
    private static final Object DEFAULT_ARROW_POSITION = HelpBalloon.TOP_LEFT;
    private static final int DEFAULT_ARROW_LENGTH = 40; // pixels
    private static final int DEFAULT_ARROW_ROTATION = 0; // degrees

    /* Test harness */
    public static void main( final String[] args ) {
        try {
            TestHelpBalloon app = new TestHelpBalloon( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /* Application */
    public TestHelpBalloon( String[] args ) throws InterruptedException {
        super( args, "TestHelpBalloon", "test of HelpBalloon", "0.1", new FrameSetup.CenteredWithSize( 640, 480 ) );

        Module module1 = new TestModule( "Module 1" );
        addModule( module1 );
    }
    
    /* Clock */
    private static class TestClock extends SwingClock {
        public TestClock() {
            super( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        }
    }

    /* Module */
    private static class TestModule extends PiccoloModule {
        
        private HelpBalloon _helpBalloon;
        
        /**
         * Constructor.
         * 
         * @param title
         */
        public TestModule( String title ) {
            super( title, new TestClock(), true /* startsPaused */);

            setLogoPanel( null );
            setClockControlPanel( null );
            
            // Play area --------------------------------------------
            
            // Canvas
            PhetPCanvas canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( canvas );
            
            PPath pathNode = new PPath();
            pathNode.setPathToRectangle( 0, 0, 75, 75 );
            pathNode.setPaint( Color.RED );
            pathNode.setOffset( 150, 150 );
            pathNode.addInputEventListener( new PDragEventHandler() );
            pathNode.addInputEventListener( new CursorHandler() );
            canvas.getLayer().addChild( pathNode );
            
            // Control panel --------------------------------------------
            
            Object[] arrowPosition = {
              HelpBalloon.TOP_LEFT, HelpBalloon.TOP_CENTER, HelpBalloon.TOP_RIGHT,
              HelpBalloon.BOTTOM_LEFT, HelpBalloon.BOTTOM_CENTER, HelpBalloon.BOTTOM_RIGHT,
              HelpBalloon.LEFT_TOP, HelpBalloon.LEFT_CENTER, HelpBalloon.LEFT_BOTTOM,
              HelpBalloon.RIGHT_TOP, HelpBalloon.RIGHT_CENTER, HelpBalloon.RIGHT_BOTTOM
            };
            final JComboBox arrowPositionComboBox = new JComboBox( arrowPosition );
            arrowPositionComboBox.setSelectedItem( DEFAULT_ARROW_POSITION );
            arrowPositionComboBox.setMaximumRowCount( arrowPosition.length );
            arrowPositionComboBox.addItemListener( new ItemListener() {
                public void itemStateChanged( ItemEvent e ) {
                    if ( e.getStateChange() == ItemEvent.SELECTED ) {
                        _helpBalloon.setArrowPosition( arrowPositionComboBox.getSelectedItem() );
                    }
                }
            } );
            JPanel arrowPositionPanel = new JPanel();
            EasyGridBagLayout positionLayout = new EasyGridBagLayout( arrowPositionPanel );
            arrowPositionPanel.setLayout( positionLayout );
            positionLayout.addComponent( new JLabel( "position:" ), 0, 0 );
            positionLayout.addComponent( arrowPositionComboBox, 0, 1 );
            
            final JLabel arrowLengthLabel = new JLabel( "length = " + DEFAULT_ARROW_LENGTH );
            final JSlider arrowLengthSlider = new JSlider();
            arrowLengthSlider.setMinimum( 0 );
            arrowLengthSlider.setMaximum( 100 );
            arrowLengthSlider.setValue( DEFAULT_ARROW_LENGTH );
            arrowLengthSlider.setMajorTickSpacing( 50 );
            arrowLengthSlider.setMinorTickSpacing( 10 );
            arrowLengthSlider.setPaintTicks( true );
            arrowLengthSlider.setPaintLabels( true );
            arrowLengthSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int arrowLength = arrowLengthSlider.getValue();
                    arrowLengthLabel.setText( "length = " + arrowLength );
                    _helpBalloon.setArrowLength( arrowLength );
                }
            } );
            
            final JLabel arrowRotationLabel = new JLabel( "rotation = " + DEFAULT_ARROW_ROTATION );
            final JSlider arrowRotationSlider = new JSlider();
            arrowRotationSlider.setMinimum( (int) HelpBalloon.MIN_ARROW_ROTATION );
            arrowRotationSlider.setMaximum( (int) HelpBalloon.MAX_ARROW_ROTATION );
            arrowRotationSlider.setValue( DEFAULT_ARROW_ROTATION );
            arrowRotationSlider.setMajorTickSpacing( (int) HelpBalloon.MAX_ARROW_ROTATION );
            arrowRotationSlider.setMinorTickSpacing( 10 );
            arrowRotationSlider.setPaintTicks( true );
            arrowRotationSlider.setPaintLabels( true );
            arrowRotationSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int arrowRotation = arrowRotationSlider.getValue();
                    arrowRotationLabel.setText( "rotation = " + arrowRotation );
                    _helpBalloon.setArrowRotation( arrowRotation );
                }
            } );
            
            JPanel arrowPanel = new JPanel();
            arrowPanel.setBorder( new TitledBorder( "Arrow attributes" ) );
            EasyGridBagLayout layout = new EasyGridBagLayout( arrowPanel );
            arrowPanel.setLayout( layout );
            layout.addComponent( arrowPositionPanel, 0, 0 );
            layout.addComponent( arrowLengthLabel, 1, 0 );
            layout.addComponent( arrowLengthSlider, 2, 0 );
            layout.addComponent( arrowRotationLabel, 3, 0 );
            layout.addComponent( arrowRotationSlider, 4, 0 );
            
            // Control panel
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            controlPanel.addControl( arrowPanel );
            
            // Help --------------------------------------------

            HelpPane helpPane = getDefaultHelpPane();
            
            // Help that points at a static location
            _helpBalloon = new HelpBalloon( helpPane, 
                    "<html>This is a HelpBalloon.<br>Adjust its properties<br>in the control panel</html>", 
                    DEFAULT_ARROW_POSITION, DEFAULT_ARROW_LENGTH, DEFAULT_ARROW_ROTATION );
            _helpBalloon.pointAt( pathNode, canvas );
            helpPane.add( _helpBalloon );
        }

        /* Enables the help button and help menu item */
        public boolean hasHelp() {
            return true;
        }
    }
}
