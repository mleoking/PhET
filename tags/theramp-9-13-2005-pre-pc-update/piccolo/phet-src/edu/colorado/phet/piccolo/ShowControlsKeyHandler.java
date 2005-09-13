/* Copyright 2004, Sam Reid */
package edu.colorado.phet.piccolo;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Sep 13, 2005
 * Time: 2:56:56 AM
 * Copyright (c) Sep 13, 2005 by Sam Reid
 */

public class ShowControlsKeyHandler implements KeyListener {
    private PhetPCanvas phetPCanvas;

    public ShowControlsKeyHandler( PhetPCanvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
    }

    public void keyPressed( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_INSERT ) {
            toggle();
        }
    }

    private void toggle() {
        phetPCanvas.getPhetRootNode().setContainsScreenNode( !phetPCanvas.getPhetRootNode().containsScreenNode() );
    }

    public void keyReleased( KeyEvent e ) {
    }

    public void keyTyped( KeyEvent e ) {
    }
}
