/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.model;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 1:30:20 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class WaveModel {
    private Lattice2D lattice;
    private ClassicalWavePropagator classicalWavePropagator;
    private ArrayList listeners = new ArrayList();

    public WaveModel( int width, int height ) {
        this( new Lattice2D( width, height ), 20, 20 );
    }

    public WaveModel( int width, int height, int dampX, int dampY ) {
        this( new Lattice2D( width, height ), dampX, dampY );
    }

    public WaveModel( Lattice2D lattice2D, int dampX, int dampY ) {
//        classicalWavePropagator = new ClassicalWavePropagator( new ConstantPotential() );
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 20, 20 );
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential() ,10,10);

//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 20, 20 );
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 10, 40);
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 5, 30 );
//        System.out.println( "dampX = " + dampX + ", dampY=" + dampY );
        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), dampX, dampY );
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 100,100);
//        classicalWavePropagator = new DampedClassicalWavePropagator( new ConstantPotential(), 30,30);
        this.lattice = lattice2D;
    }

    public ClassicalWavePropagator getClassicalWavePropagator() {
        return classicalWavePropagator;
    }

    public void setSize( int width, int height ) {
        lattice.setSize( width, height );
        classicalWavePropagator.setSize( width, height );
        notifySizeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getValue( int i, int j ) {
        return lattice.getValue( i, j );
    }

    public void addListener( int index, Listener listener ) {
        listeners.add( index, listener );
    }

    public Potential getPotential() {
        return classicalWavePropagator.getPotential();
    }

//    public void setDampingRegion( int dampX, int dampY ) {
//        classicalWavePropagator.setDampingRegion( dampX, dampY );
////        classicalWavePropagator = new DampedClassicalWavePropagator( getPotential(), dampX, dampY );
//    }

    public static interface Listener {

        void sizeChanged();
    }

    private void notifySizeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.sizeChanged();
        }
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
