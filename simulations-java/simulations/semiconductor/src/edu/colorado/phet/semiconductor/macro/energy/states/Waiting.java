/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:50:31 PM
 */
public class Waiting implements BandParticleState {
    public Waiting() {
    }

    public boolean stepInTime( final BandParticle particle, double dt ) {
//        particle.setVelocity(new Vector2D.Double());
        return false;
    }

}
