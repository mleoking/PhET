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

public class IndexColorMap implements ColorMap {
    private Lattice2D lattice;
    private float r;
    private float g;
    private float b;
    private Color[] colors;
    private int NUM_COLORS = 201;
    private WaveValueReader waveValueReader;

    public IndexColorMap( Lattice2D lattice ) {
        this( lattice, Color.blue );
    }

    public IndexColorMap( Lattice2D lattice, Color color ) {
        this( lattice, color, new WaveValueReader.Displacement() );
    }

    public IndexColorMap( Lattice2D lattice, Color color, WaveValueReader waveValueReader ) {
        this.waveValueReader = waveValueReader;
        r = color.getRed() / 255.0f;
        g = color.getGreen() / 255.0f;
        b = color.getBlue() / 255.0f;
        this.lattice = lattice;
        colors = new Color[NUM_COLORS];
        for( int i = 0; i < colors.length - 1; i++ ) {
            float value = i / ( (float)colors.length );
            colors[i] = new Color( r * value, g * value, b * value );
        }
        float value = 1;
        colors[colors.length - 1] = new Color( r * value, g * value, b * value );
    }

    public Color getColor( int i, int k ) {
        double value = waveValueReader.getValue( lattice, i, k );
        int key = (int)( value * ( colors.length - 1 ) );
        return colors[key];
    }

    public Color getRootColor() {
        return new Color( r, g, b );
    }

}
