package edu.colorado.phet.cck3;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:19:42 AM
 * Copyright (c) Jul 7, 2006 by Sam Reid
 */
public class SimpleKeyEvent implements KeyListener {
    int keycode;

    protected SimpleKeyEvent( int keycode ) {
        this.keycode = keycode;
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if( e.getKeyCode() == keycode ) {
            invoke();
        }
    }

    public void invoke() {
    }

    public void keyTyped( KeyEvent e ) {
    }
}
