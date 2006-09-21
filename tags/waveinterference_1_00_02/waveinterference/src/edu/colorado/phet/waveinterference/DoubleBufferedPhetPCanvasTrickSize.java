/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.piccolo.PhetPCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 12:28:18 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */

public class DoubleBufferedPhetPCanvasTrickSize extends PhetPCanvas {
    private BufferedImage buffer;

    public DoubleBufferedPhetPCanvasTrickSize() {
//        getLayer().setScale( 0.5 );
//        setDefaultRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
//        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
    }

    public void paintComponent( Graphics g ) {
        paintBuffered( g );
    }

    protected void superPaint( Graphics g ) {
        super.paintComponent( g );
    }

    protected void paintBuffered( final Graphics g ) {
        synchronizeImage();
        Graphics2D g2 = buffer.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );//to overcome the slow default in 1.5
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g2 );
        g.drawImage( buffer, 0, 0, null );
    }

    private void synchronizeImage() {
        if( buffer == null || buffer.getWidth() != getWidth() || buffer.getHeight() != getHeight() ) {
            buffer = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
        }
    }

}
