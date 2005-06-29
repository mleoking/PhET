/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 8:16:39 AM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class GaussianWave extends WaveSetup implements Wave {
    private GaussianWave1D xWave;
    private GaussianWave1D yWave;

    public GaussianWave( Point2D center, Vector2D momentum, double dxLattice ) {
        this( center, momentum, dxLattice, dxLattice );
    }

    public GaussianWave( Point2D center, Vector2D momentum, double dxLattice, double dyLattice ) {
        this.xWave = new GaussianWave1D( momentum.getX(), center.getX(), dxLattice );
        this.yWave = new GaussianWave1D( momentum.getY(), center.getY(), dyLattice );
        super.setWave( this );
    }

    public void initialize( Wavefunction wavefunction, double time ) {
        super.initialize( wavefunction, time );
        wavefunction.normalize();
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        Complex x = xWave.getValue( i );
        Complex y = yWave.getValue( j );
        Complex product = x.times( y );
        return product;
    }
}
