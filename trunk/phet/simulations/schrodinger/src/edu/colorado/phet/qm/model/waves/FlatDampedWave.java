/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.waves;

import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:35:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class FlatDampedWave implements Wave {
    private Wave wave;
    private double intensity;
    private int x0 = 50;//todo magick.
    private double dxLattice = 7;
    private int radiusForMax = 3;

    public FlatDampedWave( Wave wave, double intensity, int width ) {
        this( wave, intensity, width, 7 * width / 100.0 );
    }

    public FlatDampedWave( Wave wave, double intensity, int width, double dxLattice ) {
        this.wave = wave;
        this.intensity = intensity;
        this.x0 = width / 2;
        this.dxLattice = dxLattice;
        this.radiusForMax = (int)( 3 * width / 100.0 );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        int xOffset = i - x0;
        double dampTerm = 1.0;
        if( Math.abs( xOffset ) > radiusForMax ) {
            double distFromMax = Math.abs( xOffset ) - radiusForMax;
            dampTerm = Math.exp( -( distFromMax ) * ( distFromMax ) / 4 / dxLattice / dxLattice );
        }
        return wave.getValue( i, j, simulationTime ).times( dampTerm * intensity );
    }
}
