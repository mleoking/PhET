/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

/**
 * A converter in which the simulation time is identical to the wall time.
 */
public class IdentityTimeConverter implements WallToSimulationTimeConverter {

    /**
     * Returns the system time as the simulation time.
     */
    public double toSimulationTime( long millis ) {
        return millis;
    }

}
