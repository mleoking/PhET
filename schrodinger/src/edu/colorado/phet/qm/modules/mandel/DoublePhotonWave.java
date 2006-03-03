/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
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
    public double dPhase = 0;
    private MandelWave mandelWave;
    private MandelModule.BeamParam leftParam;
    private MandelModule.BeamParam rightParam;

    public DoublePhotonWave( SchrodingerModule schrodingerModule, DiscreteModel discreteModel ) {
        super( schrodingerModule, discreteModel );
        setOn();
        setIntensity( 1.0 );

    }

    protected Wave createWave( double phase ) {
        if( leftParam == null ) {
            leftParam = new MandelModule.BeamParam( 450, 0, getDiscreteModel().getWavefunction() );
            rightParam = new MandelModule.BeamParam( 450, 0, getDiscreteModel().getWavefunction() );
        }
        double insetX = getDiscreteModel().getWavefunction().getWidth() * getFractionalInset();
        mandelWave = new MandelWave( (int)insetX, getMomentum(), phase, dPhase, getTotalWaveMagnitudeLeft(), getTotalWaveMagnitudeRight(),
                                     getDiscreteModel().getWavefunction().getWidth() );
        return mandelWave;
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
        System.out.println( "leftParam = " + leftParam );
        System.out.println( "rightParam = " + rightParam );
    }
}
