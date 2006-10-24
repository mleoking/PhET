package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Oct 24, 2006
 * Time: 12:16:36 PM
 * Copyright (c) Oct 24, 2006 by Sam Reid
 */

public class BufferedPhetPCanvas extends PhetPCanvas {
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;

    public void paintComponent( Graphics g ) {
        Rectangle clip = g.getClipBounds();//todo is this in the correct coordinate frame?
//        System.out.println( "clip=" + clip + ", r=" + r );

        getBufferedGraphics().setClip( g.getClipBounds() );

        Graphics2D g2 = (Graphics2D)g;
//        System.out.println( "g2.getTransform() = " + g2.getTransform() );
        long t = System.currentTimeMillis();
        super.paintComponent( getBufferedGraphics() );
        long t2 = System.currentTimeMillis();
        g2.drawImage( bufferedImage, 0, 0, this );
        long t3 = System.currentTimeMillis();
//        super.paintComponent( g );
        long t4 = System.currentTimeMillis();
//        System.out.println( "Time to draw onto buffer: " + ( t2 - t ) + ", Time to draw buffer to screen: " + ( t3 - t2 ) + " toPaintDirectToScreen: " + ( t4 - t3 ) );
    }

    private Graphics2D getBufferedGraphics() {
        if( bufferedImage == null || bufferedImage.getWidth() != getWidth() || bufferedImage.getHeight() != getHeight() ) {
//            this.bufferedImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
            this.bufferedImage = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
            this.bufferedGraphics = bufferedImage.createGraphics();
        }
        return bufferedGraphics;
    }
}
