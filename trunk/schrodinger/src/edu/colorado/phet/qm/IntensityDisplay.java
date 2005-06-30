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
    private DetectorSheet graphic;
    private int h = 6;
    private int y = 2;
    private double probabilityScaleFudgeFactor = 1.0;
    private double normDecrement = 1.0;

    public IntensityDisplay( SchrodingerModule schrodingerModule, SchrodingerPanel schrodingerPanel, int detectorHeight ) {
        this.schrodingerModule = schrodingerModule;
        this.schrodingerPanel = schrodingerPanel;
        this.detectorHeight = detectorHeight;
        this.random = new Random();

        graphic = new DetectorSheet( schrodingerPanel, getWidth(), detectorHeight );
        getSchrodingerPanel().addGraphic( graphic );

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
            detectOne( sub );
        }
//        System.out.println( "tryDetecting = " + tryDetecting );
    }

    private void detectOne( Wavefunction sub ) {
        Function.LinearFunction linearFunction = new Function.LinearFunction( 0, getDiscreteModel().getGridWidth(), 0, getWidth() );
        Point pt = getCollapsePoint( sub );

        double screenGridWidth = schrodingerModule.getSchrodingerPanel().getWavefunctionGraphic().getBlockWidth();
        double randOffsetY = 2 * ( random.nextDouble() - 0.5 ) * screenGridWidth;

        int displayVal = (int)( linearFunction.evaluate( pt.x ) + randOffsetY );
        double scale = detectorHeight / 8;
        double offset = detectorHeight / 2;
        int y = (int)( random.nextGaussian() * scale + offset );
        graphic.addDetectionEvent( displayVal, y );

        updateWavefunctionAfterDetection();
    }

    private void updateWavefunctionAfterDetection() {
        double magnitude = getDiscreteModel().getWavefunction().getMagnitude();
        if( magnitude <= 1.0 ) {
            getDiscreteModel().getWavefunction().clear();
        }
        else {
            double newMagnitude = magnitude - normDecrement;
            double scale = newMagnitude / magnitude;
            getDiscreteModel().getWavefunction().scale( scale );
        }
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
        graphic.reset();
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
}
