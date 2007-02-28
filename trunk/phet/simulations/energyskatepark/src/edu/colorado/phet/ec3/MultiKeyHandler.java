/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 12:47:23 PM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class MultiKeyHandler {
    private HashMap pressedKeys = new HashMap();
    private static final Object DUMMY_VALUE = new Object();

    public boolean isPressed( int vk_value ) {
        return pressedKeys.containsKey( new Integer( vk_value ) );
    }

    public void clear() {
        pressedKeys.clear();
    }

    public void keyPressed( KeyEvent e ) {
        pressedKeys.put( new Integer( e.getKeyCode() ), DUMMY_VALUE );
    }

    public void keyReleased( KeyEvent e ) {
        pressedKeys.remove( new Integer( e.getKeyCode() ) );
    }

    public void keyTyped( KeyEvent e ) {
    }
}
