/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.modules.mandel;

import edu.colorado.phet.qm.PhotonWave;
import edu.colorado.phet.qm.SchrodingerModule;
import edu.colorado.phet.qm.model.DiscreteModel;
import edu.colorado.phet.qm.model.Wave;

/**
 * User: Sam Reid
 * Date: Jul 22, 2005
 * Time: 9:07:32 AM
 * Copyright (c) Jul 22, 2005 by Sam Reid
 */

public class DoubleWave extends PhotonWave {
    public DoubleWave( SchrodingerModule schrodingerModule, DiscreteModel discreteModel ) {
        super( schrodingerModule, discreteModel );
    }

    protected Wave createWave( double phase ) {
        System.out.println( "Creating mandel wave, momentum=" + getMomentum() );
        return new MandelWave( getMomentum(), phase );
    }
}
