/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

/**
 * User: Sam Reid
 * Date: Jun 2, 2005
 * Time: 3:46:08 AM
 * Copyright (c) Jun 2, 2005 by Sam Reid
 */

public class Repaint extends SceneGraphMouseAdapter {
    public void mouseDragged( SceneGraphMouseEvent event ) {
        event.getMouseEvent().getComponent().repaint();
    }
}
