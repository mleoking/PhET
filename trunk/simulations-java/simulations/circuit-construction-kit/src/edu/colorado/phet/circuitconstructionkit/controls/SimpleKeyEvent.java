// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Jul 7, 2006
 * Time: 9:19:42 AM
 */
public class SimpleKeyEvent implements KeyListener {
    int keycode;

    public SimpleKeyEvent(int keycode) {
        this.keycode = keycode;
    }

    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == keycode) {
            invoke();
        }
    }

    public void invoke() {
    }

    public void keyTyped(KeyEvent e) {
    }
}
