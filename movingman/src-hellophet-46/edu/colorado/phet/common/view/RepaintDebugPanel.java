/** Sam Reid*/
package edu.colorado.phet.common.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 25, 2004
 * Time: 10:09:04 AM
 * Copyright (c) Aug 25, 2004 by Sam Reid
 */
public class RepaintDebugPanel extends ApparatusPanel {
    public RepaintDebugPanel() {
        super.setDoubleBuffered( false );
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        super.repaint( tm, x, y, width, height );
    }

    public void repaint( Rectangle r ) {
        super.repaint( r );
    }

    public void repaint() {
        super.repaint();
    }

    public void repaint( long tm ) {
        super.repaint( tm );
    }

    public void repaint( int x, int y, int width, int height ) {
        super.repaint( x, y, width, height );
    }

    public void paintImmediately( int x, int y, int w, int h ) {
//        System.out.println( "PaintImm: = x=" + x + ", y=" + y + ", w=" + w + ", h=" + h );
        super.paintImmediately( x, y, w, h );
    }

    public void paintImmediately( final Rectangle r ) {
        paintImmediately( r.x, r.y, r.width, r.height );
//            super.paintImmediately( r );
//            repaint( r.x,r.y,r.width, r.height);
//            if( mode instanceof PlaybackMode ) {
//                RepaintDebugPanel.super.paintImmediately( r );
//            }
//            else {


//            SwingUtilities.invokeLater( new Runnable() {
//                public void run() {
//                    RepaintDebugPanel.super.paintImmediately( r );
//                }
//            } );
    }


//        }
    public void paintSoon( Rectangle union ) {
//            repaint( union );
        paintImmediately( union );
    }
}
