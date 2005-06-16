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

    private Point center;
    private Vector2D momentum;
    private double dxLattice;

    public GaussianWave( Point center, Vector2D momentum, double dxLattice ) {
        this.center = center;
        this.momentum = momentum;
        this.dxLattice = dxLattice;
    }

    public void initialize( Complex[][] wavefunction ) {
        initGaussian( wavefunction );
        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
        Wavefunction.normalize( wavefunction );
        System.out.println( "GaussianWave.initialize" );
    }

    private void initGaussian( Complex[][] w ) {
        for( int i = 0; i < w.length; i++ ) {
            for( int j = 0; j < w[0].length; j++ ) {
                w[i][j] = new Complex();
                init( w, w[i][j], i, j );
            }
        }
    }

    private void init( Complex[][] w, Complex complex, int i, int j ) {
        double space = getSpaceTerm( i, j );
        Complex mom = getMomentumTerm( i, j );
        double norm = getNormalizeTerm( 2 );

        Complex c = mom.times( space ).times( norm );
        complex.setValue( c );
    }

    private Complex getMomentumTerm( int i, int j ) {
        double dot = momentum.dot( new Vector2D.Double( center, new Point( i, j ) ) );
        Complex c = new Complex( Math.cos( dot ), Math.sin( dot ) );
        return c;
    }

    private double getSpaceTerm( int i, int j ) {
        Vector2D v2 = new Vector2D.Double( center, new Point( i, j ) );
        return Math.exp( -v2.getMagnitudeSq() / 2 / ( dxLattice * dxLattice ) );
    }

    private double getNormalizeTerm( int dim ) {
        return Math.pow( Math.PI * 2 * dxLattice * dxLattice, -dim / 2 );
    }

}
