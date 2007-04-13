/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.bernoulli.bernoulli.clock2;

/**
 Converts a system clock time in milliseconds to simulation time.
 */
public interface WallToSimulationTimeConverter {
    /**Convert a system clock time in milliseconds to simulation time.*/
    double toSimulationTime(long wallTimeMillis);
}
