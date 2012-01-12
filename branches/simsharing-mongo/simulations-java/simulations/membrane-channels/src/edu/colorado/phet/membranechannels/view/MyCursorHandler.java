// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Piccolo event handler that shows a different Cursor when entering a PNode.
 * This version is a variant of the CursorHandler class that was in the
 * common code as of Aug 24, 2010.  The primary difference for this one is
 * that it uses the information available in the PInputEvent rather than
 * maintaining state information about the pressed state of the mouse or about
 * previous settings of the cursor.
 * 
 * This may be propagated into the common code at some point, see Unfuddle
 * #2457 for background information.
 */
public class MyCursorHandler extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Some common cursors...
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

    private static final CursorManager manager = new CursorManager();

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

        private boolean mouseOverPnode;

        private CursorManager() {
        }

        public void mouseEntered( PInputEvent event, Cursor cursor ) {
            if ( !event.isLeftMouseButton() ) {
                ((JComponent)event.getComponent()).setCursor( cursor );
            }
            mouseOverPnode = true;
        }

        public void mouseReleased( PInputEvent event, Cursor cursor ) {
            JComponent component = ((JComponent)event.getComponent());
            if ( mouseOverPnode ) {
                component.setCursor( cursor );
            }else{
                component.setCursor( Cursor.getDefaultCursor() );
            }
        }

        public void mouseExited( PInputEvent event ) {
            if ( !event.isLeftMouseButton() ) {
                ((JComponent)event.getComponent()).setCursor( Cursor.getDefaultCursor() );
            }
            mouseOverPnode = false;
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
    public MyCursorHandler() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public MyCursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public MyCursorHandler( Cursor cursor ) {
        this.cursor = cursor;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        manager.mouseEntered( event, cursor );
    }

    public void mouseReleased( PInputEvent event ) {
        manager.mouseReleased( event, cursor );
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
        redNode.addInputEventListener( new MyCursorHandler( CROSSHAIR ) );
        canvas.getLayer().addChild( redNode );

        PPath greenNode = new PPath( new Rectangle2D.Double( 0, 0, 100, 100 ) );
        greenNode.setPaint( Color.GREEN );
        greenNode.setOffset( redNode.getFullBoundsReference().getMaxX() + 50, redNode.getFullBoundsReference().getY() );
        greenNode.addInputEventListener( new MyCursorHandler( HAND ) );
        canvas.getLayer().addChild( greenNode );

        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.setSize( 500, 300 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}