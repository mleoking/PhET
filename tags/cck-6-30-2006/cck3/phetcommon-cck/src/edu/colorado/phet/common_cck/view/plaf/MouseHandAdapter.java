/** Sam Reid*/
package edu.colorado.phet.common_cck.view.plaf;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Apr 3, 2004
 * Time: 11:42:01 PM
 * Copyright (c) Apr 3, 2004 by Sam Reid
 */
public class MouseHandAdapter extends MouseAdapter {
    public void mouseEntered( MouseEvent e ) {
        e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( MouseEvent e ) {
        e.getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }
}
