/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.qm;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 12:04:22 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class PlaneWave implements BoundaryCondition, InitialWavefunction {
    double k;
    private double XMESH;

    public PlaneWave( double k, double XMESH ) {
        this.k = k;
        this.XMESH = XMESH;
    }

    public void setValue( Complex[][] w, int i, int j, double simulationTime) {
        int XMESH = w.length - 1;
        int YMESH = w[0].length - 1;
        w[i][j] = new Complex( Math.cos( k * i / XMESH - k * k * simulationTime ), Math.sin( k * i / XMESH - k * k * simulationTime ) );
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        //w[i][0] = new Complex( Math.cos( K * i / XMESH - K * K * simulationTime ), Math.sin( K * i / XMESH - K * K * simulationTime ) );
        return new Complex( Math.cos( k * i / XMESH - k * k * simulationTime ), Math.sin( k * i / XMESH - k * k * simulationTime ) );
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
