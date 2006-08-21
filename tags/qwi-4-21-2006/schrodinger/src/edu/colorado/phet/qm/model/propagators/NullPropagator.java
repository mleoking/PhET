/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.Propagator;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.model.math.Complex;

/**
 * User: Sam Reid
 * Date: Dec 11, 2005
 * Time: 12:16:25 PM
 * Copyright (c) Dec 11, 2005 by Sam Reid
 */

public class NullPropagator extends Propagator {
    public NullPropagator( Potential potential ) {
        super( potential );
    }

    public void propagate( Wavefunction w ) {
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new NullPropagator( getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }
}
