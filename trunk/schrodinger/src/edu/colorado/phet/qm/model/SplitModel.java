/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import java.awt.*;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SplitModel extends DiscreteModel {

    private Wavefunction rightWavefunction;
    private Wavefunction leftWavefunction;

    private Detector leftDetector;
    private Detector rightDetector;
    private Mode mode = new NormalMode();

    public SplitModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
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

    private void superStep() {
        super.step();
    }

    private Wavefunction superGetDetectionRegion( int height, int detectionY, int width, int h ) {
        return super.getDetectionRegion( height, detectionY, width, h );
    }


    class SplitMode implements Mode {

        public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {

            Wavefunction leftRegion = leftWavefunction.copyRegion( 0, detectionY, width, h );
            Wavefunction rightRegion = rightWavefunction.copyRegion( 0, detectionY, width, h );
            Wavefunction sumMagnitudes = sumMagnitudes( leftRegion, rightRegion );
            return sumMagnitudes;
        }

        public void step() {

            beforeTimeStep();
            getPropagator().propagate( getWavefunction() );
            //copy slit regions to left & right sides
            copyDetectorAreasToWaves();
            clearSouthWave();
            clearLRWavesSouthPart();
            getPropagator().propagate( rightWavefunction );   //todo won't work for light, needs its own propagator.
            getPropagator().propagate( leftWavefunction );

            getDamping().damp( rightWavefunction );
            getDamping().damp( leftWavefunction );

            incrementTimeStep();
            finishedTimeStep();
        }


    }

    private void clearLRWavesSouthPart() {
        int topYClear = (int)getDoubleSlitPotential().getSlitAreas()[0].getMaxY();
        for( int i = 0; i < leftWavefunction.getWidth(); i++ ) {
            for( int j = topYClear; j < leftWavefunction.getHeight(); j++ ) {
                leftWavefunction.setValue( i, j, new Complex() );
                rightWavefunction.setValue( i, j, new Complex() );
            }
        }
    }

    public SplitModel( int width, int height, double deltaTime, Wave wave ) {
        super( width, height, deltaTime, wave );
        rightWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );
        leftWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );

        leftDetector = new Detector( 0, 0, 0, 0 );
        rightDetector = new Detector( 0, 0, 0, 0 );

        synchronizeDetectors();
    }

    private void synchronizeDetectors() {
        Rectangle[] areas = getDoubleSlitPotential().getSlitAreas();
        leftDetector.setRect( areas[0] );
        rightDetector.setRect( areas[1] );
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        return mode.getDetectionRegion( height, detectionY, width, h );
    }

    private Wavefunction sumMagnitudes( Wavefunction leftRegion, Wavefunction rightRegion ) {
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

    private double sumMagnitudes( Complex left, Complex right ) {
        double lhs = left.abs();
        double rhs = right.abs();
        double both = lhs + rhs;
        return both;
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
    }

    private void clearSouthWave() {
        int topYClear = (int)getDoubleSlitPotential().getSlitAreas()[0].getMinY();
        for( int i = 0; i < getWavefunction().getWidth(); i++ ) {
            for( int j = 0; j < topYClear; j++ ) {
                getWavefunction().setValue( i, j, new Complex() );
            }
        }
    }

    private void copyDetectorAreasToWaves() {
        Rectangle[] slits = getDoubleSlitPotential().getSlitAreas();
        copy( slits[0], leftWavefunction );
        copy( slits[1], rightWavefunction );
    }

    private void copy( Rectangle slit, Wavefunction dest ) {
        //todo a smaller south wavefunction will need to include y-offsets.
        for( int i = slit.x; i < slit.x + slit.width; i++ ) {
            for( int j = slit.y; j < slit.y + slit.height; j++ ) {
                if( getWavefunction().containsLocation( i, j ) ) {
                    Complex value = getWavefunction().valueAt( i, j );
                    dest.setValue( i, j, value );
                }
            }
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

    public void setSplitModel( boolean split ) {
        this.mode = split ? (Mode)new SplitMode() : new NormalMode();
        if( split ) {//copy wavefunction state for continuity.
            copyNorthRegionToSplits();
        }
        else {
            copySplitsToNorthRegion();
        }
    }

    private void copyNorthRegionToSplits() {
        int yMax = getDoubleSlitPotential().getY();
        for( int i = 0; i < leftWavefunction.getWidth(); i++ ) {//todo assumes all same size waves
            for( int j = 0; j < yMax; j++ ) {
                Complex v = getWavefunction().valueAt( i, j );
                v.scale( 0.5 );
                leftWavefunction.setValue( i, j, v );
                rightWavefunction.setValue( i, j, v );
            }
        }
    }

    private void copySplitsToNorthRegion() {
        int yMax = getDoubleSlitPotential().getY();
        for( int i = 0; i < leftWavefunction.getWidth(); i++ ) {
            for( int j = 0; j < yMax; j++ ) {
                double sum = sumMagnitudes( leftWavefunction.valueAt( i, j ), rightWavefunction.valueAt( i, j ) );
                getWavefunction().setValue( i, j, new Complex( sum, 0 ) );
            }
        }
    }
}
