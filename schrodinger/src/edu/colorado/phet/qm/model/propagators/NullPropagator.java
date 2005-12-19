/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.qm.model.DiscreteModel;
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
    protected NullPropagator( DiscreteModel discreteModel, Potential potential ) {
        super( discreteModel, potential );
    }

    public void propagate( Wavefunction w ) {
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new NullPropagator( getDiscreteModel(), getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }
}
