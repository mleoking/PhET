package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 1:28:56 PM
 * Copyright (c) Apr 26, 2004 by Sam Reid
 */
public interface ParticleAction {
    public void apply( BandParticle particle );
}
