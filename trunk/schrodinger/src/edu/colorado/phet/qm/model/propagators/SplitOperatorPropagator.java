/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model.propagators;

import edu.colorado.phet.common.math.Vector2D;
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
    PhysicalSystem physicalSystem;
    double dt = 0.01;
    private double mass = 1.0;

    public SplitOperatorPropagator( PhysicalSystem physicalSystem, Potential potential ) {
        super( potential );
        this.physicalSystem = physicalSystem;
    }

    public void propagate( Wavefunction w ) {
        Wavefunction psi = new Wavefunction( w );
        Wavefunction expV = getExpV( w.getWidth(), w.getHeight() );
        Wavefunction expT = getExpT( w.getWidth(), w.getHeight() );
        psi = multiplyPointwise( expV, psi );
        Wavefunction phi = QWIFFT2D.forwardFFT( psi );
        phi = multiplyPointwise( expT, phi );
        psi = QWIFFT2D.inverseFFT( phi );
        w.setWavefunction( w );
    }

    private Wavefunction getExpV( int width, int height ) {
        return ones( width, height );
    }

    private Wavefunction getExpT( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, getExpTValue( i, k ) );
            }
        }
        return wavefunction;
    }

    private Complex getExpTValue( int i, int j ) {
        Vector2D.Double p = new Vector2D.Double( i, j );
        double pSquared = p.getMagnitudeSq();
        double numerator = pSquared * dt / 2 / mass;
        return Complex.exponentiateImaginary( -numerator );
    }

    private Wavefunction ones( int width, int height ) {
        Wavefunction wavefunction = new Wavefunction( width, height );
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int k = 0; k < wavefunction.getHeight(); k++ ) {
                wavefunction.setValue( i, k, 1, 0 );
            }
        }
        return wavefunction;
    }


    private Wavefunction multiplyPointwise( Wavefunction a, Wavefunction b ) {
        Wavefunction result = new Wavefunction( a.getWidth(), b.getWidth() );
        for( int i = 0; i < result.getWidth(); i++ ) {
            for( int k = 0; k < result.getHeight(); k++ ) {
                result.setValue( i, k, a.valueAt( i, k ).times( b.valueAt( i, k ) ) );
            }
        }
        return a;
    }

    public void reset() {
    }

    public void setBoundaryCondition( int i, int k, Complex value ) {
    }

    public Propagator copy() {
        return new SplitOperatorPropagator( physicalSystem, getPotential() );
    }

    public void normalize() {
    }

    public void setWavefunctionNorm( double norm ) {
    }

    public void setValue( int i, int j, double real, double imag ) {
    }

}
