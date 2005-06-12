/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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
    private Random random = new Random();

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
//        System.out.println( "new ProbabilityValue().compute( wavefunction ) = " + new ProbabilityValue().compute( wavefunction ) );
//        System.out.println( "new PositionValue().compute( ) = " + new PositionValue().compute( wavefunction ) );
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

    public Point getCollapsePoint() {
        //compute a probability model for each dA
        Complex[][] copy = copyWavefunction();

        Wavefunction.normalize( copy );//just in case we care.
        //todo could work without a normalize, just choose random.nextDouble between 0 and totalprob.
        int XMESH = copy.length - 1;
        int YMESH = copy[0].length - 1;
        Complex runningSum = new Complex();
        double rnd = random.nextDouble();

        for( int i = 1; i < XMESH; i++ ) {
            for( int j = 1; j < YMESH; j++ ) {
                Complex psiStar = copy[i][j].complexConjugate();
                Complex psi = copy[i][j];
                Complex term = psiStar.times( psi );
                double pre = runningSum.abs();
                runningSum = runningSum.plus( term );
                double post = runningSum.abs();
                if( pre <= rnd && rnd <= post ) {
                    return new Point( i, j );
                }
            }
        }
        throw new RuntimeException( "No collapse point." );
    }

    private Complex[][] copyWavefunction() {
        Complex[][] copy = new Complex[getWavefunction().length][getWavefunction()[0].length];
        for( int i = 0; i < copy.length; i++ ) {
            for( int j = 0; j < copy[0].length; j++ ) {
                copy[i][j] = wavefunction[i][j].copy();
            }
        }
        return copy;
    }

    public void collapse() {
        Point collapsePoint = getCollapsePoint();
        new GaussianWave( collapsePoint, new Vector2D.Double(), 0.1 ).initialize( getWavefunction() );
    }


    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();
    }

}
