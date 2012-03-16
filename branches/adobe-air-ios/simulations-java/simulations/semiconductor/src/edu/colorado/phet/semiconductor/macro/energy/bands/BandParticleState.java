// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.semiconductor.macro.energy.bands;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:48:50 PM
 */
public interface BandParticleState {
    /**
     * Returns true if the state is complete.
     */
    boolean stepInTime( BandParticle particle, double dt );

}
