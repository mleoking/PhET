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

    public SplitModel( int width, int height ) {
        this( width, height, createInitDT(), createInitWave() );
    }

    public SplitModel( int width, int height, double deltaTime, Wave wave ) {
        super( width, height, deltaTime, wave );
        rightWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );
        leftWavefunction = new Wavefunction( getGridWidth(), getGridHeight() );

        leftDetector = new Detector( 0, 0, 0, 0 );
        rightDetector = new Detector( 0, 0, 0, 0 );

//        addDetector( leftDetector );
//        addDetector( rightDetector );
        synchronizeDetectors();
    }

    private void synchronizeDetectors() {
        Rectangle[] areas = getDoubleSlitPotential().getSlitAreas();
        leftDetector.setRect( areas[0] );
        rightDetector.setRect( areas[1] );
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        Wavefunction leftRegion = leftWavefunction.copyRegion( 0, detectionY, width, h );
        Wavefunction rightRegion = rightWavefunction.copyRegion( 0, detectionY, width, h );
        Wavefunction sumMagnitudes = sumMagnitudes( leftRegion, rightRegion );
        return sumMagnitudes;
    }

    private Wavefunction sumMagnitudes( Wavefunction leftRegion, Wavefunction rightRegion ) {
        Wavefunction sum = leftRegion.createEmptyWavefunction();
        for( int i = 0; i < sum.getWidth(); i++ ) {
            for( int j = 0; j < sum.getHeight(); j++ ) {
                Complex left = leftRegion.valueAt( i, j );
                Complex right = rightRegion.valueAt( i, j );
                double lhs = left.abs();
                double rhs = right.abs();
                double both = lhs + rhs;
                sum.setValue( i, j, new Complex( both, 0 ) );
            }
        }
        return sum;
    }

    protected void step() {
        beforeTimeStep();
        getPropagator().propagate( getWavefunction() );
        //copy slit regions to left & right sides
        copyDetectorAreasToWaves();
        clearSouthWave();
        getPropagator().propagate( rightWavefunction );   //todo won't work for light, needs its own propagator.
        getPropagator().propagate( leftWavefunction );

        getDamping().damp( rightWavefunction );
        getDamping().damp( leftWavefunction );

        incrementTimeStep();
        finishedTimeStep();
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

    private void copy( Rectangle slit, Wavefunction destinationWavefunction ) {
        //todo a smaller south wavefunction will need to include y-offsets.
        for( int i = slit.x; i < slit.x + slit.width; i++ ) {
            for( int j = slit.y; j < slit.y + slit.height; j++ ) {
                if( getWavefunction().containsLocation( i, j ) ) {
                    Complex value = getWavefunction().valueAt( i, j );
                    destinationWavefunction.setValue( i, j, value );
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

    public void setRightDetectorEnabled( boolean selected ) {

    }
}
