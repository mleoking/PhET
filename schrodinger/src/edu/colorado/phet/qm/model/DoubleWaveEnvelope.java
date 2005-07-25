/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jul 8, 2005
 * Time: 6:35:54 PM
 * Copyright (c) Jul 8, 2005 by Sam Reid
 */

public class DoubleWaveEnvelope implements Wave {
    private Wave wave;
    private double intensity;
    private int x0 = 30;//todo magick.
    private int x1 = 70;//todo magick.
    private double dxLattice = 7;
    private int radiusForMax = 3;

    public DoubleWaveEnvelope( Wave wave, double intensity ) {
        this.wave = wave;
        this.intensity = intensity;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        double damp1 = getDampTerm( i, x0 );
        double damp2 = getDampTerm( i, x1 );
        double dampTerm = damp1 + damp2;
        return wave.getValue( i, j, simulationTime ).times( dampTerm * intensity );
    }

    private double getDampTerm( int i, int x0 ) {
        int xOffset = i - x0;
        double dampTerm = 1.0;
        if( Math.abs( xOffset ) > radiusForMax ) {
            double distFromMax = Math.abs( xOffset ) - radiusForMax;
            dampTerm = Math.exp( -( distFromMax ) * ( distFromMax ) / 4 / dxLattice / dxLattice );
        }
        return dampTerm;
    }
}
