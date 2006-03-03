/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.PhotonWave;
import edu.colorado.phet.qm.model.Wave;
import edu.colorado.phet.qm.model.waves.MandelWave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:07:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class DoublePhotonWave extends PhotonWave {
    public double dPhase = 0;

    public DoublePhotonWave( SchrodingerModule schrodingerModule, DiscreteModel discreteModel ) {
        super( schrodingerModule, discreteModel );
    }

    protected Wave createWave( double phase ) {
        double insetX = getDiscreteModel().getWavefunction().getWidth() * getFractionalInset();
        return new MandelWave( (int)insetX, getMomentum(), phase, dPhase, getTotalWaveMagnitude(), getDiscreteModel().getWavefunction().getWidth() );
    }

    public static double getFractionalInset() {
        return 0.3;
    }
}
