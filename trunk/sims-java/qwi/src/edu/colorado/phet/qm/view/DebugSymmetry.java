package edu.colorado.phet.qm.view;

import edu.colorado.phet.qm.QWIModule;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Nov 1, 2006
 * Time: 12:28:42 AM
 * Copyright (c) Nov 1, 2006 by Sam Reid
 */

public class DebugSymmetry implements KeyListener {
    private QWIModule qwiPanel;

    public DebugSymmetry( QWIModule qwiPanel ) {
        this.qwiPanel = qwiPanel;
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_S ) {
            qwiPanel.debugSymmetry();
        }
    }

    public void keyTyped( KeyEvent e ) {
    }
}
