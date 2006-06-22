/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common_cck.view.graphics.mousecontrols;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Oct 9, 2003
 * Time: 12:53:40 AM
 * Copyright (c) Oct 9, 2003 by Sam Reid
 */
public class CursorControl implements MouseInputListener {
    private Cursor cursor;
    private Cursor exitCursor;

    public CursorControl() {
        this( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public CursorControl( Cursor cursor ) {
        this( cursor, Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }

    public CursorControl( Cursor cursor, Cursor exitCursor ) {
        this.cursor = cursor;
        this.exitCursor = exitCursor;
    }

    public void mouseClicked( MouseEvent e ) {
    }

    public void mousePressed( MouseEvent e ) {
    }

    public void mouseReleased( MouseEvent e ) {
    }

    public void mouseEntered( MouseEvent e ) {
        e.getComponent().setCursor( cursor );
    }

    public void mouseExited( MouseEvent e ) {
        e.getComponent().setCursor( exitCursor );
    }

    public void mouseDragged( MouseEvent e ) {
    }

    public void mouseMoved( MouseEvent e ) {
    }

}
