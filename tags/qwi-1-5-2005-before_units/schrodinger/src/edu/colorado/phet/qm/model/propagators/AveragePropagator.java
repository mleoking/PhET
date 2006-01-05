/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:35:37 PM
 * Copyright (c) Jun 28, 2005 by Sam Reid
 */

public class AveragePropagator extends Propagator {
    public AveragePropagator( Potential potential ) {
        super( potential );
    }

    public void propagate( Wavefunction w ) {
        Wavefunction copy = w.copy();
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                Complex avg = ( copy.valueAt( i + 1, j ) ).plus( copy.valueAt( i - 1, j ) ).plus( copy.valueAt( i, j + 1 ) ).plus( copy.valueAt( i, j - 1 ) );
                w.setValue( i, j, avg.times( 0.25 ) );
            }
        }
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new AveragePropagator( getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }
}
