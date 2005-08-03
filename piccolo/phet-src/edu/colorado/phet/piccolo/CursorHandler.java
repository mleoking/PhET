/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 1, 2005
 * Time: 10:30:27 PM
 * Copyright (c) Aug 1, 2005 by Sam Reid
 */

public class CursorHandler extends PBasicInputEventHandler {
    private Cursor cursor;
    private Cursor defaultCursor;
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

    private Component getComponent( PInputEvent event ) {
        return event.getSourceSwingEvent().getComponent();
    }

    public void mouseExited( PInputEvent event ) {
        entered = false;
        if( !dragging ) {
            getComponent( event ).setCursor( defaultCursor );
        }
    }
}
