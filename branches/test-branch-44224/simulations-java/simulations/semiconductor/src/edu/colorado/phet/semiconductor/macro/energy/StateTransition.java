package edu.colorado.phet.semiconductor.macro.energy;

import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;

/**
 * User: Sam Reid
 * Date: Mar 16, 2004
 * Time: 9:54:05 PM
 */
public abstract class StateTransition {
    public abstract BandParticleState getState( BandParticle particle, EnergySection section );

    public boolean apply( BandParticle particle, EnergySection section ) {
        BandParticleState result = getState( particle, section );
        if ( result != null ) {
            particle.setState( result );
            return true;
        }
        else {
            return false;
        }
    }
}
