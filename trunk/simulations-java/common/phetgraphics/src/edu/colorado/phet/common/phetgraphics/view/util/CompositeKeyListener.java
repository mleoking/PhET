// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetgraphics.view.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Dec 24, 2004
 * Time: 5:42:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class CompositeKeyListener implements KeyListener {
    private ArrayList listeners = new ArrayList();

    public void addKeyListener( KeyListener keyListener ) {
        listeners.add( keyListener );
    }

    public int numKeyListeners() {
        return listeners.size();
    }

    public void removeKeyListener( KeyListener keyListener ) {
        listeners.remove( keyListener );
    }

    public void keyTyped( KeyEvent e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            KeyListener keyListener = (KeyListener) listeners.get( i );
            keyListener.keyTyped( e );
        }
    }

    public void keyPressed( KeyEvent e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            KeyListener keyListener = (KeyListener) listeners.get( i );
            keyListener.keyPressed( e );
        }
    }

    public void keyReleased( KeyEvent e ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            KeyListener keyListener = (KeyListener) listeners.get( i );
            keyListener.keyReleased( e );
        }
    }
}
