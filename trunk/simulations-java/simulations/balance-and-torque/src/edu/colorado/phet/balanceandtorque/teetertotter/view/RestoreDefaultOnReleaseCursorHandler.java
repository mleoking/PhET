// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo event handler that shows a different Cursor when entering a PNode.
 * This version is a variation of the one that was in the common area as of
 * Aug 24 2010.  The main difference with this one is that the default cursor
 * is restored when the user releases the mouse.  This was needed for some
 * PNodes in this sim because the PNode could move away from point at which it
 * was released, leaving the cursor in an incorrect state.  This also fixes an
 * issue where the cursor would be incorrect if the user clicked on a PNode
 * and it cause an error dialog to be shown (see Unfuddle #2475 for more
 * information on this one).
 */
public class RestoreDefaultOnReleaseCursorHandler extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Some common cursors...
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

    private static final CursorManager manager = new CursorManager();

    //todo: should make 1 manager per JComponent?
    //the current implementation assumes state is global across all JPanels, may not work properly when moving from one JComponent to another

    /**
     * This class manages global state for the cursor.  Consider the following scenario:
     * 1. The cursor enters Node A
     * >The cursor becomes a hand
     * 2. The cursor is pressed and dragged out of Node A into Node B
     * >The cursor should remain a hand
     * <p/>
     * This is difficult (or impossible) to do without maintaining global state about which
     * nodes have been entered and dragged.
     */
    private static class CursorManager {

        public void mousePressed( PInputEvent event, Cursor cursor ) {
            Component component = (JComponent) event.getComponent();
            // Usually, the cursor would have been set when the mouseEntered
            // event was received.  However, it is possible that the user
            // pressed, released, and is now pressing again, and in that case
            // the altered cursor should be set.
            if ( component.getCursor() != cursor ) {
                component.setCursor( cursor );
            }
        }

        public void mouseEntered( PInputEvent event, Cursor cursor ) {
            if ( !event.isLeftMouseButton() ) {
                ( (JComponent) event.getComponent() ).setCursor( cursor );
            }
        }

        public void mouseReleased( JComponent component ) {
            component.setCursor( Cursor.getDefaultCursor() );
        }

        public void mouseExited( PInputEvent event ) {
            if ( !event.isLeftMouseButton() ) {
                ( (JComponent) event.getComponent() ).setCursor( Cursor.getDefaultCursor() );
            }
        }
    }

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Cursor cursor;  // cursor to change to

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Creates a handler that uses the HAND cursor.
     */
    public RestoreDefaultOnReleaseCursorHandler() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public RestoreDefaultOnReleaseCursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public RestoreDefaultOnReleaseCursorHandler( Cursor cursor ) {
        this.cursor = cursor;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        manager.mouseEntered( event, cursor );
    }

    public void mousePressed( PInputEvent event ) {
        manager.mousePressed( event, cursor );
    }

    public void mouseReleased( PInputEvent event ) {
        manager.mouseReleased( (JComponent) event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        manager.mouseExited( event );
    }

    //----------------------------------------------------------------------------
    // test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath redNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        redNode.setPaint( Color.RED );
        redNode.setOffset( 50, 50 );
        redNode.addInputEventListener( new RestoreDefaultOnReleaseCursorHandler( CROSSHAIR ) );
        canvas.getLayer().addChild( redNode );

        PPath greenNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + 50, redNode.getFullBoundsReference().getY() );
        greenNode.addInputEventListener( new RestoreDefaultOnReleaseCursorHandler( HAND ) );
        canvas.getLayer().addChild( greenNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}