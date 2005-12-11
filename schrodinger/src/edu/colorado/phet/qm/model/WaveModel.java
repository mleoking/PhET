/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

/**
 * User: Sam Reid
 * Date: Dec 10, 2005
 * Time: 7:05:02 PM
 * Copyright (c) Dec 10, 2005 by Sam Reid
 */

public class WaveModel {
    private Wavefunction wavefunction;
    private Propagator propagator;

    public WaveModel( Wavefunction wavefunction, Propagator propagator ) {
        this.wavefunction = wavefunction;
        this.propagator = propagator;
    }

    public Wavefunction getWavefunction() {
        return wavefunction;
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public void setPropagator( Propagator propagator ) {
        this.propagator = propagator;
    }

    public void clear() {
        wavefunction.clear();
        propagator.reset();
    }

    public void normalize() {
        wavefunction.normalize();
        propagator.normalize();
    }

    public void setWavefunctionNorm( double norm ) {
        wavefunction.setMagnitude( norm );
        propagator.setWavefunctionNorm( norm );
    }
}
