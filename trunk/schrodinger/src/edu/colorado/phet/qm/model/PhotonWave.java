/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.model;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.util.QMLogger;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 5:56:06 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class PhotonWave {
    private SchrodingerModule module;
    private DiscreteModel discreteModel;
    private CylinderSource waveSource;
    private double momentum = 2.0 / 10.0 * Math.PI;
    private double dPhase = Math.PI / 6;
    private double waveMagnitude = 0.075;
    private double phase = 0.0;
    private double intensity = 0.0;//todo initial value
    private double intensityScale = 0.75;
    private int waveSourceHeight = 2;
    private ModelElement phaseUpdate;

    public PhotonWave( SchrodingerModule module, DiscreteModel discreteModel ) {
        this.module = module;
        this.discreteModel = discreteModel;
        this.waveSource = new CylinderSource( createRectRegionForCylinder(), createWave( phase ) );
        phaseUpdate = new ModelElement() {
            public void stepInTime( double dt ) {
                updatePhase();
            }
        };
    }

    public void setMomentum( double momentum ) {
        this.momentum = momentum;
        dPhase = Math.abs( momentum );
        QMLogger.debug( "momentum = " + momentum + ", dPhase=" + dPhase );

        updateWaveSource();
    }

    public void setOff() {
        clearElements();
    }

    private void clearElements() {
        while( getDiscreteModel().containsListener( waveSource ) ) {
            getDiscreteModel().removeListener( waveSource );
        }
        while( module.getModel().containsModelElement( phaseUpdate ) ) {
            module.getModel().removeModelElement( phaseUpdate );
        }
    }

    public void setOn() {
        clearElements();
        waveSource.setRegion( createRectRegionForCylinder() );
        getDiscreteModel().addListener( waveSource );
        module.getModel().addModelElement( phaseUpdate );
    }

    private void updatePhase() {
        phase += dPhase;
        updateWaveSource();
    }

    private void updateWaveSource() {
        waveSource.setWave( createWave( phase ) );
    }

    protected Wave createWave( double phase ) {
//        System.out.println( "creating photonwave, momentum=" + getMomentum() );
        final PlaneWave planeWave = new PlaneWave( momentum, getDiscreteModel().getGridWidth() );
        planeWave.setPhase( phase );
        planeWave.setMagnitude( waveMagnitude );
        return new FlatDampedWave( planeWave, intensity * intensityScale );
    }

    private Rectangle createRectRegionForCylinder() {
        Rectangle rectangle = new Rectangle( 0, getWavefunction().getHeight() - waveSourceHeight,
                                             getWavefunction().getWidth(), waveSourceHeight );
//        Rectangle rectangle =new Rectangle( getWavefunction().getWidth()/4, getWavefunction().getHeight() - waveSourceHeight,
//                               getWavefunction().getWidth()/2, waveSourceHeight );
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

    public double getMomentum() {
        return momentum;
    }

    public double getMagnitude() {
        return waveMagnitude;
    }

    public double getTotalWaveMagnitude() {
        return waveMagnitude * intensity * intensityScale;
    }
}
