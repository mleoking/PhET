/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 12:04:22 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PlaneWave implements BoundaryCondition, InitialWavefunction {
    private double k;
    private double XMESH;

    private double scale = 1.0;

    public PlaneWave( double k, double XMESH ) {
        this.k = k;
        this.XMESH = XMESH;
    }


    public double getScale() {
        return scale;
    }

    public void setScale( double scale ) {
        this.scale = scale;
    }

    public void setValue( Complex[][] w, int i, int j, double simulationTime ) {
        w[i][j] = getValueImpl( i, j, simulationTime );
    }

    private Complex getValueImpl( int i, int j, double simulationTime ) {
        Complex complex = new Complex( Math.cos( k * i / XMESH - k * k * simulationTime ), Math.sin( k * i / XMESH - k * k * simulationTime ) );
        complex.scale( scale );
        return complex;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        return getValueImpl( i, j, simulationTime );
    }

    public void initialize( Complex[][] wavefunction ) {
        for( int i = 0; i < wavefunction.length; i++ ) {
            Complex[] complexes = wavefunction[i];
            for( int j = 0; j < complexes.length; j++ ) {
                setValue( wavefunction, i, j, 0 );
            }
        }
    }
}
