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

public class PhotonEmissionColorMap implements ColorMap {
    private Lattice2D lattice;
    private boolean[][] inited;
    private Color color;

    public PhotonEmissionColorMap( Lattice2D lattice ) {
        this( lattice, Color.blue );
    }

    public PhotonEmissionColorMap( Lattice2D lattice, Color color ) {
        this.color = color;
        this.lattice = lattice;
        inited = new boolean[lattice.getWidth()][lattice.getHeight()];
    }

    public Paint getColor( int i, int k ) {
        //todo ensure wavefunction is the correct size (could have been resized). 
        float value = lattice.wavefunction[i][k];
        float epsilon = 0.025f;
        if( Math.abs( value ) < epsilon && !inited[i][k] ) {
            return Color.black;
        }
        else {
            inited[i][k] = true;
            return new BasicTestColorMap( lattice, color ).getColor( i, k );
        }
    }

}
