/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;

/**
 * TestMouseHandling is a simple simulation that is useful for testing of mouse event handling.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class TestMouseHandling {

    /**
     * main
     */
    public static void main( String args[] ) throws IOException {
        TestMouseHandling test = new TestMouseHandling( args );
    }

    /**
     * TestMouseHandling creates the PhET application.
     */
    public TestMouseHandling( String[] args ) throws IOException {

        // Set up the application descriptor.
        String title = "TestMouseHandling";
        String description = "A test harness for mouse event handling";
        String version = "0.1";
        FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 300, 300 );
        AbstractClock clock = new SwingTimerClock( 1, 40 );
        Module module = new TestModule( clock );
        ApplicationModel appModel =
                new ApplicationModel( title, description, version, frameSetup, module, clock );
        
        // Create and start the application.
        PhetApplication app = new PhetApplication( appModel, args );
        app.startApplication();
    }

    /**
     * TestModule sets up a module with two draggable shapes.
     */
    private class TestModule extends Module {
        public TestModule( AbstractClock clock ) {
            super( "Test Module", clock );
            
            // Model
            BaseModel model = new BaseModel();
            setModel( model );
            
            // Apparatus Panel
            ApparatusPanel2 apparatusPanel = new ApparatusPanel2( clock );
            apparatusPanel.setBackground( Color.BLACK );
            setApparatusPanel( apparatusPanel );
            
            // Yellow rectangle
            PhetShapeGraphic yellowGraphic = new PhetShapeGraphic( apparatusPanel );
            yellowGraphic.setName( "yellow" );
            yellowGraphic.setShape( new Rectangle( 0, 0, 50, 50 ) );
            yellowGraphic.setPaint( Color.YELLOW );
            yellowGraphic.setLocation( 100, 100 );
            yellowGraphic.setCursorHand();
            yellowGraphic.addMouseInputListener( new MouseHandler( yellowGraphic ) );
            apparatusPanel.addGraphic( yellowGraphic, 1 );
            
            // Red rectangle
            final PhetShapeGraphic redGraphic = new PhetShapeGraphic( apparatusPanel );
            redGraphic.setName( "red" );
            redGraphic.setShape( new Rectangle( 0, 0, 50, 50 ) );
            redGraphic.setPaint( Color.RED );
            redGraphic.setLocation( 200, 100 );
            redGraphic.setCursorHand();
            redGraphic.addMouseInputListener( new MouseHandler( redGraphic ) );
            apparatusPanel.addGraphic( redGraphic, 2 );
        }
    }

    /**
     * MouseHandler handles mouse events for a specified graphic.
     * It prints a debugging message for each event received,
     * and moves the graphic during dragging.
     */
    private class MouseHandler extends MouseInputAdapter {

        private PhetGraphic _graphic;
        private Point _previousPoint;

        public MouseHandler( PhetGraphic graphic ) {
            super();
            _graphic = graphic;
            _previousPoint = new Point();
        }

        public void mouseClicked( MouseEvent event ) {
            System.out.println( "mouseClicked on " + _graphic.getName() );
        }

        public void mouseDragged( MouseEvent event ) {
            System.out.println( "mouseDragged on " + _graphic.getName() );
            int dx = event.getX() - _previousPoint.x;
            int dy = event.getY() - _previousPoint.y;
            int x = _graphic.getX() + dx;
            int y = _graphic.getY() + dy;
            _graphic.setLocation( x, y );
            _previousPoint.setLocation( event.getPoint() );
        }

        public void mouseEntered( MouseEvent event ) {
            System.out.println( "mouseEntered on " + _graphic.getName() );
        }

        public void mouseExited( MouseEvent event ) {
            System.out.println( "mouseExited on " + _graphic.getName() );
        }

        /* You may find it useful to comment out this "chatty" method. */
        public void mouseMoved( MouseEvent event ) {
            System.out.println( "mouseMoved on " + _graphic.getName() );
        }

        public void mousePressed( MouseEvent event ) {
            System.out.println( "mousePressed on " + _graphic.getName() );
            _previousPoint.setLocation( event.getPoint() );
        }

        public void mouseReleased( MouseEvent event ) {
            System.out.println( "mouseReleased on " + _graphic.getName() );
        }
    }
}
