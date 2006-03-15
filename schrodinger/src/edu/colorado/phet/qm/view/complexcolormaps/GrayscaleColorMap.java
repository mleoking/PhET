/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.complexcolormaps;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 17, 2005
 * Time: 5:16:49 PM
 * Copyright (c) Dec 17, 2005 by Sam Reid
 */

public abstract class GrayscaleColorMap implements ComplexColorMap {
    private double colorScale;

    public GrayscaleColorMap() {
        this( 12.0 );
    }

    public GrayscaleColorMap( double colorScale ) {
        this.colorScale = colorScale;
    }

    public Paint getColor( Complex value ) {
        double re = getComponent( value );
        re = Math.abs( re );
        if( re > 1 ) {
            System.out.println( "re = " + re );
            re = 1.0;
        }
        Color color = new Color( (float)re, (float)re, (float)re );
        color = scaleUp( color );
        return color;
    }

    protected abstract double getComponent( Complex value );

    private Color scaleUp( Color color ) {
        return new Color( scaleUp( color.getRed() ), scaleUp( color.getGreen() ), scaleUp( color.getBlue() ) );
    }

    private float scaleUp( int color ) {
        float f = color / 255.0f;
        f *= colorScale;
        if( f > 1.0f ) {
            f = 1.0f;
        }
        return f;
    }

    public static class Imaginary extends GrayscaleColorMap {
        protected double getComponent( Complex value ) {
            return value.getImaginary();
        }
    }

    public static class Real extends GrayscaleColorMap {
        protected double getComponent( Complex value ) {
            return value.getReal();
        }
    }
}
