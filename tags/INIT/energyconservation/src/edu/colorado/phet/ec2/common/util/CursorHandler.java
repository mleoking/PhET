/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.ec2.common.util;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Jul 27, 2003
 * Time: 3:24:47 PM
 * Copyright (c) Jul 27, 2003 by Sam Reid
 */
public class CursorHandler {

    static Cursor handCursor = new Cursor( Cursor.HAND_CURSOR );
    static Cursor defaultCursor = new Cursor( Cursor.DEFAULT_CURSOR );

    public static void mouseEntered( MouseEvent event ) {
        event.getComponent().setCursor( handCursor );
    }

    public static void mouseExited( MouseEvent event ) {
        event.getComponent().setCursor( defaultCursor );
    }

    public static void setCursor( int cursor, Component c ) {
        c.setCursor( new Cursor( cursor ) );
    }

    public static void setDefaultCursor( Component c ) {
        c.setCursor( defaultCursor );
    }

    public static void setHandCursor( Component c ) {
        c.setCursor( handCursor );
    }

}
