/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Jun 30, 2004
 * Time: 1:09:58 PM
 * Copyright (c) Jun 30, 2004 by Sam Reid
 */
public class CCKKeyListener implements KeyListener {
    CCK3Module cck;
    private RepaintDebugGraphic colorG;

    public CCKKeyListener( CCK3Module cck, RepaintDebugGraphic colorG ) {
        this.cck = cck;
        this.colorG = colorG;
    }

    public void keyPressed( KeyEvent e ) {
    }

    public void keyReleased( KeyEvent e ) {
        if( e.getKeyCode() == KeyEvent.VK_UP ) {
            colorG.setActive( true );
        }
        else if( e.getKeyCode() == KeyEvent.VK_DOWN ) {
            colorG.setActive( false );
        }
        else if( e.getKeyCode() == KeyEvent.VK_BACK_SPACE || e.getKeyCode() == KeyEvent.VK_DELETE ) {
            cck.deleteSelection();
            cck.desolderSelection();
        }
        else if( e.getKeyCode() == KeyEvent.VK_A && e.isControlDown() ) {
            cck.selectAll();
        }
        else if( e.getKeyCode() == KeyEvent.VK_ESCAPE ) {
            boolean orig = cck.getCircuitGraphic().isLifelike();
            for( int i = 0; i < 100; i++ ) {
                cck.setLifelike( !orig );
                cck.setLifelike( orig );
            }
        }
        else if( e.getKeyCode() == KeyEvent.VK_SPACE ) {
            Frame[] win = JFrame.getFrames();
            for( int i = 0; i < win.length; i++ ) {
                Frame frame = win[i];
                frame.repaint();
            }
        }
    }

    public void keyTyped( KeyEvent e ) {
    }


}
