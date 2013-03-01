// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.event;

import java.awt.Cursor;

import javax.swing.JComponent;

import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Piccolo event handler that shows a different Cursor when entering a PNode
 */
public class DynamicCursorHandler extends CursorHandler {
    private JComponent mouseOverComponent = null; // the component that the mouse is over

    public DynamicCursorHandler() {
        this( HAND );
    }

    /**
     * Creates a handler using one of the cursor types predefined by Cursor.
     *
     * @param cursorType
     */
    public DynamicCursorHandler( int cursorType ) {
        this( Cursor.getPredefinedCursor( cursorType ) );
    }

    /**
     * Creates a handler using a specified cursor.
     *
     * @param cursor
     */
    public DynamicCursorHandler( Cursor cursor ) {
        super( cursor );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    // Sets a different cursor.
    // If the mouse is over a component, immediately show the new cursor on the component.
    public void setCursor( int cursor ) {
        this.cursor = Cursor.getPredefinedCursor( cursor );
        if ( mouseOverComponent != null ) {
            //If you don't change the manager.lastEntered, it will toggle back during the mouse release
            manager.lastEntered = this.cursor;
            mouseOverComponent.setCursor( this.cursor );
        }
    }

    //----------------------------------------------------------------------------
    // PBasicInputEventHandler overrides
    //----------------------------------------------------------------------------

    public void mouseEntered( PInputEvent event ) {
        super.mouseEntered( event );
        mouseOverComponent = (JComponent) event.getComponent();
    }

    public void mouseExited( PInputEvent event ) {
        super.mouseExited( event );
        mouseOverComponent = null;
    }
}