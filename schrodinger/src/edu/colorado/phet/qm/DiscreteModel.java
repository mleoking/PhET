/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class DiscreteModel {
    private Complex[][] wavefunction;
    private int xmesh;
    private int ymesh;
    private Potential potential;

    private CNCPropagator cncPropagator;
    private int timeStep;
    private double deltaTime;
    private InitialWavefunction initialWavefunction;
    private BoundaryCondition boundaryCondition;
    private ArrayList listeners = new ArrayList();

    public DiscreteModel( int xmesh, int ymesh ) {
        this( xmesh, ymesh, new ConstantPotential( 0 ), 1E-5, new EmptyWave(), new ZeroBoundaryCondition() );
    }

    public DiscreteModel( int xmesh, int ymesh, Potential potential, double deltaTime, InitialWavefunction initialWavefunction,
                          BoundaryCondition boundaryCondition ) {
        this.xmesh = xmesh;
        this.ymesh = ymesh;
        this.potential = potential;
        this.deltaTime = deltaTime;
        this.initialWavefunction = initialWavefunction;
        this.boundaryCondition = boundaryCondition;
        wavefunction = new Complex[xmesh + 1][ymesh + 1];
        initialWavefunction.initialize( wavefunction );
        cncPropagator = new CNCPropagator( deltaTime, boundaryCondition, potential );
    }

    private void step() {
        cncPropagator.propagate( wavefunction );
        timeStep++;
        finishedTimeStep();

        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
        System.out.println( "new PositionValue().compute( ) = " + new PositionValue().compute( wavefunction ) );
    }

    private void finishedTimeStep() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.finishedTimeStep( this );
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void reset() {
    }

    public int getXMesh() {
        return xmesh;
    }

    public int getYMesh() {
        return ymesh;
    }

    public Potential getPotential() {
        return potential;
    }

    public void stepInTime( double dt ) {
//        System.out.println( "DiscreteModel.stepInTime" );
        step();
//        System.out.println( "/DiscreteModel.stepInTime" );
    }

    public Complex[][] getWavefunction() {
        return wavefunction;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void fireParticle( InitialWavefunction initialWavefunction ) {
        initialWavefunction.initialize( wavefunction );
    }

    public void setGridSpacing( int nx, int ny ) {
        if( ny != xmesh || ny != ymesh ) {
            xmesh = nx;
            ymesh = ny;
            wavefunction = new Complex[xmesh + 1][ymesh + 1];
            initialWavefunction.initialize( wavefunction );
            notifySizeChanged();
        }
    }

    private void notifySizeChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.sizeChanged();
        }
    }

    public double getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime( double t ) {
        this.deltaTime = t;
        cncPropagator.setDeltaTime( deltaTime );
    }

    public void setPotential( Potential potential ) {
        this.potential = potential;
        cncPropagator.setPotential( potential );
    }


    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();
    }

}
