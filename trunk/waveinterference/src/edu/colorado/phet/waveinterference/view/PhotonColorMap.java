/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.waveinterference.model.Lattice2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:58:36 PM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class PhotonColorMap implements ColorMap {
    private Lattice2D lattice;
    private Vector3f rootColor;
    private Vector3f positiveColor;
    private Vector3f negativeColor;

    public PhotonColorMap( Lattice2D lattice ) {
        this( lattice, Color.blue );
    }

    public PhotonColorMap( Lattice2D lattice, Color color ) {
        rootColor = new Vector3f( 0, 0, 0 );
        positiveColor = new Vector3f( color.brighter() );
        negativeColor = new Vector3f( color.darker() );
        this.lattice = lattice;
    }

    public Color getColor( int i, int k ) {

        float value = lattice.wavefunction[i][k];
        if( value > 0 ) {
            if( value > 1 ) {
                value = 1;
            }
            return rootColor.add( positiveColor.scale( value ) ).toColor();
        }
        else if( value <= 0 ) {
            if( value <= -1 ) {
                value = -1;
            }
            return rootColor.add( negativeColor.scale( -value ) ).toColor();
        }
        else {
            return Color.black;
        }
    }

    private static class Vector3f {
        float r;
        float g;
        float b;

        public Vector3f( float r, float g, float b ) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public Vector3f( Color color ) {
            this( color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f );
        }

        public Vector3f add( Vector3f c ) {
            return new Vector3f( r + c.r, g + c.g, b + c.b );
        }

        public Color toColor() {
            return new Color( r, g, b );
        }

        public Vector3f scale( float value ) {
            return new Vector3f( Math.min( r * value, 1f ), Math.min( g * value, 1f ), Math.min( b * value, 1f ) );
        }
    }
}
