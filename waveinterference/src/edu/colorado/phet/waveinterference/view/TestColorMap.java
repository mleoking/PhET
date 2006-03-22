/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;
import sun.awt.image.IntegerComponentRaster;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:54:41 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class TestColorMap implements ColorMap {
    private Lattice2D lattice;
    float r = 0;
    float g = 0;
    float b = 1.0f;

    public TestColorMap( Lattice2D lattice ) {
        this.lattice = lattice;
    }

//    static class MyContext implements PaintContext {
//        Raster raster = Raster.createRaster( new ComponentSampleModel(), )
//
//        public void dispose() {
//        }
//
//        public ColorModel getColorModel() {
//            return null;
//        }
//
//        public Raster getRaster( int x, int y, int w, int h ) {
//            return raster;
//        }
//    }

    static class MyPaint implements Paint {
        private int value;

        public MyPaint( float r, float g, float b ) {
            int r1 = (int)( r * 255 + 0.5 );
            int r2 = (int)( g * 255 + 0.5 );
            int r3 = (int)( b * 255 + 0.5 );

            value = ( ( 255 & 0xFF ) << 24 ) |
                    ( ( r1 & 0xFF ) << 16 ) |
                    ( ( r2 & 0xFF ) << 8 ) |
                    ( ( r3 & 0xFF ) );
        }

        public PaintContext createContext( ColorModel cm, Rectangle deviceBounds, Rectangle2D userBounds, AffineTransform xform, RenderingHints hints ) {
            return new MyColorPaintContext( value, null );
        }

        public int getTransparency() {
            return Paint.OPAQUE;
        }
    }

    public Paint getColor( int i, int k ) {
        float value = ( lattice.wavefunction[i][k] + 1.0f ) / 2.0f;
        if( value > 1 ) {
            value = 1;
        }
        else if( value < 0 ) {
            value = 0;
        }
        return new MyPaint( r * value, g * value, b * value );
//        return new Color( r * value, g * value, b * value );
    }

    static class MyColorPaintContext implements PaintContext {
        int color;


        protected MyColorPaintContext( int color, ColorModel cm ) {
            this.color = color;
        }

        public void dispose() {
        }

        public ColorModel getColorModel() {
            return ColorModel.getRGBdefault();
        }

        public synchronized Raster getRaster( int x, int y, int w, int h ) {
            WritableRaster t = savedTile;
            static WritableRaster savedTile;
            if( t == null || w > t.getWidth() || h > t.getHeight() ) {
                t = getColorModel().createCompatibleWritableRaster( w, h );
                IntegerComponentRaster icr = (IntegerComponentRaster)t;
//                int[] array = icr.getDataStorage();
                Arrays.fill( icr.getDataStorage(), color );
                if( w <= 64 && h <= 64 ) {
                    savedTile = t;
                }
            }

            return t;
        }
    }
}
