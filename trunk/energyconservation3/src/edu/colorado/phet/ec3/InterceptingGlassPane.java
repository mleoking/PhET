/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.piccolo.PhetPCanvas;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Sep 30, 2005
 * Time: 2:58:42 PM
 * Copyright (c) Sep 30, 2005 by Sam Reid
 */

public class InterceptingGlassPane extends JComponent {
    private PhetPCanvas phetPCanvas;

    public InterceptingGlassPane( final EC3Canvas phetPCanvas ) {
        this.phetPCanvas = phetPCanvas;
        setOpaque( false );
        addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                phetPCanvas.keyPressed( e );
            }

            public void keyReleased( KeyEvent e ) {
                phetPCanvas.keyReleased( e );
            }

            public void keyTyped( KeyEvent e ) {
                phetPCanvas.keyTyped( e );
            }
        } );
    }
}
