package edu.colorado.phet.cck.elements;

import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 30, 2003
 * Time: 12:09:55 AM
 * Copyright (c) Aug 30, 2003 by Sam Reid
 */
public interface InteractionHandler {
    boolean canHandleMousePress( MouseEvent event );

    void mousePressed( MouseEvent event );

    void mouseDragged( MouseEvent event );

    void mouseReleased( MouseEvent event );

    void mouseEntered( MouseEvent event );

    void mouseExited( MouseEvent event );
}
