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
    private double gridHeight;

    private double scale = 1.0;

    public PlaneWave( double k, double gridHeight ) {
        this.k = k;
        this.gridHeight = gridHeight;
    }

    public double getScale() {
        return scale;
    }

    public void setScale( double scale ) {
        this.scale = scale;
    }

    public void setValue( Wavefunction w, int i, int j, double simulationTime ) {
        w.setValue( i, j, getValueImpl( i, j, simulationTime ) );
    }

    private Complex getValueImpl( int i, int j, double t ) {
        Complex complex = new Complex( Math.cos( k * j / gridHeight - k * k * t ), Math.sin( k * j / gridHeight - k * k * t ) );
        complex.scale( scale );
        return complex;
    }

    public Complex getValue( int i, int j, double simulationTime ) {
        return getValueImpl( i, j, simulationTime );
    }

    public void initialize( Wavefunction wavefunction ) {
        for( int i = 0; i < wavefunction.getWidth(); i++ ) {
            for( int j = 0; j < wavefunction.getHeight(); j++ ) {
                setValue( wavefunction, i, j, 0 );
            }
        }
    }
}
