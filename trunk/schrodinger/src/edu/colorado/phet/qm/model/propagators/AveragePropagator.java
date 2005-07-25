/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Complex;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Jun 28, 2005
 * Time: 3:35:37 PM
 * Copyright (c) Jun 28, 2005 by Sam Reid
 */

public class AveragePropagator implements Propagator {
    public void propagate( Wavefunction w ) {
        Wavefunction copy = w.copy();
        for( int i = 1; i < w.getWidth() - 1; i++ ) {
            for( int j = 1; j < w.getHeight() - 1; j++ ) {
                Complex avg = ( copy.valueAt( i + 1, j ) ).plus( copy.valueAt( i - 1, j ) ).plus( copy.valueAt( i, j + 1 ) ).plus( copy.valueAt( i, j - 1 ) );
                w.setValue( i, j, avg.times( 0.25 ) );
            }
        }
    }

    public void setDeltaTime( double deltaTime ) {
    }

    public double getSimulationTime() {
        return 0;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new AveragePropagator();
    }

    public void normalize() {

    }

    public void setWavefunctionNorm( double norm ) {
    }
}
