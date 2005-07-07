/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.model.clock.AbstractClock;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:05:52 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleModule extends SchrodingerModule {
    public SingleParticleModule( AbstractClock clock ) {
        super( "Single Particles", clock );
        setSchrodingerPanel( new SingleParticlePanel( this ) );
        setSchrodingerControlPanel( new SingleParticleControlPanel( this ) );
    }
}
