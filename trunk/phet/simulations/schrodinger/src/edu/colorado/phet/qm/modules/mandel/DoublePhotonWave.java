/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.QWIModule;
import edu.colorado.phet.qm.model.CylinderSource;
import edu.colorado.phet.qm.model.PhotonWave;
import edu.colorado.phet.qm.model.QWIModel;
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
    private PhotonMandelBeam photonMandelBeam;

    public DoublePhotonWave( QWIModule qwiModule, QWIModel QWIModel, PhotonMandelBeam photonMandelBeam ) {
        super( qwiModule, QWIModel );
        this.photonMandelBeam = photonMandelBeam;
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
        leftWaveSource.setWave( createLeftWave() );
        rightWaveSource.setWave( createRightWave() );
    }

    public boolean isCombinedWaveModel() {
        return leftParam.getWaveModel() == rightParam.getWaveModel();
    }

    protected Wave createLeftWave() {
//        double rightMag = isCombinedWaveModel() ? getTotalWaveMagnitudeRight() : 0.0;
//        return new MandelWave( (int)getInsetX(), getMomentum(), getMomentum(), getPhase(), dPhase, getTotalWaveMagnitudeLeft(), rightMag,
//                               getDiscreteModel().getWavefunction().getWidth() );
//        System.out.println( "getMomentum() = " + getMomentum() );
//        System.out.println( "leftParam.getMomentum() = " + leftParam.getMomentum() );
//        System.out.println( "rightParam.getMomentum() = " + rightParam.getMomentum() );

        double rightMag = isCombinedWaveModel() ? getTotalWaveMagnitudeRight() : 0.0;
        return new MandelWave( (int)getInsetX(), leftParam.getMomentum() * scale, rightParam.getMomentum() * scale, getPhase(), dPhase, getTotalWaveMagnitudeLeft(), rightMag,
                               getDiscreteModel().getWavefunction().getWidth() );
    }

    double scale = 5.4E-4 / 2;
//    double scale=1.0;

    protected Wave createRightWave() {
        double leftMag = isCombinedWaveModel() ? getTotalWaveMagnitudeLeft() : 0.0;
        return new MandelWave( (int)getInsetX(), leftParam.getMomentum() * scale, rightParam.getMomentum() * scale, getPhase(), dPhase, leftMag, getTotalWaveMagnitudeRight(),
                               getDiscreteModel().getWavefunction().getWidth() );
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
        updateWaves();//todo is this necessary?
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
