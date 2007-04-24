/*, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.clock2;

/**
 * A converter in which the simulation time is identical to the wall time.
 */
public class IdentityTimeConverter implements WallToSimulationTimeConverter {

    /**Returns the system time as the simulation time.*/
    public double toSimulationTime(long millis) {
        return millis;
    }

}
