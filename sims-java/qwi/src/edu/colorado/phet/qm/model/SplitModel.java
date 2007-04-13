/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.qm.model.math.Complex;
import edu.colorado.phet.qm.model.potentials.ConstantPotential;
import edu.colorado.phet.qm.model.potentials.HorizontalDoubleSlit;
import edu.colorado.phet.qm.model.propagators.NullPropagator;
import edu.colorado.phet.qm.view.piccolo.detectorscreen.IntensityManager;

import java.awt.*;


/**
 * User: Sam Reid
 * Date: Jun 10, 2005
 * Time: 6:55:04 PM
 * Copyright (c) Jun 10, 2005 by Sam Reid
 */

public class SplitModel extends QWIModel {
    private WaveModel rightWaveModel;
    private WaveModel leftWaveModel;

    private Detector leftDetector;
    private Detector rightDetector;
    private Mode mode = new NormalMode();
    private HorizontalDoubleSlit.Listener listener;

    public SplitModel() {
        this( QWIModel.DEFAULT_WIDTH, QWIModel.DEFAULT_WIDTH );
    }

    public SplitModel( int width, int height ) {
        this( width, height, DEFAULT_DT, createInitWave() );
    }

    public SplitModel( int width, int height, double deltaTime, Wave wave ) {
        super( width, height, deltaTime, wave );
        rightWaveModel = new WaveModel( new Wavefunction( getGridWidth(), getGridHeight() ), new NullPropagator( new ConstantPotential() ) );
        leftWaveModel = new WaveModel( new Wavefunction( getGridWidth(), getGridHeight() ), new NullPropagator( new ConstantPotential() ) );

        leftDetector = new Detector( this, 0, 0, 0, 0 );
        rightDetector = new Detector( this, 0, 0, 0, 0 );

        listener = new HorizontalDoubleSlit.Listener() {
            public void slitChanged() {
                synchronizeDetectorRegions();
            }
        };

        getDoubleSlitPotential().addListener( listener );

        synchronizeDetectorRegions();

        setDetectionCausesCollapse( false );//since it's a split model

//        System.out.println( "DEBUG_WAVES = " + DEBUG_WAVES );
        if( DEBUG_WAVES ) {
            final WaveDebugger leftWaveDebugger = new WaveDebugger( "Left", getLeftWavefunction() );
            leftWaveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( QWIModel model ) {
                    leftWaveDebugger.update();
                }
            } );

            final WaveDebugger rightWaveDebugger = new WaveDebugger( "Right", getRightWavefunction() );
            rightWaveDebugger.setVisible( true );
            addListener( new Adapter() {
                public void finishedTimeStep( QWIModel model ) {
                    rightWaveDebugger.update();
                }
            } );
        }

    }

    public void synchronizeDetectorRegions() {
        Rectangle[] areas = getDoubleSlitPotential().getSlitAreas();
        leftDetector.setRect( areas[0] );
        rightDetector.setRect( areas[1] );
    }

    public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
        return mode.getDetectionRegion( height, detectionY, width, h );
    }

    public void setPropagator( Propagator propagator ) {
        super.setPropagator( propagator );
        leftWaveModel.setPropagator( propagator.copy() );
        rightWaveModel.setPropagator( propagator.copy() );
    }

    protected void step() {
        mode.step();
    }

    public void reset() {
        super.reset();
        rightWaveModel.clear();
        leftWaveModel.clear();
    }

    public void clearWavefunction() {
        super.clearWavefunction();
        rightWaveModel.clear();
        leftWaveModel.clear();
    }

    public Wavefunction getRightWavefunction() {
        return rightWaveModel.getWavefunction();
    }

    public Wavefunction getLeftWavefunction() {
        return leftWaveModel.getWavefunction();
    }

    public Detector getLeftDetector() {
        return leftDetector;
    }

    public Detector getRightDetector() {
        return rightDetector;
    }

    public void setSplitMode( boolean split ) {
//        System.out.println( "split = " + split );
        this.mode = split ? (Mode)new SplitMode() : new NormalMode();
        if( split ) {//copy wavefunction state for continuity.
            copyNorthRegionToLR();
        }
        else {
            copyLRToNorthRegion();
        }
    }

    private void copyLRToNorthRegion() {
        getWaveModel().combineWaves( getNorthRegion(), leftWaveModel, rightWaveModel );
    }

    public Rectangle getNorthRegion() {
        return new Rectangle( 0, 0, getLeftWavefunction().getWidth(), getDoubleSlitPotential().getY() );
    }

    private void copyNorthRegionToLR() {
        getWaveModel().splitWave( getNorthRegion(), leftWaveModel, rightWaveModel );
    }

    public Propagator getLeftPropagator() {
        return leftWaveModel.getPropagator();
    }

    public Propagator getRightPropagator() {
        return rightWaveModel.getPropagator();
    }

    private Wavefunction superGetDetectionRegion( int height, int detectionY, int width, int h ) {
        return super.getDetectionRegion( height, detectionY, width, h );
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
            clearLRWavesSouthArea();
        }
    }

    class SplitMode implements Mode {

        public Wavefunction getDetectionRegion( int height, int detectionY, int width, int h ) {
            Wavefunction leftRegion = getLeftWavefunction().copyRegion( 0, detectionY, width, h );
            Wavefunction rightRegion = getRightWavefunction().copyRegion( 0, detectionY, width, h );
            return sumMagnitudes( leftRegion, rightRegion );
        }

        public void step() {
            if( isBarrierAbsorptive() ) {
                beforeTimeStep();
                getPropagationStrategy().step();

                //copy slit regions to left & right sides
                Rectangle[] slits = getDoubleSlitPotential().getSlitAreas();
                getSourceWaveModel().copyTo( deepen( slits[0] ), leftWaveModel );
                getSourceWaveModel().copyTo( deepen( slits[1] ), rightWaveModel );

                finishStep();
            }
            else {
                beforeTimeStep();
                getPropagationStrategy().step();

                //copy slit regions to left & right sides
                Rectangle[] slits = getDoubleSlitPotential().getSlitAreas();
                getWaveModel().copyTo( slits[0], leftWaveModel );//todo in the split model with absorption, this will be getSourceWaveModel.copyTo
                getWaveModel().copyTo( slits[1], rightWaveModel );

                finishStep();
            }
        }

        private Rectangle deepen( Rectangle slit ) {
            return new Rectangle( slit.x, slit.y, slit.width, slit.height + 2 );//todo this is a hack to make the slits look correct
        }
    }

    private void finishStep() {
        getWaveModel().clearWave( new Rectangle( getWavefunction().getWidth(), getSlitTopY() ) );

        rightWaveModel.propagate();
        leftWaveModel.propagate();
        getDamping().damp( getRightWavefunction() );
        getDamping().damp( getLeftWavefunction() );

        clearLRWavesSouthArea();

        incrementTimeStep();
        finishedTimeStep();
    }

    private void clearLRWavesSouthArea() {
        Rectangle rectangle = getLRWaveSouthClearArea();
        leftWaveModel.clearWave( rectangle );
        rightWaveModel.clearWave( rectangle );
    }

    protected Rectangle getLRWaveSouthClearArea() {
        int topYClear = (int)getDoubleSlitPotential().getSlitAreas()[0].getMaxY();
        return new Rectangle( 0, topYClear, getLeftWavefunction().getWidth(), getLeftWavefunction().getHeight() );
    }

    protected int getSlitTopY() {
        return (int)getDoubleSlitPotential().getSlitAreas()[0].getMinY();
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

    public void updateWavefunctionAfterDetection() {
        reduceWavefunctionNorm( IntensityManager.NORM_DECREMENT );//todo this used to work okay
    }

    public void setWaveSize( int width, int height ) {
        super.setWaveSize( width, height );
        rightWaveModel.setWaveSize( width, height );
        leftWaveModel.setWaveSize( width, height );
    }
}
