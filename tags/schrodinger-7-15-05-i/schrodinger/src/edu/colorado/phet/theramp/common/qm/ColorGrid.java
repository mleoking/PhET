/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Jun 9, 2005
 * Time: 2:54:21 PM
 * Copyright (c) Jun 9, 2005 by Sam Reid
 */

public class ColorGrid {
    BufferedImage image;
    private int nx;
    private int ny;
    private int width;
    private int height;

    public ColorGrid( int width, int height, int nx, int ny ) {
        this.nx = nx;
        this.ny = ny;
        image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        this.width = width;
        this.height = height;
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
        int blockWidth = width / nx;
        int blockHeight = height / ny;
        for( int i = 0; i < nx; i++ ) {
            for( int k = 0; k < ny; k++ ) {
                Paint p = colorMap.getPaint( i, k );
                g2.setPaint( p );
                g2.fillRect( i * blockWidth, k * blockHeight, blockWidth, blockHeight );
            }
        }
    }

    public static void main( String[] args ) {

//        final int nx = 200;
//        final int ny = 200;
        final int nx = 100;
        final int ny = 100;

        ColorMap map = new ColorMap() {
            public Paint getPaint( int i, int k ) {
                return new Color( Color.HSBtoRGB( ( (float)i ) / nx, ( (float)k ) / ny, 1.0f ) );
            }
        };
        final ColorGrid colorGrid = new ColorGrid( 800, 800, nx, ny );
        colorGrid.colorize( map );

        final ImageDebugFrame imageDebugFrame = new ImageDebugFrame( colorGrid.getBufferedImage() );
        imageDebugFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        imageDebugFrame.setVisible( true );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorMap map = new ColorMap() {
                    public Paint getPaint( int i, int k ) {
//                        return new Color( Color.HSBtoRGB( ( (float)i ) / nx, ( (float)k ) / ny, (float)( 1.0f-time/20.0 ) ) );
                        float h = i / (float)nx;
                        float s = k / (float)ny;
//                        h *= Math.cos( time / 50.0 );
                        s *= Math.cos( time / 20.0 );
                        float b = 1.0f;
                        Color c = new Color( Color.HSBtoRGB( h, s, b ) );
                        float blue = Math.abs( (float)Math.sin( time / 30.0 ) );
//                        System.out.println( "blue = " + blue );
                        try {
                            Color out = new Color( (float)( c.getRed() / 255.0 ), (float)( c.getGreen() / 255.0 ), blue );
                            return out;
                        }
                        catch( Exception e ) {
                            System.out.println( "blue = " + blue );
                            return Color.red;
                        }

                    }
                };
                colorGrid.colorize( map );
                imageDebugFrame.setImage( colorGrid.getBufferedImage() );
                time++;
//                System.out.println( "time = " + time );
            }
        } );
        timer.start();
    }

    static int time = 0;

    public Image getBufferedImage() {
        return image;
    }


}
