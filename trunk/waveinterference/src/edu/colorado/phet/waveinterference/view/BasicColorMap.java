/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 21, 2006
 * Time: 11:54:41 PM
 * Copyright (c) Mar 21, 2006 by Sam Reid
 */

public class BasicColorMap implements ColorMap {
    private Lattice2D lattice;
    float r = 0;
    float g = 0;
    float b = 1.0f;

    public BasicColorMap( Lattice2D lattice ) {
        this( lattice, Color.blue );
    }

    public BasicColorMap( Lattice2D lattice, Color color ) {
        this.lattice = lattice;
        r = color.getRed() / 255f;
        g = color.getGreen() / 255f;
        b = color.getBlue() / 255f;
    }

    public Color getColor( int i, int k ) {
        float value = ( lattice.getValue( i, k ) + 1.0f ) / 2.0f;
        if( value > 1 ) {
            value = 1;
        }
        else if( value < 0 ) {
            value = 0;
        }
        return new Color( r * value, g * value, b * value );
    }

    public Color getRootColor() {
        return new Color( r, g, b );
    }

}
