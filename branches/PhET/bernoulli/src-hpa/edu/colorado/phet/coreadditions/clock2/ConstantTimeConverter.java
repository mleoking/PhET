/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2;

/**
 * Provides a constant mapping from wall time to simulation time.
 */
public class ConstantTimeConverter implements WallToSimulationTimeConverter {
    double timePerTick;

    public ConstantTimeConverter(double timePerTick) {
        this.timePerTick = timePerTick;
    }

    /**Returns the same time per tick regardless of the actual system time.*/
    public double toSimulationTime(long millis) {
        return timePerTick;
    }

    public void setTimePerTick(double timePerTick) {
        this.timePerTick = timePerTick;
    }


}
