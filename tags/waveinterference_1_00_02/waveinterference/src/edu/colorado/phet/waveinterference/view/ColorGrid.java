/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
    private ArrayList listeners = new ArrayList();

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
//        System.out.println( "<<create image>>w= " + imageWidth + ", h=" + imageHeight );
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
        g2.dispose();
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
            notifyListeners();
        }
    }

    public void setNx( int nx ) {
        if( this.nx != nx ) {
            this.nx = nx;
            createImage();
            notifyListeners();
        }
    }

    public void setNy( int ny ) {
        if( this.ny != ny ) {
            this.ny = ny;
            createImage();
            notifyListeners();
        }
    }

    public void setCellDimensions( int cellWidth, int cellHeight ) {
        if( this.cellWidth != cellWidth || this.cellHeight != cellHeight ) {
            this.cellWidth = cellWidth;
            this.cellHeight = cellHeight;
            createImage();
            notifyListeners();
        }
    }

    private void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.update();
        }
    }

    public Dimension getGridDimensions() {
        return new Dimension( nx, ny );
    }

    public static interface Listener {

        void update();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
