/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:30:20 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class WaveModel {
    private Lattice2D lattice;
    private ClassicalWavePropagator classicalWavePropagator;

    public WaveModel( int width, int height ) {
        lattice = new Lattice2D( width, height );
        classicalWavePropagator = new ClassicalWavePropagator( new ConstantPotential() );
    }

    public void setSize( int width, int height ) {
        lattice.setSize( width, height );
        classicalWavePropagator.setSize( width, height );
    }

    public Lattice2D getLattice() {
        return lattice;
    }

    public void propagate() {
        classicalWavePropagator.propagate( lattice );
    }

    public int getHeight() {
        return lattice.getHeight();
    }

    public void setSourceValue( int i, int j, float value ) {
        lattice.setValue( i, j, (float)value );
        classicalWavePropagator.setBoundaryCondition( i, j, (float)value );
    }

    public int getWidth() {
        return lattice.getWidth();
    }

    public boolean containsLocation( int x, int y ) {
        return lattice.containsLocation( x, y );
    }

    public double getAverageValue( int x, int y, int windowWidth ) {
        return lattice.getAverageValue( x, y, windowWidth );
    }

    public void setPotential( Potential potential ) {
        classicalWavePropagator.setPotential( potential );
    }

    public void clear() {
        lattice.clear();
        classicalWavePropagator.clear();
    }
}
