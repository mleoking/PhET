/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class GaussianWave implements InitialWavefunction {
    private GaussianWave1D xWave;
    private GaussianWave1D yWave;

    public GaussianWave( Point center, Vector2D momentum, double dxLattice ) {
        this.xWave = new GaussianWave1D( momentum.getX(), center.x, dxLattice );
        this.yWave = new GaussianWave1D( momentum.getY(), center.y, dxLattice );
    }

    public void initialize( Wavefunction wavefunction ) {
        initGaussian( wavefunction );
//        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
        System.out.println( "pre-norm gaussian: wavefunction.getMagnitude() = " + wavefunction.getMagnitude() );
        wavefunction.normalize();
        System.out.println( "GaussianWave.initialize" );
        System.out.println( "post-norm gaussian: wavefunction.getMagnitude() = " + wavefunction.getMagnitude() );
    }

    private void initGaussian( Wavefunction wavefunction ) {
        System.out.println( "GaussianWave.initGaussian" );

        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                wavefunction.setValue( i, j, new Complex() );

                init( wavefunction, i, j );
            }
        }
    }

    private void init( Wavefunction w, int i, int j ) {
        Complex x = xWave.getValue( i );
        Complex y = yWave.getValue( j );
        w.valueAt( i, j ).setToProduct( x, y );
    }


}
