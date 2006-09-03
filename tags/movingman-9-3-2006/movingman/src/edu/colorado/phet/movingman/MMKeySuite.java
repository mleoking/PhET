/** Sam Reid*/
package edu.colorado.phet.movingman;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Oct 31, 2004
 * Time: 12:41:21 PM
 * Copyright (c) Oct 31, 2004 by Sam Reid
 */
public class MMKeySuite implements KeyListener {
    MovingManModule module;

    public MMKeySuite( MovingManModule module ) {
        this.module = module;
    }

    public void keyTyped( KeyEvent e ) {
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
            toggleRunning();
        }
    }

    private void toggleRunning() {
        boolean paused = module.isPaused();
        module.setPaused( !paused );
    }
}
