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
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
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
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestPGlassPane extends PhetApplication {

    // Clock parameters
    private static final int CLOCK_RATE = 25; // wall time: frames per second
    private static final double MODEL_RATE = 1; // model time: dt per clock tick

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

            PhetPCanvas canvas = new PhetPCanvas();
            canvas.setBackground( canvasColor );
            setSimulationPanel( canvas );
            
            ControlPanel controlPanel = new ControlPanel( this );
            setControlPanel( controlPanel );
            controlPanel.addControl( new JLabel( "label1" ) );
            controlPanel.addControl( new JLabel( "label2" ) );
            controlPanel.addControl( new JButton( "button1" ) );
            controlPanel.addControl( new JButton( "button2" ) );
            controlPanel.addControl( new JCheckBox( "checkBox1" ) );
            controlPanel.addControl( new JCheckBox( "checkBox2" ) );
            controlPanel.addControlFullWidth( new JSlider() );
            controlPanel.addControlFullWidth( new JSlider() );
            Box box = new Box( BoxLayout.Y_AXIS );
            box.add( new JLabel( "label3" ) );
            box.add( new JButton( "button3" ) );
            box.add( new JCheckBox( "checkBox3" ) );
            box.add( new JSlider() );
            controlPanel.addControlFullWidth( box );

            JFrame frame = PhetApplication.instance().getPhetFrame();
            JComponent glassPane = new MyGlassPane( frame );
            setHelpPane( glassPane );
        }
        
        public boolean hasHelp() {
            return true;
        }

        private class MyGlassPane extends PGlassPane {

            public MyGlassPane( JFrame parentFrame ) {
                super( parentFrame );
                // Periodically mark certain components with colored circles...
                Timer timer = new Timer( 500, new ActionListener() {

                    public void actionPerformed( ActionEvent e ) {
                        getLayer().removeAllChildren();
                        markComponents( getParentFrame().getContentPane() );
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
