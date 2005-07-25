/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:35:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class DampedWave implements Wave {
    private Wave wave;
    private double intensity;
    private int x0 = 50;//todo magick.
    private double dxLattice = 4.0;

    public DampedWave( Wave wave, double intensity ) {
        this.wave = wave;
        this.intensity = intensity;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        double dampTerm = Math.exp( -( i - x0 ) * ( i - x0 ) / 4 / dxLattice / dxLattice );
        return wave.getValue( i, j, simulationTime ).times( dampTerm * intensity );
    }
}
