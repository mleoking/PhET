/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.states;

import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticleState;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:50:31 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public class Waiting implements BandParticleState {
    public Waiting() {
    }

    public boolean stepInTime( final BandParticle particle, double dt ) {
//        particle.setVelocity(new PhetVector());
        return false;
    }

    public boolean isMoving() {
        return false;
    }

}
