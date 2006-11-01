/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;

import java.awt.*;

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

    public void setWaveSize( int width, int height ) {
        wavefunction.setSize( width, height );
        clear();
    }

    public void copyTo( Rectangle area, WaveModel dest ) {
        for( int i = area.x; i < area.x + area.width; i++ ) {
            for( int j = area.y; j < area.y + area.height; j++ ) {
                //todo could clean up with a rectangle intersect instead of containsLocation.
                if( wavefunction.containsLocation( i, j ) ) {
                    copyTo( i, j, dest );
                }
            }
        }
    }

    public void copyTo( int i, int j, WaveModel dest ) {
        Complex value = wavefunction.valueAt( i, j );
        dest.wavefunction.setValue( i, j, value );
        propagator.copyTo( i, j, dest.propagator );
    }

    public void propagate() {
        propagator.propagate( wavefunction );
        wavefunction.setMagnitudeDirty();
    }

    public void clearWave( Rectangle rect ) {
        wavefunction.clearRect( rect );
        propagator.clearWave( rect );
    }

    public void splitWave( Rectangle region, WaveModel a, WaveModel b ) {
        wavefunction.splitWave( region, a.wavefunction, b.wavefunction );
        propagator.splitWave( region, a.propagator, b.propagator );
    }

    public void combineWaves( Rectangle region, WaveModel a, WaveModel b ) {
        wavefunction.combineWaves( region, a.wavefunction, b.wavefunction );
        propagator.combineWaves( region, a.propagator, b.propagator );
    }

    public int getWidth() {
        return wavefunction.getWidth();
    }

    public int getHeight() {
        return wavefunction.getHeight();
    }

    public void setValue( int i, int j, double real, double imag ) {
        wavefunction.setValue( i, j, real, imag );
        propagator.setValue( i, j, real, imag );
    }

    public void setMagnitude( double mag ) {
        wavefunction.setMagnitude( mag );
        propagator.setWavefunctionNorm( mag );
    }

    public boolean containsLocation( int i, int k ) {
        return wavefunction.containsLocation( i, k );
    }

    public void debugSymmetry() {
        System.out.println( "Wave size, w=" + getWidth() + "h=" + getHeight() );
    }
}
