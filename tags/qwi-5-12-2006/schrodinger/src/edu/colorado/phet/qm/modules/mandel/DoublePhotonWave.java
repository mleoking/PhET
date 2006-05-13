/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.CylinderSource;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.PhotonWave;
import edu.colorado.phet.qm.model.Wave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:07:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class DoublePhotonWave extends PhotonWave {
    private double dPhase = 0;
    private MandelModule.BeamParam leftParam;
    private MandelModule.BeamParam rightParam;
    private CylinderSource leftWaveSource;
    private CylinderSource rightWaveSource;

    public DoublePhotonWave( SchrodingerModule schrodingerModule, DiscreteModel discreteModel ) {
        super( schrodingerModule, discreteModel );
        setOn();
        setIntensity( 1.0 );
        this.leftWaveSource = new CylinderSource( createRectRegionForCylinder(), createWave( super.getPhase() ) );
        this.rightWaveSource = new CylinderSource( createRectRegionForCylinder(), createWave( super.getPhase() ) );
    }

    protected void initializeEntrantWave() {
        leftWaveSource.setRegion( createRectRegionForCylinder() );
        rightWaveSource.setRegion( createRectRegionForCylinder() );
        if( leftParam == null ) {
            leftParam = new MandelModule.BeamParam( getMomentum(), 0, getDiscreteModel().getWaveModel() );
            rightParam = new MandelModule.BeamParam( getMomentum(), 0, getDiscreteModel().getWaveModel() );
        }
        updateWaves();
        leftWaveSource.initializeEntrantWave( leftParam.getWaveModel(), getDiscreteModel().getSimulationTime() );
        rightWaveSource.initializeEntrantWave( rightParam.getWaveModel(), getDiscreteModel().getSimulationTime() );
    }

    private void updateWaves() {
        leftWaveSource.setWave( createLeftWave( leftParam, rightParam, getPhase() ) );
        rightWaveSource.setWave( createRightWave( leftParam, rightParam, getPhase() ) );
    }

    protected Wave createLeftWave( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam, double phase ) {
        double insetX = getInsetX();
        if( leftParam.getWaveModel() == rightParam.getWaveModel() ) {
            return new MandelWave( (int)insetX, getMomentum(), getMomentum(), phase, dPhase, getTotalWaveMagnitudeLeft(), getTotalWaveMagnitudeRight(),
                                   getDiscreteModel().getWavefunction().getWidth() );
        }
        else {
            return new MandelWave( (int)insetX, getMomentum(), getMomentum(), phase, dPhase, getTotalWaveMagnitudeLeft(), 0,
                                   getDiscreteModel().getWavefunction().getWidth() );
        }
    }

    protected Wave createRightWave( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam, double phase ) {
        double insetX = getInsetX();
        if( leftParam.getWaveModel() == rightParam.getWaveModel() ) {
            return new MandelWave( (int)insetX, getMomentum(), getMomentum(), phase, dPhase, getTotalWaveMagnitudeLeft(), getTotalWaveMagnitudeRight(),
                                   getDiscreteModel().getWavefunction().getWidth() );
        }
        else {
            return new MandelWave( (int)insetX, getMomentum(), getMomentum(), phase, dPhase, 0, getTotalWaveMagnitudeRight(),
                                   getDiscreteModel().getWavefunction().getWidth() );
        }
    }

    private double getInsetX() {
        return getDiscreteModel().getWavefunction().getWidth() * getFractionalInset();
    }

    private double getTotalWaveMagnitudeRight() {
        return getMagnitude() * getIntensityScale() * rightParam.getIntensity();
    }

    private double getTotalWaveMagnitudeLeft() {
        return getMagnitude() * getIntensityScale() * leftParam.getIntensity();
    }

    public static double getFractionalInset() {
        return 0.3;
    }

    public void setBeamParameters( MandelModule.BeamParam leftParam, MandelModule.BeamParam rightParam ) {
        setOn();
        setIntensity( 1.0 );
        this.leftParam = leftParam;
        this.rightParam = rightParam;
//        System.out.println( "leftParam = " + leftParam );
//        System.out.println( "rightParam = " + rightParam );
    }

    public void setLeftMomentum( double momentum ) {
        leftParam.setMomentum( momentum );
        updateWaves();
    }

    public void setRightMomentum( double momentum ) {
        rightParam.setMomentum( momentum );
        updateWaves();
    }
}
