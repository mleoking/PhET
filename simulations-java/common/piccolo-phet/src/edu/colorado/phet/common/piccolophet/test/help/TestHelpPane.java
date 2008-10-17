/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.piccolophet.test.help;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.piccolophet.help.HelpPane;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;


/**
 * TestHelpPane tests PhET's Piccolo-compatible help subsystem.
 * It tests position and visibilty tracking for various objects.
 * The objects consist of JComponents and PNodes that are
 * "screen children" and "world children".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestHelpPane extends PhetApplication {

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

    // Colors for module 1
    private static final Color CANVAS_COLOR_1 = new Color( 255, 208, 252 );
    private static final Color SCREEN_COLOR_1 = Color.BLUE;
    private static final Color WORLD_COLOR_1 = Color.ORANGE;

    // Colors for module 2
    private static final Color CANVAS_COLOR_2 = new Color( 208, 255, 252 );
    private static final Color SCREEN_COLOR_2 = Color.RED;
    private static final Color WORLD_COLOR_2 = Color.GREEN;

    /* Test harness */
    public static void main( final String[] args ) {
        ApplicationConstructor applicationConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {

                try {
                    TestHelpPane app = new TestHelpPane( config );
                    return app;
                }
                catch( Exception e ) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
        PhetApplicationConfig phetApplicationConfig = new PhetApplicationConfig( args, applicationConstructor, "piccolo-phet" );
        new PhetApplicationLauncher().launchSim( phetApplicationConfig, applicationConstructor );
    }

    /* Application */
    public TestHelpPane( PhetApplicationConfig config) throws InterruptedException {
        super( config);

        Module module1 = new TestModule( "Module 1", CANVAS_COLOR_1, SCREEN_COLOR_1, WORLD_COLOR_1 );
        addModule( module1 );

        Module module2 = new TestModule( "Module 2", CANVAS_COLOR_2, SCREEN_COLOR_2, WORLD_COLOR_2 );
        addModule( module2 );
    }

    /* Clock */
    private static class TestClock extends SwingClock {
        public TestClock() {
            super( 1000 / CLOCK_RATE, new TimingStrategy.Constant( MODEL_RATE ) );
        }
    }

    /* Module */
    private static class TestModule extends PiccoloModule {

        /**
         * Constructor.
         *
         * @param title            title that appears in the module's tab
         * @param canvasColor      color of the canvas background
         * @param screenChildColor color used for all "screen children"
         * @param worldChildColor  color used for all "world children"
         */
        public TestModule( String title, Color canvasColor, Color screenChildColor, Color worldChildColor ) {
            super( title, new TestClock(), true /* startsPaused */ );

            // Play area --------------------------------------------

            // Canvas
            PhetPCanvas canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setSimulationPanel( canvas );
            canvas.setBackground( canvasColor );

            // Composite (path + text) screen child...
            PPath screenPath = new PPath();
            screenPath.setPathToEllipse( 0, 0, 100, 100 );
            screenPath.setPaint( screenChildColor );
            PText screenText = new PText( "screen" );
            screenText.setOffset( screenPath.getWidth() / 2 - screenText.getWidth() / 2, screenPath.getHeight() / 2 - screenText.getHeight() / 2 );
            final PComposite screenComposite = new PComposite();
            screenComposite.addChild( screenPath );
            screenComposite.addChild( screenText );
            screenComposite.setOffset( 300, 250 );
            screenComposite.addInputEventListener( new CursorHandler() );
            screenComposite.addInputEventListener( new PDragEventHandler() );
            canvas.addScreenChild( screenComposite );

            // Composite (path + text) world child...
            PPath worldPath = new PPath();
            worldPath.setPathToRectangle( 0, 0, 200, 200 );
            worldPath.setPaint( worldChildColor );
            PText worldText = new PText( "world" );
            worldText.setOffset( worldPath.getWidth() / 2 - worldText.getWidth() / 2, worldPath.getHeight() / 2 - worldText.getHeight() / 2 );
            final PComposite worldComposite = new PComposite();
            worldComposite.addChild( worldPath );
            worldComposite.addChild( worldText );
            worldComposite.setOffset( 700, 100 );
            worldComposite.addInputEventListener( new CursorHandler() );
            worldComposite.addInputEventListener( new PDragEventHandler() );
            canvas.addWorldChild( worldComposite );

            // PSwing screen child...
            JButton screenButton1 = new JButton( "screen button 1" );
            screenButton1.setOpaque( false );
            JButton screenButton2 = new JButton( "screen button 2" );
            screenButton2.setOpaque( false );
            JPanel screenPanel = new JPanel();
            screenPanel.setBorder( new TitledBorder( "screen panel" ) );
            screenPanel.setBackground( screenChildColor );
            screenPanel.setLayout( new BorderLayout() );
            screenPanel.add( screenButton1, BorderLayout.NORTH );
            screenPanel.add( screenButton2, BorderLayout.SOUTH );
            final PSwing screenPSwing = new PSwing( screenPanel );
            screenPSwing.setOffset( 50, 450 );
            canvas.addScreenChild( screenPSwing );

            // PSwing world child...
            JButton worldButton1 = new JButton( "world button 1" );
            worldButton1.setOpaque( false );
            JButton worldButton2 = new JButton( "world button 2" );
            worldButton2.setOpaque( false );
            JPanel worldPanel = new JPanel();
            worldPanel.setBorder( new TitledBorder( "world panel" ) );
            worldPanel.setBackground( worldChildColor );
            worldPanel.setLayout( new BorderLayout() );
            worldPanel.add( worldButton1, BorderLayout.NORTH );
            worldPanel.add( worldButton2, BorderLayout.SOUTH );
            final PSwing worldPSwing = new PSwing( worldPanel );
            worldPSwing.setOffset( 850, 800 );
            canvas.addWorldChild( worldPSwing );

            // Control panel --------------------------------------------

            // "Screen children" visibility control
            final JCheckBox screenCheckBox = new JCheckBox( "screen", true /* selected */ );
            screenCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    screenComposite.setVisible( screenCheckBox.isSelected() );
                    screenPSwing.setVisible( screenCheckBox.isSelected() );
                }
            } );

            // "World children" visibility control
            final JCheckBox worldCheckBox = new JCheckBox( "world", true /* selected */ );
            worldCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    worldComposite.setVisible( worldCheckBox.isSelected() );
                    worldPSwing.setVisible( worldCheckBox.isSelected() );
                }
            } );

            // Control panel
            ControlPanel controlPanel = new ControlPanel();
            setControlPanel( controlPanel );
            controlPanel.addControl( screenCheckBox );
            controlPanel.addControl( worldCheckBox );

            // Debug --------------------------------------------

            // Assign names to all PNodes, for debugging...
            screenPath.addAttribute( "name", "screenPath" );
            screenText.addAttribute( "name", "screenText" );
            screenComposite.addAttribute( "name", "screenComposite" );
            worldPath.addAttribute( "name", "worldPath" );
            worldText.addAttribute( "name", "worldText" );
            worldComposite.addAttribute( "name", "worldComposite" );
            screenPSwing.addAttribute( "name", "screenPSwing" );
            worldPSwing.addAttribute( "name", "worldPSwing" );

            // Assign names to all JComponents, for debugging...
            screenButton1.setName( "screenButton1" );
            screenButton2.setName( "screenButton2" );
            screenPanel.setName( "screenPanel" );
            worldButton1.setName( "worldButton1" );
            worldButton2.setName( "worldButton2" );
            worldPanel.setName( "worldPanel" );
            screenCheckBox.setName( "circleCheckBox" );
            worldCheckBox.setName( "squareCheckBox" );

            // Help --------------------------------------------

            HelpPane helpPane = getDefaultHelpPane();

            // Help that doesn't point at anything, has no arrow
            HelpBalloon noArrowHelp = new HelpBalloon( helpPane, "no arrow" );
            noArrowHelp.setLocation( 50, 200 );
            helpPane.add( noArrowHelp );

            // Help that points at a static location
            HelpBalloon pointHelp = new HelpBalloon( helpPane, "static point", HelpBalloon.BOTTOM_LEFT, 40, 45 );
            pointHelp.pointAt( 50, 150 );
            helpPane.add( pointHelp );

            // Help for stuff in the control panel...

            HelpBalloon screenCheckBoxHelp = new HelpBalloon( helpPane, "Show screen children", HelpBalloon.RIGHT_BOTTOM, 40 );
            screenCheckBoxHelp.pointAt( screenCheckBox );
            screenCheckBoxHelp.setArrowFillPaint( screenChildColor );
            helpPane.add( screenCheckBoxHelp );

            HelpBalloon worldCheckBoxHelp = new HelpBalloon( helpPane, "Show world children", HelpBalloon.RIGHT_TOP, 40, -45 );
            worldCheckBoxHelp.pointAt( worldCheckBox );
            worldCheckBoxHelp.setArrowFillPaint( worldChildColor );
            helpPane.add( worldCheckBoxHelp );

            // Help for screen children...

            HelpBalloon screenCompositeHelp = new HelpBalloon( helpPane, "PComposite screen child", HelpBalloon.BOTTOM_CENTER, 20 );
            screenCompositeHelp.pointAt( screenComposite, canvas );
            helpPane.add( screenCompositeHelp );

            HelpBalloon screenTextHelp = new HelpBalloon( helpPane, "PText in PComposite", HelpBalloon.LEFT_CENTER, 20, -30 );
            screenTextHelp.pointAt( screenText, canvas );
            helpPane.add( screenTextHelp );

            HelpBalloon screenPathHelp = new HelpBalloon( helpPane, "PPath in PComposite", HelpBalloon.RIGHT_CENTER, 20 );
            screenPathHelp.pointAt( screenPath, canvas );
            helpPane.add( screenPathHelp );

            HelpBalloon screenPSwingHelp = new HelpBalloon( helpPane, "PSwing screen child", HelpBalloon.BOTTOM_CENTER, 20 );
            screenPSwingHelp.pointAt( screenPSwing, canvas );
            helpPane.add( screenPSwingHelp );

            HelpBalloon screenPanelHelp = new HelpBalloon( helpPane, "JPanel in PSwing", HelpBalloon.TOP_CENTER, 20 );
            screenPanelHelp.pointAt( screenPanel, screenPSwing, canvas );
            helpPane.add( screenPanelHelp );

            HelpBalloon screenButton1Help = new HelpBalloon( helpPane, "JButton in JPanel in PSwing", HelpBalloon.LEFT_BOTTOM, 20, -30 );
            screenButton1Help.pointAt( screenButton1, screenPSwing, canvas );
            helpPane.add( screenButton1Help );

            // Help for world children...

            HelpBalloon worldCompositeHelp = new HelpBalloon( helpPane, "PComposite world child", HelpBalloon.BOTTOM_CENTER, 20 );
            worldCompositeHelp.pointAt( worldComposite, canvas );
            helpPane.add( worldCompositeHelp );

            HelpBalloon worldTextHelp = new HelpBalloon( helpPane, "PText in PComposite", HelpBalloon.LEFT_CENTER, 20, -30 );
            worldTextHelp.pointAt( worldText, canvas );
            helpPane.add( worldTextHelp );

            HelpBalloon worldPathHelp = new HelpBalloon( helpPane, "PPath in PComposite", HelpBalloon.RIGHT_CENTER, 20 );
            worldPathHelp.pointAt( worldPath, canvas );
            helpPane.add( worldPathHelp );

            HelpBalloon worldPSwingHelp = new HelpBalloon( helpPane, "PSwing world child", HelpBalloon.BOTTOM_CENTER, 20 );
            worldPSwingHelp.pointAt( worldPSwing, canvas );
            helpPane.add( worldPSwingHelp );

            HelpBalloon worldPanelHelp = new HelpBalloon( helpPane, "JPanel in PSwing", HelpBalloon.TOP_CENTER, 20 );
            worldPanelHelp.pointAt( worldPanel, worldPSwing, canvas );
            helpPane.add( worldPanelHelp );

            HelpBalloon worldButton1Help = new HelpBalloon( helpPane, "JButton in JPanel in PSwing", HelpBalloon.LEFT_BOTTOM, 20, -30 );
            worldButton1Help.pointAt( worldButton1, worldPSwing, canvas );
            helpPane.add( worldButton1Help );
        }

        /* Enables the help button and help menu item */
        public boolean hasHelp() {
            return true;
        }
    }
}
