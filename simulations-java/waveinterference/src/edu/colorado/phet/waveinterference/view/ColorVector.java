/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 13, 2006
 * Time: 5:25:18 AM
 * Copyright (c) Apr 13, 2006 by Sam Reid
 */
public class ColorVector {
    float r;
    float g;
    float b;

    public ColorVector( float r, float g, float b ) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColorVector( Color color ) {
        this( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f );
    }

    public ColorVector add( ColorVector c ) {
        return new ColorVector( r + c.r, g + c.g, b + c.b );
    }

    public Color toColor() {
        return new Color( r, g, b );
    }

    public ColorVector scale( float value ) {
        return new ColorVector( Math.min( r * value, 1f ), Math.min( g * value, 1f ), Math.min( b * value, 1f ) );
    }

    public double getMagnitude() {
        return Math.abs( r * r + g * g + b * b );
    }
}
