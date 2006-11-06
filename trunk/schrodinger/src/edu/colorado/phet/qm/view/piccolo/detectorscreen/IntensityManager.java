/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.CollapseComputation;
import edu.colorado.phet.qm.model.QWIModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.QWIPanel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:43:24 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class IntensityManager {
    private QWIModule qwiModule;
    private QWIPanel QWIPanel;

    private DetectorSheetPNode detectorSheetPNode;
    private Random random;
    private int detectorRegionHeight = 2;
    private int detectionRegionY = 2;
    private double probabilityScaleFudgeFactor = 1.0;
    public static double NORM_DECREMENT = 1.0;
    private int multiplier = 1;
    private ArrayList listeners = new ArrayList();
    private double minimumProbabalityForDetection = 0.0;
    private int countsAboveZero = 0;
    private boolean allowProbabilityThreshold = false;
    private boolean allowTimeThreshold = false;
    private int timeThresholdCount = 0;

    public IntensityManager( QWIModule qwiModule, QWIPanel QWIPanel, DetectorSheetPNode detectorSheetPNode ) {
        this.detectorSheetPNode = detectorSheetPNode;
        this.qwiModule = qwiModule;
        this.QWIPanel = QWIPanel;
        this.random = new Random();
        qwiModule.getQWIModel().addListener( new QWIModel.Adapter() {
            public void propagatorChanged() {
                countsAboveZero = 0;
            }

            public void sizeChanged() {
                countsAboveZero = 0;
            }

            public void particleFired( QWIModel QWIModel ) {
                countsAboveZero = 0;
            }
        } );
    }

    public void tryDetecting() {
        Wavefunction sub = getDetectionRegion();
        double probability = sub.getMagnitude() * probabilityScaleFudgeFactor;
        if( probability >= 1E-4 ) {
            countsAboveZero++;
        }
//        System.out.println( "probability = " + sub.getMagnitude() + ", counts=" + countsAboveZero );
        for( int i = 0; i < multiplier; i++ ) {
            double rand = random.nextDouble();
            if( isAboveTimeThreshold() && rand <= probability && isAboveThreshold( sub ) ) {
                detectOne( sub );
                updateWavefunctionAfterDetection();
                notifyDetection();
                countsAboveZero = 0;
            }
        }
    }

    private boolean isAboveTimeThreshold() {
        return allowTimeThreshold ? countsAboveZero > timeThresholdCount : true;
    }

    private boolean isAboveThreshold( Wavefunction sub ) {
        return allowProbabilityThreshold ? sub.getMagnitude() >= minimumProbabalityForDetection : true;
    }

    public void setHighIntensityMode() {
        setMultiplier( 100 );
        setProbabilityScaleFudgeFactor( 10 );
        setNormDecrement( 0.0 );
        detectorSheetPNode.setHighIntensityMode();
    }

    public void reset() {
    }

    public double getMinimumProbabilityForDetection() {
        return minimumProbabalityForDetection;
    }

    public void setTimeThreshold( boolean allowTimeThreshold ) {
        this.allowTimeThreshold = allowTimeThreshold;
        System.out.println( "allowTimeThreshold = " + allowTimeThreshold );
    }

    public void setMinimumProbabilityForDetection( double value ) {
        this.minimumProbabalityForDetection = value;
        System.out.println( "this.minimumProbabalityForDetection = " + this.minimumProbabalityForDetection );
    }

    public void setTimeThreshold( int timeThresholdCount ) {
        this.timeThresholdCount = timeThresholdCount;
        System.out.println( "timeThresholdCount = " + timeThresholdCount );
    }

    public static interface Listener {
        void detectionOccurred();
    }

    private void notifyDetection() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.detectionOccurred();
        }
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void detectOne( Wavefunction detectionRegion ) {
        Point pt = new CollapseComputation().getCollapsePoint( detectionRegion, detectionRegion.getBounds() );
        double sep = Math.abs( getModelToViewTransform1d().evaluate( pt.x ) - getModelToViewTransform1d().evaluate( pt.x + 1 ) );
        double MAX_RAND_OFFSET = sep / 2;
//        System.out.println( "MAX_RAND_OFFSET = " + MAX_RAND_OFFSET );
        double randomDX = random.nextDouble() * MAX_RAND_OFFSET * ( random.nextBoolean() ? 1.0 : -1.0 );

        double x = getModelToViewTransform1d().evaluate( pt.x ) + randomDX;
//        x *= 1.0;//getWavePanelScale();
        double y = getDetectY();

        detectorSheetPNode.addDetectionEvent( x, y );
    }

    public Function.LinearFunction getModelToViewTransform1d() {
        return new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(),
                                            0, detectorSheetPNode.getBufferedImage().getWidth() );
    }

    private int getDetectY() {
        return (int)( random.nextDouble() * detectorSheetPNode.getDetectorHeight() * 0.5 );
    }

    private void updateWavefunctionAfterDetection() {
        getDiscreteModel().updateWavefunctionAfterDetection();
    }

    public Wavefunction getDetectionRegion() {
        return getDiscreteModel().getDetectionRegion( 0, getDetectionRegionY(),
                                                      getDiscreteModel().getWavefunction().getWidth(), detectorRegionHeight );
    }

    private int getDetectionRegionY() {
        return detectionRegionY;
    }

    public QWIPanel getSchrodingerPanel() {
        return QWIPanel;
    }

    private QWIModel getDiscreteModel() {
        return qwiModule.getQWIModel();
    }

    public double getProbabilityScaleFudgeFactor() {
        return probabilityScaleFudgeFactor;
    }

    public void setProbabilityScaleFudgeFactor( double probabilityScaleFudgeFactor ) {
        this.probabilityScaleFudgeFactor = probabilityScaleFudgeFactor;
    }

    public double getNormDecrement() {
        return NORM_DECREMENT;
    }

    public void setNormDecrement( double normDecrement ) {
        this.NORM_DECREMENT = normDecrement;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier( int multiplier ) {
        this.multiplier = multiplier;
    }

}
