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
        repaint( event );
    }

    private void repaint( SceneGraphMouseEvent event ) {
        event.getMouseEvent().getComponent().repaint();
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        repaint( event );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        repaint( event );
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        repaint( event );
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        repaint( event );
    }
}
