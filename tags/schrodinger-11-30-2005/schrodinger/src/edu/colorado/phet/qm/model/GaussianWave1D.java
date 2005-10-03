/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class GaussianWave1D {

    private double momentum;
    private double x0;
    private double dxLattice;
    private double phase = 0.0;

    public GaussianWave1D( double momentum, double x0, double dxLattice ) {
        this.momentum = momentum;
        this.x0 = x0;
        this.dxLattice = dxLattice;
    }

    public Complex getValue( int i ) {
        double norm = Math.pow( Math.PI * dxLattice * dxLattice, -1.0 / 4.0 );
        double space = Math.exp( -( i - x0 ) * ( i - x0 ) / 4 / dxLattice / dxLattice );
        Complex momentumTerm = Complex.exponentiateImaginary( momentum * ( i ) + phase );
        momentumTerm.scale( norm * space );
        return momentumTerm;
    }

    public void setPhase( double phase ) {
        this.phase = phase;
    }
}
