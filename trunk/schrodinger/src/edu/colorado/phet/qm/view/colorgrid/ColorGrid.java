/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.colorgrid;

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
    private int cellWidth;
    private int cellHeight;
    private int nx;
    private int ny;

    public ColorGrid( int cellWidth, int cellHeight, int nx, int ny ) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.nx = nx;
        this.ny = ny;
        createImage();
    }

    private void createImage() {
        int imageWidth = nx * getCellWidth();
        int imageHeight = ny * getCellHeight();
        if( imageWidth <= 0 ) {
            imageWidth = 1;
        }
        if( imageHeight <= 0 ) {
            imageHeight = 1;
        }
        image = new BufferedImage( imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB );
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
        int blockWidth = getCellWidth();
        int blockHeight = getCellHeight();

        for( int i = 0; i < nx; i++ ) {
            for( int k = 0; k < ny; k++ ) {
                Paint p = colorMap.getColor( i, k );
                g2.setPaint( p );
                g2.fillRect( i * blockWidth, k * blockHeight, blockWidth, blockHeight );
            }
        }
    }

    public BufferedImage getBufferedImage() {
        return image;
    }

    public Rectangle getRectangle( int i, int j ) {
        return new Rectangle( i * getCellWidth(), j * getCellHeight(), getCellWidth(), getCellHeight() );
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public Rectangle getViewRectangle( Rectangle modelRect ) {
        int w = getCellWidth();
        int h = getCellHeight();
        return new Rectangle( modelRect.x * w, modelRect.y * h, modelRect.width * w, modelRect.height * h );
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

    public int getHeight() {
        return image.getHeight();
    }

    public void setModelSize( int nx, int ny ) {
        if( this.nx != nx || this.ny != ny ) {
            this.nx = nx;
            this.ny = ny;
            createImage();
        }
    }

    public void setNx( int nx ) {
        this.nx = nx;
        createImage();
    }

    public void setNy( int ny ) {
        this.ny = ny;
        createImage();
    }

    public void setCellDimensions( int cellWidth, int cellHeight ) {
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        createImage();
    }
}
