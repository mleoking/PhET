/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Changes the mouse to a different cursor on entrance.
 * //todo should this save the state of the mouse before entrance, for restore on exit?
 */

public class CursorHandler extends PBasicInputEventHandler {
    private Cursor cursor;
    private Cursor defaultCursor;//the cursor to return to on exit.
    public static final Cursor HAND = Cursor.getPredefinedCursor( Cursor.HAND_CURSOR );
    public static final Cursor DEFAULT = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor CROSSHAIR = Cursor.getPredefinedCursor( Cursor.CROSSHAIR_CURSOR );
    private boolean dragging = false;
    private boolean entered = false;

    public CursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    public CursorHandler( Cursor cursor ) {
        this( cursor, Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public CursorHandler( Cursor cursor, Cursor defaultCursor ) {
        this.cursor = cursor;
        this.defaultCursor = defaultCursor;
    }

    public void mouseEntered( PInputEvent event ) {
        entered = true;
        getComponent( event ).setCursor( cursor );
    }

    public void mouseDragged( PInputEvent event ) {
        dragging = true;
        getComponent( event ).setCursor( cursor );
    }

    public void mouseReleased( PInputEvent event ) {
        dragging = false;
        if( !entered ) {
            getComponent( event ).setCursor( defaultCursor );
        }
        super.mouseReleased( event );
    }

    private Component getComponent( PInputEvent aEvent ) {
        if( !( EventQueue.getCurrentEvent() instanceof MouseEvent ) ) {
            new Exception("EventQueue.getCurrentEvent was not a MouseEvent, consider making PInputEvent.getSourceSwingEvent public.  Actual event: "+aEvent+", class="+aEvent.getClass().getName()).printStackTrace( );
        }
        if( EventQueue.getCurrentEvent() instanceof MouseEvent ) {
            MouseEvent mouseEvent = (MouseEvent)EventQueue.getCurrentEvent();
            return mouseEvent.getComponent();
        }
        else {
            throw new RuntimeException( "No component for event." );
        }
    }

    public void mouseExited( PInputEvent event ) {
        entered = false;
        if( !dragging ) {
            getComponent( event ).setCursor( defaultCursor );
        }
    }
}
