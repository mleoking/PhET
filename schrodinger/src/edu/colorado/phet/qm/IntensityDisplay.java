/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.qm.model.DetectorSet;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wavefunction;
import edu.colorado.phet.qm.view.DetectorSheet;
import edu.colorado.phet.qm.view.SchrodingerPanel;

import java.awt.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:43:24 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class IntensityDisplay {
    private SchrodingerModule schrodingerModule;
    private SchrodingerPanel schrodingerPanel;
    private int detectorHeight;
    private Random random;
    private DetectorSheet detectorSheet;
//    private int h = 6;
    private int h = 2;
    private int y = 2;
    private double probabilityScaleFudgeFactor = 1.0;
    private double normDecrement = 1.0;
    private int multiplier = 1;

    public IntensityDisplay( SchrodingerModule schrodingerModule, SchrodingerPanel schrodingerPanel, int detectorHeight ) {
        this.schrodingerModule = schrodingerModule;
        this.schrodingerPanel = schrodingerPanel;
        this.detectorHeight = detectorHeight;
        this.random = new Random();

        detectorSheet = new DetectorSheet( schrodingerPanel, getWidth(), detectorHeight );
        getSchrodingerPanel().addGraphic( detectorSheet );
        detectorSheet.setLocation( schrodingerPanel.getWavefunctionGraphic().getX(), 0 );
        getDiscreteModel().addListener( new DiscreteModel.Adapter() {
            public void finishedTimeStep( DiscreteModel model ) {
                tryDetecting();
            }
        } );
    }

    private void tryDetecting() {
//        QuickTimer tryDetecting=new QuickTimer();
        Wavefunction sub = getDetectionRegion();

        double probability = sub.getMagnitude() * probabilityScaleFudgeFactor;
//        System.out.println( "probability = " + probability );
        double rand = random.nextDouble();
        if( rand <= probability ) {
//            System.out.println( "Detection Occurred!" );
            for( int i = 0; i < multiplier; i++ ) {
                detectOne( sub );
            }
            updateWavefunctionAfterDetection();
        }
//        System.out.println( "tryDetecting = " + tryDetecting );
    }

    private void detectOne( Wavefunction sub ) {
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(), 0, getWidth() );
        Point pt = getCollapsePoint( sub );

        double screenGridWidth = schrodingerModule.getSchrodingerPanel().getWavefunctionGraphic().getBlockWidth();
        double randOffsetY = 2 * ( random.nextDouble() - 0.5 ) * screenGridWidth;

        int x = (int)( linearFunction.evaluate( pt.x ) + randOffsetY );
        int y = getY();
        detectorSheet.addDetectionEvent( x, y );


    }

    private int getY() {
        int y = (int)( random.nextDouble() * detectorHeight );
        return y;
    }

    private int getYGaussian() {
        double scale = detectorHeight / 8;
        double offset = detectorHeight / 2;
        int y = (int)( random.nextGaussian() * scale + offset );
        return y;
    }

    private void updateWavefunctionAfterDetection() {
        getDiscreteModel().reduceWavefunctionNorm( normDecrement );
    }

    public int getWidth() {
        return getSchrodingerPanel().getWavefunctionGraphic().getWavefunctionWidth();
    }

    public Wavefunction getDetectionRegion() {
        return getDiscreteModel().getWavefunction().copyRegion( 0,
                                                                getDetectionY(),
                                                                getDiscreteModel().getWavefunction().getWidth(),
                                                                h );
    }

    private int getDetectionY() {
        return y;
    }

    private SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private DiscreteModel getDiscreteModel() {
        return schrodingerModule.getDiscreteModel();
    }

    private Point getCollapsePoint( Wavefunction sub ) {
        DetectorSet detectorSet = new DetectorSet( sub );
        Point pt = detectorSet.getCollapsePoint();
        return pt;
    }

    public void reset() {
        detectorSheet.reset();
    }

    public double getProbabilityScaleFudgeFactor() {
        return probabilityScaleFudgeFactor;
    }

    public void setProbabilityScaleFudgeFactor( double probabilityScaleFudgeFactor ) {
        this.probabilityScaleFudgeFactor = probabilityScaleFudgeFactor;
    }

    public double getNormDecrement() {
        return normDecrement;
    }

    public void setNormDecrement( double normDecrement ) {
        this.normDecrement = normDecrement;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier( int multiplier ) {
        this.multiplier = multiplier;
    }

    public DetectorSheet getDetectorSheet() {
        return detectorSheet;
    }

    public int getOpacity() {
        return detectorSheet.getOpacity();
    }

    public void setOpacity( int opacity ) {
        detectorSheet.setOpacity( opacity );
    }
}
