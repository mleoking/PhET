/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.potentials.CompositePotential;

import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class DiscreteModel {
    private Wavefunction wavefunction;
//    private int xmesh;
//    private int ymesh;
    private CompositePotential compositePotential;

    private Propagator propagator;
    private int timeStep;
    private double deltaTime;
    private InitialWavefunction initialWavefunction;
    private BoundaryCondition boundaryCondition;
    private ArrayList listeners = new ArrayList();
    private DetectorSet detectorSet;
    private boolean detectionCausesCollapse = true;
    private boolean oneShotDetectors = true;
    public Damping damping;
    private VerticalETA verticalEta;

    public DiscreteModel( int width, int height ) {
        this( width, height, 1E-5, new EmptyWave(), new ZeroBoundaryCondition() );
    }

    public DiscreteModel( int width, int height, double deltaTime, InitialWavefunction initialWavefunction,
                          BoundaryCondition boundaryCondition ) {
        this.compositePotential = new CompositePotential();
        this.deltaTime = deltaTime;
        this.initialWavefunction = initialWavefunction;
        this.boundaryCondition = boundaryCondition;
        wavefunction = new Wavefunction( width, height );
        detectorSet = new DetectorSet( wavefunction );
        initialWavefunction.initialize( wavefunction );
        propagator = new ModifiedRichardsonPropagator( deltaTime, boundaryCondition, compositePotential );
        addListener( detectorSet.getListener() );

        damping = new Damping();
        addListener( damping );

        verticalEta = new VerticalETA();
        verticalEta.addListener( new DistributionCapture( this ) );
        addListener( verticalEta );
    }

    public VerticalETA getVerticalEta() {
        return verticalEta;
    }

    private void step() {
        beforeTimeStep();
        propagator.propagate( wavefunction );
        timeStep++;
        finishedTimeStep();
    }

    private void beforeTimeStep() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.beforeTimeStep( this );
        }
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
        wavefunction.clear();
        detectorSet.reset();
    }

    public int getGridWidth() {
        return wavefunction.getWidth();
    }

    public int getGridHeight() {
        return wavefunction.getHeight();
    }

    public Potential getPotential() {
        return compositePotential;
    }

    public void stepInTime( double dt ) {
//        System.out.println( "DiscreteModel.stepInTime" );
        step();
//        System.out.println( "/DiscreteModel.stepInTime" );
    }

    public Wavefunction getWavefunction() {
        return wavefunction;
    }

    public int getTimeStep() {
        return timeStep;
    }

    public void fireParticle( InitialWavefunction initialWavefunction ) {
        initialWavefunction.initialize( wavefunction );
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.particleFired( this );
        }
    }

    public void setGridSpacing( int nx, int ny ) {
        if( nx != getGridWidth() || ny != getGridHeight() ) {
            wavefunction.setSize( nx, ny );
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
        propagator.setDeltaTime( deltaTime );
    }

    public void addDetector( Detector detector ) {
        detectorSet.addDetector( detector );
    }

    public void setDetectionCausesCollapse( boolean selected ) {
        this.detectionCausesCollapse = selected;
    }

    public void addPotential( Potential potential ) {
        compositePotential.addPotential( potential );
    }

    public void clearPotential() {
        compositePotential.clear();
    }

    public double getSimulationTime() {
        return propagator.getSimulationTime();
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void setOneShotDetectors( boolean oneShotDetectors ) {
        this.oneShotDetectors = oneShotDetectors;
    }

    public boolean isOneShotDetectors() {
        return oneShotDetectors;
    }

    public void setPropagator( Propagator propagator ) {
        this.propagator = propagator;
    }

    public Propagator getPropagator() {
        return propagator;
    }

    public Point getCollapsePoint() {
        return detectorSet.getCollapsePoint();
    }

    public Damping getDamping() {
        return damping;
    }

    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();

        void beforeTimeStep( DiscreteModel discreteModel );

        void particleFired( DiscreteModel discreteModel );
    }

    public static class Adapter implements Listener {

        public void finishedTimeStep( DiscreteModel model ) {
        }

        public void sizeChanged() {
        }

        public void potentialChanged() {
        }

        public void beforeTimeStep( DiscreteModel discreteModel ) {
        }

        public void particleFired( DiscreteModel discreteModel ) {
        }
    }

    public BoundaryCondition getBoundaryCondition() {
        return boundaryCondition;
    }

    public void setBoundaryCondition( BoundaryCondition boundaryCondition ) {
        this.boundaryCondition = boundaryCondition;
    }

    public boolean isDetectionCausesCollapse() {
        return detectionCausesCollapse;
    }
}
