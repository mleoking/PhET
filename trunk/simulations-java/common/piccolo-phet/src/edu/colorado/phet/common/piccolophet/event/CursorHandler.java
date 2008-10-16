/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.Cursor;

/**
 * CursorHandler handles cursor behavior for interactive PNodes.
 * It changes the cursor on mouseEnter, and restores the cursor mouseExit.
 * <p/>
 * The default cursor is the "hand" cursor, but you can specify your
 * own cursor in one of the constructors.
 * <p/>
 * None of the events received by this handler are marked as "handled".
 */
public class CursorHandler extends CursorHandler2{

    public CursorHandler() {
    }

    public CursorHandler( int cursorType ) {
        super( cursorType );
    }

    public CursorHandler( Cursor cursor ) {
        super( cursor );
    }
}