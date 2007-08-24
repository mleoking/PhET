package edu.colorado.phet.semiconductor_semi.macro.energy;

import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticle;

/**
 * User: Sam Reid
 * Date: Apr 26, 2004
 * Time: 1:28:56 PM
 */
public interface ParticleAction {
    public void apply( BandParticle particle );
}
