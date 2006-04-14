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
    private ColorVector rootColor;
    private ColorVector positiveColor;
    private ColorVector negativeColor;

    public PhotonColorMap( Lattice2D lattice ) {
        this( lattice, Color.blue );
    }

    public PhotonColorMap( Lattice2D lattice, Color color ) {
        rootColor = new ColorVector( 0, 0, 0 );
        positiveColor = new ColorVector( color.brighter() );
        negativeColor = new ColorVector( color.darker() );
        this.lattice = lattice;
    }

    public Color getColor( int i, int k ) {

        float value = lattice.getValue( i, k );
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

    public Color getRootColor() {
        return rootColor.toColor();
    }

}
