/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo event handler that shows a different Cursor when entering a PNode
 */
public class CursorHandler extends PBasicInputEventHandler {

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
     *      >The cursor becomes a hand
     * 2. The cursor is pressed and dragged out of Node A into Node B
     *      >The cursor should remain a hand
     *
     * This is difficult (or impossible) to do without maintaining global state about which
     * nodes have been entered and dragged.
     */
    private static class CursorManager {

        private Cursor lastEntered;
        boolean pressed = false;

        private CursorManager() {
        }

        public void mouseEntered( JComponent component, Cursor cursor ) {
            if ( !pressed ) {
                component.setCursor( cursor );
            }
            lastEntered=cursor;
        }

        public void mousePressed() {
            pressed = true;
        }

        public void mouseReleased( JComponent component ) {
            if ( lastEntered != null ) {
                component.setCursor( lastEntered );
            }else{
                component.setCursor( Cursor.getDefaultCursor() );
            }

            pressed = false;
        }

        public void mouseExited( JComponent component ) {
            if ( !pressed ) {
                component.setCursor( Cursor.getDefaultCursor() );
            }
            lastEntered = null;
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
    public CursorHandler() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public CursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public CursorHandler( Cursor cursor ) {
        this.cursor = cursor;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        manager.mouseEntered( (JComponent) event.getComponent(), cursor );
    }

    public void mousePressed( PInputEvent event ) {
        manager.mousePressed();
    }

    public void mouseReleased( PInputEvent event ) {
        manager.mouseReleased( (JComponent) event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        manager.mouseExited( (JComponent) event.getComponent() );
    }

    //----------------------------------------------------------------------------
    // test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {

        PhetPCanvas canvas = new PhetPCanvas();

        PPath redNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        redNode.setPaint( Color.RED );
        redNode.setOffset( 50, 50 );
        redNode.addInputEventListener( new CursorHandler( CROSSHAIR ) );
        canvas.getLayer().addChild( redNode );

        PPath greenNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + 50, redNode.getFullBoundsReference().getY() );
        greenNode.addInputEventListener( new CursorHandler( HAND ) );
        canvas.getLayer().addChild( greenNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}