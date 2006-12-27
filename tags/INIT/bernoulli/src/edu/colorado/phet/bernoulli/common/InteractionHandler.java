package edu.colorado.phet.bernoulli.common;

import java.awt.event.MouseEvent;

/**
 * User: Sam Reid
 * Date: Aug 22, 2003
 * Time: 10:55:22 AM
 * Copyright (c) Aug 22, 2003 by Sam Reid
 */
public interface InteractionHandler {
    boolean canHandleMousePress( MouseEvent event );

    void mousePressed( MouseEvent event );

    void mouseDragged( MouseEvent event );

    void mouseReleased( MouseEvent event );

    void mouseEntered( MouseEvent event );

    void mouseExited( MouseEvent event );
}
