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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;

import javax.swing.*;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.model.clock.TimingStrategy;
import edu.colorado.phet.common.view.ControlPanel;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.piccolo.help.PGlassPane;
import edu.umd.cs.piccolo.nodes.PPath;


/**
 * TestPGlassPane tests PGlassPane.
 * <p>
 * Pressing the Help button will display a PGlassPane that puts 
 * colored circles at the upper-left corner of certain Swing controls.
 * See method markComponents for details.
 * <p>
 * JButtons and JSliders in the control panel have a hand cursor,
 * so that you can verify that the cursor is being set correctly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPGlassPane extends PhetApplication {

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick
    
    // Cursors
    private static final  Cursor HAND_CURSOR = new Cursor( Cursor.HAND_CURSOR );

    /* Test harness */
    public static void main( final String[] args ) {
        try {
            TestPGlassPane app = new TestPGlassPane( args );
            app.startApplication();
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /* Application */
    public TestPGlassPane( String[] args ) throws InterruptedException {
        super( args, "TestPGlassPane", "test of PGlassPane", "0.1", new FrameSetup.CenteredWithSize( 1024, 768 ) );

        Module module1 = new TestModule( "Module 1", new Color( 255, 208, 252 ) );
        addModule( module1 );

        Module module2 = new TestModule( "Module 2", new Color( 208, 255, 252 ) );
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

        public TestModule( String title, Color canvasColor ) {
            super( title, new TestClock(), true /* startsPaused */ );

            // Simulation panel (aka, play area) -----------------------------------
            PhetPCanvas canvas = new PhetPCanvas();
            setSimulationPanel( canvas );
            canvas.setBackground( canvasColor );
            
            // Control panel -----------------------------------
               
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            
            ActionListener actionListener = new ActionListener() {
               public void actionPerformed( ActionEvent event ) {
                   System.out.println( "actionPerformed: " + event.getActionCommand() );
               }
            };
            
            JButton button1 = new JButton( "button1" );
            button1.setCursor( HAND_CURSOR );
            button1.setActionCommand( "button1" );
            button1.addActionListener( actionListener  );

            JButton button2 = new JButton( "button2" );
            button2.setCursor( HAND_CURSOR );
            button2.setActionCommand( "button2" );
            button2.addActionListener( actionListener );
            
            JSlider slider1 = new JSlider();
            slider1.setCursor( HAND_CURSOR );

            JSlider slider2 = new JSlider();
            slider2.setCursor( HAND_CURSOR );
 
            // controls embedded in a Box
            Box box = new Box( BoxLayout.Y_AXIS );
            { 
                JButton button3 = new JButton( "button3" );
                button3.setCursor( HAND_CURSOR );
                button3.setActionCommand( "button3" );
                button3.addActionListener( actionListener );

                JSlider slider3 = new JSlider();
                slider3.setCursor( HAND_CURSOR );
                
                box.add( button3 );
                box.add( new JCheckBox( "checkBox3" ) );
                box.add( slider3 );
            }
            
            controlPanel.addControl( button1 );
            controlPanel.addControl( button2 );
            controlPanel.addSeparator();
            controlPanel.addControl( new JCheckBox( "checkBox1" ) );
            controlPanel.addControl( new JCheckBox( "checkBox2" ) );
            controlPanel.addSeparator();
            controlPanel.addControlFullWidth( slider1 );
            controlPanel.addControlFullWidth( slider2 );
            controlPanel.addSeparator();
            controlPanel.addControlFullWidth( box );

            // Help (glass pane)  -----------------------------------
            
            JFrame frame = PhetApplication.instance().getPhetFrame();
            JComponent glassPane = new MyGlassPane( frame );
            setHelpPane( glassPane );
        }
        
        public boolean hasHelp() {
            return true;
        }

        /**
         * Extension of PGlassPane that draws circles at the upper-left corner 
         * of various JComponents.  See markComponents for details.
         * 
         * MyGlassPane
         *
         * @author Chris Malley (cmalley@pixelzoom.com)
         * @version $Revision$
         */
        private class MyGlassPane extends PGlassPane {

            public MyGlassPane( JFrame parentFrame ) {
                super( parentFrame );
                
                // Periodically mark certain components with colored circles...
                Timer timer = new Timer( 500, new ActionListener() {
                    public void actionPerformed( ActionEvent e ) {
                        getLayer().removeAllChildren();
                        markComponents( getParentFrame().getLayeredPane() );
                    }
                } );
                timer.start();
            }

            /*
             * Recursively navigate through the Swing component hierachy.
             * For certain component types, draw a circle at their upper-left corner 
             * using these colors:
             * 
             * RED   = AbstractButton
             * BLUE  = JCheckBox
             * GREEN = JSlider
             * 
             * @param container
             */
            private void markComponents( Container container ) {
                for ( int i = 0; i < container.getComponentCount(); i++ ) {
                    Component c = container.getComponent( i );
                    if ( c.isVisible() ) {
                        if ( c instanceof AbstractButton ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.RED );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof JCheckBox ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.BLUE );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof JSlider ) {
                            Point loc = SwingUtilities.convertPoint( c.getParent(), c.getLocation(), this );
                            PPath path = new PPath( new Ellipse2D.Double( -5, -5, 10, 10 ) );
                            path.setPaint( Color.GREEN );
                            path.setOffset( loc );
                            getLayer().addChild( path );
                        }
                        else if ( c instanceof Container ) {
                            markComponents( (Container) c );
                        }
                    }
                }
            }
        }
    }
}
