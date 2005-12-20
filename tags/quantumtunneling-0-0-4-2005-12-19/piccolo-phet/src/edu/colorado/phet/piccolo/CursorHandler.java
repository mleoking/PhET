/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.event.MouseEvent;

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
 * None of the events received by this handler are marked as "handled",
 * so this hanlder can co-exist with other event handlers.  Other event
 * handlers should be careful not to mark events as "handled" that this
 * handler needs to see.
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

    /* cursor to change to */
    private Cursor cursor;
    /* cursor to restore */
    private Cursor restoreCursor;
    /* is the mouse pressed? */
    private boolean mousePressed;
    /* Is the mouse inside the node? */
    private boolean mouseInside;

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
        mousePressed = false;
        mouseInside = false;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        mouseInside = true;
        if( !mousePressed ) {
            restoreCursor = getComponent( event ).getCursor();
            getComponent( event ).setCursor( cursor );
        }
    }

    public void mouseExited( PInputEvent event ) {
        mouseInside = false;
        if( !mousePressed ) {
            getComponent( event ).setCursor( restoreCursor );
        }
    }

    public void mousePressed( PInputEvent event ) {
        mousePressed = true;
        if( !mouseInside ) {
            restoreCursor = getComponent( event ).getCursor();
            getComponent( event ).setCursor( cursor );
        }
    }

    public void mouseReleased( PInputEvent event ) {
        mousePressed = false;
        if( !mouseInside ) {
            getComponent( event ).setCursor( restoreCursor );
        }
    }

    /*
     * Workaround for a Piccolo problem.
     */
    private Component getComponent( PInputEvent aEvent ) {
        if( !( EventQueue.getCurrentEvent() instanceof MouseEvent ) ) {
            new Exception( "EventQueue.getCurrentEvent was not a MouseEvent, consider making PInputEvent.getSourceSwingEvent public.  Actual event: " + aEvent + ", class=" + aEvent.getClass().getName() ).printStackTrace();
        }
        MouseEvent mouseEvent = (MouseEvent)EventQueue.getCurrentEvent();
        return mouseEvent.getComponent();
    }
}