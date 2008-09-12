/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.event;

import java.awt.*;

import javax.swing.*;

import edu.umd.cs.piccolo.PComponent;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Feasibility test for improved cursor handling
 */
public class CursorHandler2 extends PBasicInputEventHandler {

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
    private boolean mousePressed = false;
    private boolean mouseEntered = false;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Creates a handler that uses the HAND cursor.
     */
    public CursorHandler2() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public CursorHandler2( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public CursorHandler2( Cursor cursor ) {
        this.cursor = cursor;
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        this.mouseEntered = true;
        updateComponent( event.getComponent() );
    }

    public void mousePressed( PInputEvent event ) {
        this.mousePressed = true;
        updateComponent( event.getComponent() );
    }

    public void mouseReleased( PInputEvent event ) {
        this.mousePressed = false;
        updateComponent( event.getComponent() );
    }

    public void mouseExited( PInputEvent event ) {
        this.mouseEntered = false;
        updateComponent( event.getComponent() );
    }

    private void updateComponent( PComponent component ) {
        if ( component instanceof JComponent ) {
            JComponent jc = (JComponent) component;
            jc.setCursor( getCursorState() );
        }
        else {
            throw new RuntimeException( "Only supported for Swing components");
        }
    }

    private Cursor getCursorState() {
        if ( !mouseEntered && !mousePressed ) {
            return DEFAULT;
        }
        else if ( mouseEntered && !mousePressed ) {
            return cursor;
        }
        else if ( !mouseEntered && mousePressed ) {
            return cursor;
        }
        else {//if ( mouseEntered && mousePressed ) {
            return cursor;
        }
    }
}