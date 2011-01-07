// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.quantumwaveinterference.model.waves;

import edu.colorado.phet.quantumwaveinterference.model.math.Complex;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 */

public class GaussianWave1D {

    private double momentum;
    private double x0;
    private double dxLattice;
    private double hbar;

    public GaussianWave1D( double momentum, double x0, double dxLattice, double hbar ) {
        this.momentum = momentum;
        this.x0 = x0;
        this.dxLattice = dxLattice;
        this.hbar = hbar;
    }

    public Complex getValue( double x ) {
        double norm = Math.pow( Math.PI * dxLattice * dxLattice, -1.0 / 4.0 );
        double space = Math.exp( -( x - x0 ) * ( x - x0 ) / 2 / dxLattice / dxLattice );
        Complex momentumTerm = Complex.exponentiateImaginary( momentum * ( x - x0 ) / hbar );
        momentumTerm.scale( norm * space );
        return momentumTerm;
    }
}
