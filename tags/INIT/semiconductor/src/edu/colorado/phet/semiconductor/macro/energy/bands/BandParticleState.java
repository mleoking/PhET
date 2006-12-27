package edu.colorado.phet.semiconductor.macro.energy.bands;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:48:50 PM
 * Copyright (c) Jan 18, 2004 by Sam Reid
 */
public interface BandParticleState {
    /**
     * Returns true if the state is complete.
     */
    boolean stepInTime( BandParticle particle, double dt );

    boolean isMoving();

}
