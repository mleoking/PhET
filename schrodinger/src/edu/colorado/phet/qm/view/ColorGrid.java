/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:54:21 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */

public class ColorGrid {
    private BufferedImage image;
    private int nx;
    private int ny;
    private int width;
    private int height;

    public ColorGrid( int width, int height, int nx, int ny ) {
        this.nx = nx;
        this.ny = ny;
        this.width = width;
        this.height = height;
        createImage();
    }

    private void createImage() {
        image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
    }

    public void setSize( int width, int height ) {
        this.width = width;
        this.height = height;
        createImage();
    }

    public void colorize( ColorMap colorMap ) {
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
        g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT );
        g2.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF );
        int blockWidth = getBlockWidth();
        int blockHeight = getBlockHeight();
        for( int i = 0; i < nx; i++ ) {
            for( int k = 0; k < ny; k++ ) {
                Paint p = colorMap.getPaint( i, k );
                g2.setPaint( p );
                g2.fillRect( i * blockWidth, k * blockHeight, blockWidth, blockHeight );
            }
        }
    }

    public int getBlockHeight() {
        int blockHeight = (int)( ( (double)height ) / ny );
        return blockHeight;
    }

    public int getBlockWidth() {
        int blockWidth = (int)( ( (double)width ) / nx );
        return blockWidth;
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public Rectangle getRectangle( int i, int j ) {
        return new Rectangle( i * getBlockWidth(), j * getBlockWidth(), getBlockWidth(), getBlockHeight() );
    }

    public Rectangle getViewRectangle( Rectangle modelRect ) {
        int w = getBlockWidth();
        int h = getBlockHeight();
        Rectangle out = new Rectangle( modelRect.x * w, modelRect.y * h, modelRect.width * w, modelRect.height * h );
        return out;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getNx() {
        return nx;
    }

    public int getNy() {
        return ny;
    }
}
