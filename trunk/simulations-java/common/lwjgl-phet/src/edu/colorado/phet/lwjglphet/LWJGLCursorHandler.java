// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Custom cursor handler for LWJGL-related business (Piccolo cursor support looks broken?)
 * TODO: fix this (or get rid of this, since it was copied from JME)
 */
public class LWJGLCursorHandler extends PBasicInputEventHandler {
    private final Cursor cursor;

    public LWJGLCursorHandler() {
        this( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public LWJGLCursorHandler( Cursor cursor ) {
        this.cursor = cursor;
    }

    /**
     * Override this to change when the cursor is modified
     *
     * @return
     */
    public boolean isEnabled() {
        return true;
    }

    @Override public void mouseEntered( PInputEvent event ) {
        if ( isEnabled() ) {
            ( (JComponent) event.getComponent() ).setCursor( cursor );
        }
    }

    @Override public void mouseExited( PInputEvent event ) {
        ( (JComponent) event.getComponent() ).setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }
}
