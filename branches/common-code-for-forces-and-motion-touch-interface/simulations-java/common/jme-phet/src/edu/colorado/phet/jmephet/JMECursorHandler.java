// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import java.awt.Cursor;

import javax.swing.JComponent;

import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Custom cursor handler for JME-related business (Piccolo cursor support looks broken?)
 * TODO: fix this
 */
public class JMECursorHandler extends PBasicInputEventHandler {
    private final Cursor cursor;

    public JMECursorHandler() {
        this( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public JMECursorHandler( Cursor cursor ) {
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
