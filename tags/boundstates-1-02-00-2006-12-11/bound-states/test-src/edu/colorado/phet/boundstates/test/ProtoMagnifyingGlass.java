/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.test;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.event.ConstrainedDragHandler;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Prototype for the magnifying glass. Demonstrates node structure,
 * constrained dragging, and handling of mouse events that occur
 * in the lens.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ProtoMagnifyingGlass extends PComposite {

    private static final Dimension FRAME_SIZE = new Dimension( 800, 600 );
    private static final double LENS_DIAMETER = 100; // pixels
    private static final double BEZEL_WIDTH = 12; // pixels
    private static final double HANDLE_LENGTH = 65; // pixels
    private static final double HANDLE_WIDTH = HANDLE_LENGTH / 4; // pixels
    private static final double HANDLE_ARC_SIZE = 10; // pixels
    private static final double HANDLE_ROTATION = -20; // degrees;
    private static final Color BEZEL_COLOR = Color.GRAY;
    private static final Color HANDLE_COLOR = Color.ORANGE;

    private PPath _lensNode;
    private PPath _bezelNode;
    private PPath _handleNode;
    private MagnifyingGlassEventHandler _eventHandler;

    /*
     * Creates child nodes and wires up event handlers.
     */
    public ProtoMagnifyingGlass() {

        // Lens
        final double glassRadius = LENS_DIAMETER / 2;
        {
            Shape glassShape = new Ellipse2D.Double( -glassRadius, -glassRadius, LENS_DIAMETER, LENS_DIAMETER ); // x,y,w,h
            _lensNode = new PPath();
            _lensNode.setPathTo( glassShape );
            _lensNode.setPaint( Color.BLACK );
        }

        // Bezel
        {
            final double bezelDiameter = ( LENS_DIAMETER + BEZEL_WIDTH );
            Shape glassShape = new Ellipse2D.Double( -bezelDiameter / 2, -bezelDiameter / 2, bezelDiameter, bezelDiameter ); // x,y,w,h
            _bezelNode = new PPath();
            _bezelNode.setPathTo( glassShape ); // same shape as glass, but we'll stroke it instead of filling it
            _bezelNode.setPaint( BEZEL_COLOR );
        }

        // Handle
        {
            Shape handleShape = new RoundRectangle2D.Double( -HANDLE_WIDTH / 2, glassRadius, HANDLE_WIDTH, HANDLE_LENGTH, HANDLE_ARC_SIZE, HANDLE_ARC_SIZE );
            _handleNode = new PPath();
            _handleNode.setPathTo( handleShape );
            _handleNode.setPaint( HANDLE_COLOR );
            _handleNode.rotate( Math.toRadians( HANDLE_ROTATION ) );
        }

        addChild( _handleNode ); // bottom
        addChild( _bezelNode );
        addChild( _lensNode ); // top

        // Event handling
        {
            // Changes the cursor to a "hand"
            addInputEventListener( new CursorHandler() );

            // Handles mouse events
            _eventHandler = new MagnifyingGlassEventHandler();
            addInputEventListener( _eventHandler );
            
            // For constrained dragging, treat as a point at the center of the lens.
            _eventHandler.setTreatAsPointEnabled( true );
            final double bezelRadius = ( LENS_DIAMETER + BEZEL_WIDTH ) / 2;
            _eventHandler.setNodeCenter( bezelRadius, bezelRadius );
        }
    }

    // Sets the bounds used to constrain dragging.
    public void setDragBounds( Rectangle2D dragBounds ) {
        _eventHandler.setDragBounds( dragBounds );
        updateDisplay();
    }

    // Determines if point is inside the lens.
    private boolean isInLens( Point2D point ) {
        Rectangle2D lensBounds = localToGlobal( _lensNode.getFullBounds() );
        return lensBounds.contains( point );
    }

    // Update what's visible in the lens.
    private void updateDisplay() {
        System.out.println( "updateDisplay" );
    }

    // Select the eigenstate that is closest to point.
    private void selectEigenstate( Point2D point ) {
        System.out.println( "selectEigenstate at " + point );
    }

    // Hilites the eigenstate that is closest to point.
    private void hiliteEigenstate( Point2D point ) {
        System.out.println( "hiliteEigenstate at " + point );
    }

    /*
     * Dispatches mouse events to the proper handler method.
     * Superclass handles constrained dragging.
     */
    private class MagnifyingGlassEventHandler extends ConstrainedDragHandler {

        private boolean _dragging;

        public MagnifyingGlassEventHandler() {
            _dragging = false;
        }

        // Hilites an eigenstate when the mouse is moved.
        public void mouseMoved( PInputEvent e ) {
            super.mouseMoved( e );
            Point2D mousePosition = e.getPosition();
            if ( !_dragging && isInLens( mousePosition ) ) {
                hiliteEigenstate( mousePosition );
            }
        }

        // Selects an eigenstate when the mouse is pressed and released without dragging.
        public void mouseReleased( PInputEvent e ) {
            super.mouseReleased( e );
            if ( !_dragging ) {
                Point2D mousePosition = e.getPosition();
                if ( isInLens( mousePosition ) ) {
                    selectEigenstate( mousePosition );
                }
            }
            _dragging = false;
        }

        // Updates the display when the magnifying glass is dragged.
        public void mouseDragged( PInputEvent e ) {
            _dragging = true;
            super.mouseDragged( e );
            updateDisplay();
        }
    }
    
    /*
     * Test harness
     */
    public static void main( String[] args ) {

        // Drag Area
        PPath dragAreaNode = new PPath();
        dragAreaNode.setPaint( Color.BLACK );
        dragAreaNode.setPathTo( new Rectangle( 0, 0, 350, 300 ) );
        dragAreaNode.setPickable( false );
        dragAreaNode.setOffset( 200, 150 );

        // Magnifying Glass
        ProtoMagnifyingGlass magnifyingGlass = new ProtoMagnifyingGlass();
        magnifyingGlass.setOffset( 350, 350 );
        Rectangle2D dragBounds = dragAreaNode.localToGlobal( dragAreaNode.getBounds() );
        magnifyingGlass.setDragBounds( dragBounds );

        // Piccolo canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.addScreenChild( dragAreaNode );
        canvas.addScreenChild( magnifyingGlass );
        
        // JFrame
        JFrame frame = new JFrame( "Magnifying Glass Prototype" );
        frame.getContentPane().add( canvas );
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setSize( FRAME_SIZE );
        frame.setResizable( false );
        frame.show();
    }
}
