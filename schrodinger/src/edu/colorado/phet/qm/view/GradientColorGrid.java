/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:54:21 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */

public class GradientColorGrid extends ColorGrid {
    public GradientColorGrid( int width, int height, int nx, int ny ) {
        super( width, height, nx, ny );
    }

    /**
     * Very expensive
     */
    public void colorize( ColorMap colorMap ) {
        Graphics2D g2 = super.getBufferedImage().createGraphics();
        int blockWidth = getBlockWidth();
        int blockHeight = getBlockHeight();
        for( int i = 0; i < super.getNx() - 1; i++ ) {
            for( int k = 0; k < super.getNy(); k++ ) {
                Color p = colorMap.getColor( i, k );
                Color p2 = colorMap.getColor( i + 1, k );
                GradientPaint gradientPaint = new GradientPaint( i * blockWidth, k * blockHeight, p, ( i + 1 ) * blockWidth, ( k ) * blockHeight, p2, false );
                g2.setPaint( gradientPaint );
                g2.fillRect( i * blockWidth, k * blockHeight, blockWidth, blockHeight );
            }
        }
    }


}
