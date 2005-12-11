/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.propagators.ClassicalWavePropagator;

import java.awt.*;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SplitModel extends DiscreteModel {
    private Wavefunction rightWavefunction;
    private Propagator rightPropagator;

    private Wavefunction leftWavefunction;
    private Propagator leftPropagator;

    private Detector leftDetector;
    private Detector rightDetector;
    private Mode mode = new NormalMode();
    private HorizontalDoubleSlit.Listener listener;

    public SplitModel() {
        this( DiscreteModel.DEFAULT_WIDTH, DiscreteModel.DEFAULT_WIDTH );
    }

    public SplitModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
    }

    public SplitModel( int width, int height, double deltaTime, Wave wave ) {
        super( width, height, deltaTime, wave );
        rightWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );
        leftWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );

        leftDetector = new Detector( this, 0, 0, 0, 0 );
        rightDetector = new Detector( this, 0, 0, 0, 0 );

        listener = new HorizontalDoubleSlit.Listener() {
            public void slitChanged() {
                synchronizeDetectorRegions();
            }
        };
        getDoubleSlitPotential().addListener( listener );
        synchronizeDetectorRegions();

//        final WaveDebugger leftWaveDebugger = new WaveDebugger( "Left", leftWavefunction );
//        leftWaveDebugger.setVisible( true );
//        addListener( new Adapter() {
//            public void finishedTimeStep( DiscreteModel model ) {
//                leftWaveDebugger.update();
//            }
//        } );
//
//        final WaveDebugger rightWaveDebugger = new WaveDebugger( "Right", rightWavefunction );
//        rightWaveDebugger.setVisible( true );
//        addListener( new Adapter() {
//            public void finishedTimeStep( DiscreteModel model ) {
//                rightWaveDebugger.update();
//            }
//        } );
    }

    private void synchronizeDetectorRegions() {
        Rectangle[] areas = getDoubleSlitPotential().getSlitAreas();
        leftDetector.setRect( areas[0] );
        rightDetector.setRect( areas[1] );
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        return mode.getDetectionRegion( height, detectionY, width, h );
    }

    public void setPropagator( Propagator propagator ) {
        super.setPropagator( propagator );
        this.leftPropagator = propagator.copy();
        this.rightPropagator = propagator.copy();
    }

    protected void step() {
        mode.step();
    }

    public void reset() {
        super.reset();
        rightWavefunction.clear();
        leftWavefunction.clear();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        rightWavefunction.clear();
        leftWavefunction.clear();
        if( rightPropagator != null ) {
            rightPropagator.reset();
            leftPropagator.reset();
        }
    }

    public Wavefunction getRightWavefunction() {
        return rightWavefunction;
    }

    public Wavefunction getLeftWavefunction() {
        return leftWavefunction;
    }

    public Detector getLeftDetector() {
        return leftDetector;
    }

    public Detector getRightDetector() {
        return rightDetector;
    }

    public void setSplitMode( boolean split ) {
        this.mode = split ? (Mode)new SplitMode() : new NormalMode();
        if( split ) {//copy wavefunction state for continuity.
            getWaveSplitStrategy().copyNorthRegionToSplits();
        }
        else {
            getWaveSplitStrategy().copySplitsToNorthRegion();
        }
    }

    public Propagator getLeftPropagator() {
        return leftPropagator;
    }

    public Propagator getRightPropagator() {
        return rightPropagator;
    }

    private Wavefunction superGetDetectionRegion( int height, int detectionY, int width, int h ) {
        return super.getDetectionRegion( height, detectionY, width, h );
    }

    private WaveSplitStrategy getWaveSplitStrategy() {
        if( getPropagator() instanceof ClassicalWavePropagator ) {
            return new ClassicalWaveSplitStrategy( this );
        }
        else {
            return new WaveSplitStrategy( this );
        }
    }

    private void superStep() {
        super.step();
    }

    static interface Mode {
        Wavefunction getDetectionRegion( int height, int detectionY, int width, int h );

        void step();
    }

    class NormalMode implements Mode {
        public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
            return SplitModel.this.superGetDetectionRegion( height, detectionY, width, h );
        }

        public void step() {
            SplitModel.this.superStep();
        }
    }

    class SplitMode implements Mode {

        public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
            Wavefunction leftRegion = leftWavefunction.copyRegion( 0, detectionY, width, h );
            Wavefunction rightRegion = rightWavefunction.copyRegion( 0, detectionY, width, h );
            return sumMagnitudes( leftRegion, rightRegion );
        }

        public void step() {
//            if( !SplitModel.this.isSlitAbsorptive() ) {
            beforeTimeStep();
            getPropagator().propagate( getWavefunction() );
            //copy slit regions to left & right sides
            getWaveSplitStrategy().copyDetectorAreasToWaves();
            getWaveSplitStrategy().clearEntrantWaveNorthArea();
            getWaveSplitStrategy().clearLRWavesSouthPart();
            rightPropagator.propagate( rightWavefunction );   //todo won't work for light, needs its own propagator.
            leftPropagator.propagate( leftWavefunction );

            getDamping().damp( rightWavefunction );
            getDamping().damp( leftWavefunction );

            incrementTimeStep();
            finishedTimeStep();
//            }
//            else {
//                beforeTimeStep();
//                new AbsorptivePropagate().step();
//
//                //copy slit regions to left & right sides
//                getWaveSplitStrategy().copyDetectorAreasToWaves();
//                getWaveSplitStrategy().clearEntrantWaveNorthArea();
//                getWaveSplitStrategy().clearLRWavesSouthPart();
//                rightPropagator.propagate( rightWavefunction );   //todo won't work for light, needs its own propagator.
//                leftPropagator.propagate( leftWavefunction );
//
//                getDamping().damp( rightWavefunction );
//                getDamping().damp( leftWavefunction );
//
//                incrementTimeStep();
//                finishedTimeStep();
//            }
        }
    }

    public static Wavefunction sumMagnitudes( Wavefunction leftRegion, Wavefunction rightRegion ) {
        Wavefunction sum = leftRegion.createEmptyWavefunction();
        for( int i = 0; i < sum.getWidth(); i++ ) {
            for( int j = 0; j < sum.getHeight(); j++ ) {

                Complex left = leftRegion.valueAt( i, j );
                Complex right = rightRegion.valueAt( i, j );
                double both = sumMagnitudes( left, right );
                sum.setValue( i, j, new Complex( both, 0 ) );
            }
        }
        return sum;
    }

    public static double sumMagnitudes( Complex left, Complex right ) {
        double lhs = left.abs();
        double rhs = right.abs();
        return lhs + rhs;
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        leftWavefunction.setSize( width, height );
        rightWavefunction.setSize( width, height );
        leftPropagator.reset();
        rightPropagator.reset();
    }
}
