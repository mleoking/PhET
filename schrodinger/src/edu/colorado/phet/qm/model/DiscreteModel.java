/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.qm.model.operators.ProbabilityValue;
import edu.colorado.phet.qm.model.operators.PxValue;
import edu.colorado.phet.qm.model.potentials.CompositePotential;

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
    private CompositePotential compositePotential;

    private Propagator propagator;
    private int timeStep;
    private double deltaTime;
    private InitialWavefunction initialWavefunction;
    private BoundaryCondition boundaryCondition;
    private ArrayList listeners = new ArrayList();
    private Random random = new Random();
    private ArrayList detectors = new ArrayList();
    private boolean detectionCausesCollapse = true;
    private boolean oneShotDetectors = true;

    public DiscreteModel( int xmesh, int ymesh ) {
        this( xmesh, ymesh, 1E-5, new EmptyWave(), new ZeroBoundaryCondition() );
    }

    public DiscreteModel( int xmesh, int ymesh, double deltaTime, InitialWavefunction initialWavefunction,
                          BoundaryCondition boundaryCondition ) {
        this.xmesh = xmesh;
        this.ymesh = ymesh;
        this.compositePotential = new CompositePotential();
        this.deltaTime = deltaTime;
        this.initialWavefunction = initialWavefunction;
        this.boundaryCondition = boundaryCondition;
        wavefunction = new Complex[xmesh + 1][ymesh + 1];
        initialWavefunction.initialize( wavefunction );
//        propagator = new CNCPropagator( deltaTime, boundaryCondition, compositePotential );
        propagator = new RichardsonPropagator( deltaTime, boundaryCondition, compositePotential );
        addListener( new DiscreteModel.DetectorHandler() );
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
        for( int i = 0; i < wavefunction.length; i++ ) {
            Complex[] complexes = wavefunction[i];
            for( int j = 0; j < complexes.length; j++ ) {
                Complex complex = complexes[j];
                if( complex == null ) {
                    wavefunction[i][j] = new Complex();
                }
                else {
                    complex.zero();
                }
            }
        }
        for( int i = 0; i < detectors.size(); i++ ) {
            Detector detector = (Detector)detectors.get( i );
            detector.reset();
        }
    }

    public int getXMesh() {
        return xmesh;
    }

    public int getYMesh() {
        return ymesh;
    }

    public Potential getPotential() {
        return compositePotential;
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
        propagator.setDeltaTime( deltaTime );
    }

    public Point getCollapsePoint( Rectangle bounds ) {
        //compute a probability model for each dA
        Complex[][] copy = copyWavefunction();
        int XMESH = copy.length - 1;
        int YMESH = copy[0].length - 1;

        for( int i = 1; i < XMESH; i++ ) {
            for( int j = 1; j < YMESH; j++ ) {
                if( !bounds.contains( i, j ) ) {
                    copy[i][j].zero();
                }
            }
        }

        Wavefunction.normalize( copy );//just in case we care.
        //todo could work without a normalize, just choose random.nextDouble between 0 and totalprob.
//        int XMESH = copy.length - 1;
//        int YMESH = copy[0].length - 1;
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
//        new RuntimeException( "No collapse point." ).printStackTrace();
        throw new RuntimeException( "No collapse point." );
//        return new Point( 0, 0 );
    }

    public Point getCollapsePoint() {//todo call getCollapsePoint with the internal bounds.
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

    public void collapse( Point collapsePoint, int collapseLatticeDX ) {
        double px = new PxValue().compute( wavefunction );
        new GaussianWave( collapsePoint, new Vector2D.Double( px, 0 ), collapseLatticeDX ).initialize( getWavefunction() );
    }

    public void addDetector( Detector detector ) {
        detectors.add( detector );
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

    public static interface Listener {
        void finishedTimeStep( DiscreteModel model );

        void sizeChanged();

        void potentialChanged();

        void beforeTimeStep( DiscreteModel discreteModel );
    }


    public class DetectorHandler implements Listener {
        public void finishedTimeStep( DiscreteModel model ) {
            for( int i = 0; i < detectors.size(); i++ ) {
                Detector detector = (Detector)detectors.get( i );
                detector.updateProbability( wavefunction );
            }

            if( detectionCausesCollapse ) {
                double norm = new ProbabilityValue().compute( wavefunction );//todo use the norm (don't assume it's 1.0)
                if( norm >= 0.2 ) {
//                System.out.println( "detectorNorm=" + norm);
                    for( int i = 0; i < detectors.size(); i++ ) {
                        Detector detector = (Detector)detectors.get( i );
                        double prob = detector.getProbability();
                        double rand = random.nextDouble() * norm;//todo is this right?
                        if( rand <= prob ) {
                            Point collapsePoint = getCollapsePoint( detector.getBounds() );
//                            int latticeDX=detector.getAverageDiameter();
                            collapse( collapsePoint, (int)( 0.07 * getXMesh() ) );
                            if( oneShotDetectors ) {
                                detector.setEnabled( false );
                            }
                            break;
                        }
                    }
                }
            }
        }

        public void sizeChanged() {
        }

        public void potentialChanged() {
        }

        public void beforeTimeStep( DiscreteModel discreteModel ) {
        }
    }

    public BoundaryCondition getBoundaryCondition() {
        return boundaryCondition;
    }

    public void setBoundaryCondition( BoundaryCondition boundaryCondition ) {
        this.boundaryCondition = boundaryCondition;
    }
}
