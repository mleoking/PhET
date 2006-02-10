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

public class SplitOperatorPropagator extends Propagator {
    public SplitOperatorPropagator( Potential potential ) {
        super( potential );
    }

    public void propagate( Wavefunction psi ) {
        Wavefunction expV = new Wavefunction( psi.getWidth(), psi.getHeight() );
        Wavefunction expT = new Wavefunction( psi.getWidth(), psi.getHeight() );
        psi = multiply( expV, psi );
        Wavefunction phi = forwardFFT( psi );
        phi = multiply( expT, phi );
        psi = inverseFFT( phi );
    }

    private Wavefunction inverseFFT( Wavefunction phi ) {
        return phi;
    }

    private Wavefunction forwardFFT( Wavefunction psi ) {
        return psi;
    }

    private Wavefunction multiply( Wavefunction a, Wavefunction b ) {
        return a;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new SplitOperatorPropagator( getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }
}
