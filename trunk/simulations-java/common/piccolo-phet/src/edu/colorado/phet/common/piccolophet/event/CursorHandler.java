/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.Cursor;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * CursorHandler handles cursor behavior for interactive PNodes.
 * It changes the cursor on mouseEnter, and restores the cursor mouseExit.
 * <p/>
 * The default cursor is the "hand" cursor, but you can specify your
 * own cursor in one of the constructors.
 * <p/>
 * None of the events received by this handler are marked as "handled".
 */
public class CursorHandler extends PBasicInputEventHandler {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Some common cursors...
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );

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
        event.getComponent().pushCursor( cursor );
    }

    public void mouseExited( PInputEvent event ) {
        /* 
         * WORKAROUND:
         * Exception handling for case in which the Piccolo cursor stack is popped too many times.
         * One case (but not the only case) where this can happen is with
         * PNode in PCanvas embedded in PSwing inside PCanvas in JFrame.
         */
        try {
            event.getComponent().popCursor();
        }
        catch( ArrayIndexOutOfBoundsException e ) {
            System.err.println( "CursorHandler.popCursor attempted to pop an empty cursor stack" );
            // this is a well-known (but benign) problem, so don't print the stack trace
        }
    }
}