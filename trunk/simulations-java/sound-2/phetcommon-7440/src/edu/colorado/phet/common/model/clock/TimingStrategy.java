/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

/**
 * Specifies the flow of time in a PhET simulation.  This involves converting wall time to simulation time (if necessary),
 * and the time that should elapse when the user manually advances time (for example, by pressing the "step forward" button.)
 */
public interface TimingStrategy {

    /**
     * Converts the wall-time change to a simulation time change.
     *
     * @param lastWallTime    the wall time of the last tick in milliseconds
     * @param currentWallTime the time of the current tick in milliseconds
     * @return the simulation time change corresponding to the change in wall time
     */
    double getSimulationTimeChange( long lastWallTime, long currentWallTime );

    /**
     * Determine how much simulation time should pass if the clock is paused, and the user presses 'frame advance'
     *
     * @return the simulation time.
     */
    double getSimulationTimeChangeForPausedClock();

    /**
     * A Constant is independent of the elapsed wall time.
     */
    public static class Constant implements TimingStrategy {
        private double simulationTimeChange;

        /**
         * Construct a Constant TimeConverter with the specified constant change in simulation time per tick.
         *
         * @param simulationTimeChange the specified constant change in simulation time per tick.
         */
        public Constant( double simulationTimeChange ) {
            this.simulationTimeChange = simulationTimeChange;
        }

        /**
         * Returns the constant value for the simulation time change.
         *
         * @param lastWallTime    the wall time of the last tick in milliseconds
         * @param currentWallTime the time of the current tick in milliseconds
         * @return the simulation time change corresponding to the change in wall time
         */
        public double getSimulationTimeChange( long lastWallTime, long currentWallTime ) {
            return simulationTimeChange;
        }

        public double getSimulationTimeChangeForPausedClock() {
            return simulationTimeChange;
        }
    }

    /**
     * The Identity TimeConverter simply converts the elapsed wall time in milliseconds to seconds for simulation time.
     */
    public static class Identity implements TimingStrategy {
        double simulationTimeChangeForPausedClock;

        public Identity( double simulationTimeChangeForPausedClock ) {
            this.simulationTimeChangeForPausedClock = simulationTimeChangeForPausedClock;
        }

        /**
         * Converts the elapsed wall time in milliseconds to seconds for simulation time.
         *
         * @param lastWallTime
         * @param currentWallTime
         * @return the corresponding simulation time.
         */
        public double getSimulationTimeChange( long lastWallTime, long currentWallTime ) {
            return ( currentWallTime - lastWallTime ) / 1000.0;
        }

        public double getSimulationTimeChangeForPausedClock() {
            return simulationTimeChangeForPausedClock;
        }
    }

    /**
     * The Scaled TimeConverter converts the elapsed wall time in milliseconds to seconds for simulation time, then multiplies by a specified scale value.
     */
    public static class Scaled implements TimingStrategy {
        private double scale;
        private double simulationTimeChangeForPausedClock;

        /**
         * Construct a Scaled TimeConverter with the specified scale.
         *
         * @param scale
         */
        public Scaled( double scale, double simulationTimeChangeForPausedClock ) {
            this.scale = scale;
            this.simulationTimeChangeForPausedClock = simulationTimeChangeForPausedClock;
        }

        /**
         * Converts elapsed wall time to simulation time by converting to seconds and scaling.
         *
         * @param lastWallTime
         * @param currentWallTime
         * @return the corresponding (scaled) simulation time.
         */
        public double getSimulationTimeChange( long lastWallTime, long currentWallTime ) {
            return ( currentWallTime - lastWallTime ) * scale / 1000.0;
        }

        public double getSimulationTimeChangeForPausedClock() {
            return simulationTimeChangeForPausedClock;
        }
    }
}