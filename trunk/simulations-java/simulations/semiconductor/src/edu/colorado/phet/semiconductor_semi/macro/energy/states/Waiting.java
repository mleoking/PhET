/*, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.energy.states;

import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor_semi.macro.energy.bands.BandParticleState;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:50:31 PM
 */
public class Waiting implements BandParticleState {
    public Waiting() {
    }

    public boolean stepInTime( final BandParticle particle, double dt ) {
//        particle.setVelocity(new PhetVector());
        return false;
    }

}
