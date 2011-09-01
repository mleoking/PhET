// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view;

import java.awt.event.KeyEvent;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 12:47:23 PM
 */

public class MultiKeyHandler {
    private final HashMap pressedKeys = new HashMap();
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
