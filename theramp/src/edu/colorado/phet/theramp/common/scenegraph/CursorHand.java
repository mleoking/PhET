/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 10:44:43 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class CursorHand extends SceneGraphMouseAdapter {
    public void mouseEntered( SceneGraphMouseEvent event ) {
        event.getMouseEvent().getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        event.getMouseEvent().getComponent().setCursor( Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR ) );
    }
}
