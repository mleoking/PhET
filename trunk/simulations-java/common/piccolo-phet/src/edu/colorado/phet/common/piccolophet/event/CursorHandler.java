/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;

import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * CursorHandler handles cursor behavior for interactive PNodes.
 * It changes the mouse to a different cursor on entrance,
 * and restores the cursor on exit.  If the mouse was pressed
 * (to drag, for example) then the cursor is not restored until
 * after the mouse has been released.
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
    private boolean cursorIsOnStack; // did we push the cursor onto the stack?

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
        cursorIsOnStack = false;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        pushCursor( event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        popCursor( event.getComponent() );
    }

    //----------------------------------------------------------------------------
    // Cursor push/pop
    //----------------------------------------------------------------------------

    private void pushCursor( PComponent component ) {
        if ( !cursorIsOnStack ) {
            component.pushCursor( cursor );
            cursorIsOnStack = true;
        }
    }

    private void popCursor( PComponent component ) {
        if ( cursorIsOnStack ) {
            /* 
             * WORKAROUND:
             * 
             * Exception handling for case in which the Piccolo cursor stack is popped too many times.
             * One case (but not the only case) where this can happen is with
             * PNode in PCanvas embedded in PSwing inside PCanvas in JFrame.
             * 
             * NOTE: This should no longer be necessary because we are keeping track of 
             * the stack state with the cursorOnStack variable. But I've left it in here
             * to remind us that it's a problem that should be fixed in Piccolo.
             */
            try {
                component.popCursor();
            }
            catch( ArrayIndexOutOfBoundsException e ) {
                System.err.println( "CursorHandler.popCursor attempted to pop an empty cursor stack" );
                // this is a well-known (but benign) problem, so don't print the stack trace
            }
            cursorIsOnStack = false;
        }
    }
}