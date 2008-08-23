/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.view.graphics.mousecontrols;

import java.awt.*;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputListener;

/**
 * CursorControl
 *
 * @author ?
 * @version $Revision$
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
