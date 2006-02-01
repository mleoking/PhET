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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.CursorHandler;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.HelpBalloon;
import edu.colorado.phet.piccolo.help.HelpPane;
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
        try {
            TestHelpPane app = new TestHelpPane( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /* Application */
    public TestHelpPane( String[] args ) throws InterruptedException {
        super( args, "TestHelpPane", "test of piccolo-compatible Help", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

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
         * @param title              title that appears in the module's tab
         * @param canvasColor        color of the canvas background
         * @param screenChildColor   color used for all "screen children"
         * @param worldChildColor    color used for all "world children"
         */
        public TestModule( String title, Color canvasColor, Color screenChildColor, Color worldChildColor ) {
            super( title, new TestClock(), true /* startsPaused */);

            // Play area --------------------------------------------
            
            // Canvas
            PhetPCanvas canvas = new PhetPCanvas( new Dimension( 1000, 1000 ) );
            setPhetPCanvas( canvas );
            canvas.setBackground( canvasColor );
            
            // Composite (path + text) screen child...
            PPath screenPath = new PPath();
            screenPath.setPathToEllipse( 0, 0, 100, 100 );
            screenPath.setPaint( screenChildColor );
            PText screenText = new PText( "screen" );
            screenText.setOffset( screenPath.getWidth()/2 - screenText.getWidth()/2, screenPath.getHeight()/2 - screenText.getHeight()/2 );
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
            worldText.setOffset( worldPath.getWidth()/2 - worldText.getWidth()/2, worldPath.getHeight()/2 - worldText.getHeight()/2 );
            final PComposite worldComposite = new PComposite();
            worldComposite.addChild( worldPath );
            worldComposite.addChild( worldText );
            worldComposite.setOffset( 700, 100 );
            worldComposite.addInputEventListener( new CursorHandler() );
            worldComposite.addInputEventListener( new PDragEventHandler() );
            canvas.addWorldChild( worldComposite );
            
            // PSwing screen child...
            JButton screenButton = new JButton( "screen" );
            screenButton.setOpaque( false );
            screenButton.setForeground( screenChildColor );
            final PSwing screenPSwing = new PSwing( canvas, screenButton );
            screenPSwing.setOffset( 200, 500 );
            canvas.addScreenChild( screenPSwing );
            
            // PSwing world child...
            JButton worldButton = new JButton( "world" );
            worldButton.setOpaque( false );
            worldButton.setForeground( worldChildColor );
            final PSwing worldPSwing = new PSwing( canvas, worldButton );
            worldPSwing.setOffset( 700, 700 );
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
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            controlPanel.addControl( screenCheckBox );
            controlPanel.addControl( worldCheckBox );

            // Debug --------------------------------------------
            
            // Assign names to all PNodes and JComponents, for debugging...
            screenPath.addAttribute( "name", "screenPath" );
            screenText.addAttribute( "name", "screenText" );
            screenComposite.addAttribute( "name", "screenComposite" );
            worldPath.addAttribute( "name", "worldPath" );
            worldText.addAttribute( "name", "worldText" );
            worldComposite.addAttribute( "name", "worldComposite" );
            screenButton.setName( "screenButton " );
            screenPSwing.addAttribute( "name", "screenPSwing" );
            worldButton.setName( "worldButton" );
            worldPSwing.addAttribute( "name", "worldPSwing" );
            screenCheckBox.setName( "circleCheckBox" );
            worldCheckBox.setName( "squareCheckBox" );
            
            // Help --------------------------------------------

            HelpPane helpPane = getDefaultHelpPane();

            // Help for stuff in the control panel...
            
            HelpBalloon screenCheckBoxHelp = new HelpBalloon( helpPane, "Show screen children", HelpBalloon.RIGHT_BOTTOM, 40 );
            screenCheckBoxHelp.pointAt( screenCheckBox );
            screenCheckBoxHelp.setArrowFillPaint( screenChildColor );
            helpPane.add( screenCheckBoxHelp );
            
            HelpBalloon worldCheckBoxHelp = new HelpBalloon( helpPane, "Show world children", HelpBalloon.RIGHT_TOP, 40 );
            worldCheckBoxHelp.pointAt( worldCheckBox );
            worldCheckBoxHelp.setArrowFillPaint( worldChildColor );
            helpPane.add( worldCheckBoxHelp );
            
            // Help for screen children...
            
            HelpBalloon screenCompositeHelp = new HelpBalloon( helpPane, "PComposite screen child", HelpBalloon.BOTTOM_CENTER, 40 );
            screenCompositeHelp.pointAt( screenComposite, canvas );
            helpPane.add( screenCompositeHelp );
            
            HelpBalloon screenTextHelp = new HelpBalloon( helpPane, "PText screen child", HelpBalloon.LEFT_CENTER, 40 );
            screenTextHelp.pointAt( screenText, canvas );
            helpPane.add( screenTextHelp );
            
            HelpBalloon screenPathHelp = new HelpBalloon( helpPane, "PPath screen child", HelpBalloon.RIGHT_CENTER, 40 );
            screenPathHelp.pointAt( screenPath, canvas );
            helpPane.add( screenPathHelp );
            
            HelpBalloon screenButtonHelp = new HelpBalloon( helpPane, "JButton in PSwing screen child", HelpBalloon.LEFT_CENTER, 40 );
            screenButtonHelp.pointAt( screenButton );
            helpPane.add( screenButtonHelp );
            
            HelpBalloon screenPSwingHelp = new HelpBalloon( helpPane, "PSwing screen child", HelpBalloon.RIGHT_CENTER, 40 );
            screenPSwingHelp.pointAt( screenPSwing, canvas );
            helpPane.add( screenPSwingHelp );
            
            // Help for world children...
            
            HelpBalloon worldCompositeHelp = new HelpBalloon( helpPane, "PComposite world child", HelpBalloon.BOTTOM_CENTER, 40 );
            worldCompositeHelp.pointAt( worldComposite, canvas );
            helpPane.add( worldCompositeHelp );
            
            HelpBalloon worldTextHelp = new HelpBalloon( helpPane, "PText world child", HelpBalloon.LEFT_CENTER, 40 );
            worldTextHelp.pointAt( worldText, canvas );
            helpPane.add( worldTextHelp );
            
            HelpBalloon worldPathHelp = new HelpBalloon( helpPane, "PPath world child", HelpBalloon.RIGHT_CENTER, 40 );
            worldPathHelp.pointAt( worldPath, canvas );
            helpPane.add( worldPathHelp );
            
            HelpBalloon worldButtonHelp = new HelpBalloon( helpPane, "JButton in PSwing world child", HelpBalloon.LEFT_CENTER, 40 );
            worldButtonHelp.pointAt( worldButton );
            helpPane.add( worldButtonHelp );
            
            HelpBalloon worldPSwingHelp = new HelpBalloon( helpPane, "PSwing world child", HelpBalloon.RIGHT_CENTER, 40 );
            worldPSwingHelp.pointAt( worldPSwing, canvas );
            helpPane.add( worldPSwingHelp );
        }

        /* Enables the help button and help menu item */
        public boolean hasHelp() {
            return true;
        }
    }
}
