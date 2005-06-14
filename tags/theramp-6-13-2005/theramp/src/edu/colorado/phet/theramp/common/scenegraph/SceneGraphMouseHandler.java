/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 10:43:12 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public interface SceneGraphMouseHandler {

    void mouseEntered( SceneGraphMouseEvent event );

    void mouseExited( SceneGraphMouseEvent event );

    void mouseDragged( SceneGraphMouseEvent event );

    void mousePressed( SceneGraphMouseEvent event );

    void mouseReleased( SceneGraphMouseEvent event );

    void mouseClicked( SceneGraphMouseEvent event );
}
