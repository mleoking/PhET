package edu.colorado.phet.semiconductor_semi.macro.energy.bands;


/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 6:48:50 PM
 *
 */
public interface BandParticleState {
    /**
     * Returns true if the state is complete.
     */
    boolean stepInTime( BandParticle particle, double dt );

}
