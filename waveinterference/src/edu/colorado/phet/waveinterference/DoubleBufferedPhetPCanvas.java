/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:28:18 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class DoubleBufferedPhetPCanvas extends PhetPCanvas {
    private BufferedImage buffer;
    private boolean pressed;

    public DoubleBufferedPhetPCanvas() {
        addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
                pressed = true;
            }

            public void mouseReleased( MouseEvent e ) {
                pressed = false;
            }
        } );
    }

    public void paintComponent( Graphics g ) {
//        if( pressed ) {
//            paintNormal( g );
//        }
//        else {
        paintBuffered( g );
//        }
    }

    protected void paintNormal( Graphics g ) {
        super.paintComponent( g );
    }

    protected void superPaint( Graphics g ) {
        super.paintComponent( g );
    }

    protected void paintBuffered( final Graphics g ) {
        synchronizeImage();
        Graphics2D g2 = buffer.createGraphics();
//        System.out.println( "g.getClip() = " + g.getClip() );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );//to overcome the slow default in 1.5
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setClip( g.getClip() );
        super.paintComponent( g2 );
        g.drawImage( buffer, 0, 0, null );
    }

    private void synchronizeImage() {
        if( buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight() ) {
            buffer = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        }
    }

}
