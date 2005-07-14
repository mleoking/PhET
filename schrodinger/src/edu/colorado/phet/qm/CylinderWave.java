/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.*;
import edu.colorado.phet.qm.view.gun.AbstractGun;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class CylinderWave {
    private SchrodingerModule module;
    private DiscreteModel discreteModel;
    private CylinderSource waveSource;
    private double momentum = 2.0 / 10.0 * Math.PI;
    private double dPhase = Math.PI / 6;
    private double waveMagnitude = 0.075;
    private double phase = 0.0;
    private double intensity = 0.0;//todo initial value
//    private double intensityScale = 2.0;
//    private double intensityScale = 1.0;
    private double intensityScale = 0.75;

    public CylinderWave( SchrodingerModule module, DiscreteModel discreteModel, AbstractGun abstractGun ) {
        this.module = module;
        this.discreteModel = discreteModel;
        this.waveSource = new CylinderSource( createRectRegionForCylinder(), createPlaneWave( phase ) );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                updatePhase();
            }
        } );
    }

    public void setMomentum( double momentum ) {
        this.momentum = momentum;
        dPhase = Math.abs( momentum );
        System.out.println( "momentum = " + momentum + ", dPhase=" + dPhase );

        updateWaveSource();
    }

    public void setOff() {
        getDiscreteModel().removeListener( waveSource );
    }

    public void setOn() {
        waveSource.setRegion( createRectRegionForCylinder() );
        getDiscreteModel().addListener( waveSource );
    }


    private void updatePhase() {
        phase += dPhase;
        updateWaveSource();
    }

    private void updateWaveSource() {
        waveSource.setWave( createPlaneWave( phase ) );
    }

    private Wave createPlaneWave( double phase ) {
        final PlaneWave planeWave = new PlaneWave( momentum, getDiscreteModel().getGridWidth() );
        planeWave.setPhase( phase );

        planeWave.setMagnitude( waveMagnitude );
        return new FlatDampedWave( planeWave, intensity * intensityScale );
    }

    private Rectangle createRectRegionForCylinder() {
//        int damping = getDiscreteModel().getDamping().getDepth();
//
//        int cylinderRadius = 50;
////        int cylinderRadius = 100;
////        int cylinderRadius = 10;
//        int depthToShow = damping + 5;
//        Point center = new Point( getWavefunction().getWidth() / 2, getWavefunction().getHeight() + cylinderRadius - depthToShow );
//        Point corner = new Point( center.x + cylinderRadius, center.y + cylinderRadius );
//        Rectangle rectangle = new Rectangle();
//        rectangle.setFrameFromCenter( center, corner );

        int h = 2;
        Rectangle rectangle = new Rectangle( 0, getWavefunction().getHeight() - h, getWavefunction().getWidth(), h );
        return rectangle;
    }

    private Wavefunction getWavefunction() {
        return getDiscreteModel().getWavefunction();
    }

    public DiscreteModel getDiscreteModel() {
        return discreteModel;
    }

    public void setIntensity( double intensity ) {
        this.intensity = intensity;
    }
}
