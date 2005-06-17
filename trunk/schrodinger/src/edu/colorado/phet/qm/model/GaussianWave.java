/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.operators.ProbabilityValue;

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

    public void initialize( Complex[][] wavefunction ) {
        initGaussian( wavefunction );
        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
        Wavefunction.normalize( wavefunction );
        System.out.println( "GaussianWave.initialize" );
    }

    private void initGaussian( Complex[][] w ) {
        System.out.println( "GaussianWave.initGaussian" );

        for( int i = 0; i < w.length; i++ ) {
            for( int j = 0; j < w[0].length; j++ ) {
                w[i][j] = new Complex();
                init( w, w[i][j], i, j );
            }
        }
    }

    private void init( Complex[][] w, Complex complex, int i, int j ) {
        Complex x = xWave.getValue( i );
        Complex y = yWave.getValue( j );
        complex.setToProduct( x, y );
    }


}
