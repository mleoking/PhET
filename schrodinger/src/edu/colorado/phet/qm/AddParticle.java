/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.qm.model.WaveSetup;
import edu.colorado.phet.qm.model.Wavefunction;

/**
 * User: Sam Reid
 * Date: Jun 27, 2005
 * Time: 1:27:45 PM
 * Copyright (c) Jun 27, 2005 by Sam Reid
 */

public class AddParticle implements ModelElement {

    private SchrodingerModule module;
    private WaveSetup waveSetup;

    public AddParticle( SchrodingerModule module, WaveSetup waveSetup ) {
        this.module = module;
        this.waveSetup = waveSetup;
    }

    public void stepInTime( double dt ) {
        Wavefunction newParticle = new Wavefunction( module.getDiscreteModel().getWavefunction().getWidth(), module.getDiscreteModel().getWavefunction().getHeight() );
        waveSetup.initialize( newParticle );

        module.getDiscreteModel().getWavefunction().add( newParticle );
    }
}
