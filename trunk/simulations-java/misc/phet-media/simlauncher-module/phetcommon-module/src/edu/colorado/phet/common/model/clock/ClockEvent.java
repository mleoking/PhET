/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/model/clock/ClockEvent.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.4 $
 * Date modified : $Date: 2006/01/03 23:37:17 $
 */
package edu.colorado.phet.common.model.clock;

/**
 * This event is passed to a ClockListener during a notification.
 */

public class ClockEvent {
    private IClock clock;

    /**
     * Construct a ClockEvent with the specified source clock.
     *
     * @param clock
     */
    public ClockEvent( IClock clock ) {
        this.clock = clock;
    }

    /**
     * Gets the source clock for this ClockEvent.
     *
     * @return the source clock for this ClockEvent.
     */
    public IClock getClock() {
        return clock;
    }

    /**
     * Determine how many milliseconds passed since the previous tick.
     *
     * @return how many milliseconds of wall-time passed since the previous tick.
     */
    public long getWallTimeChange() {
        return clock.getWallTimeChange();
    }

    /**
     * Get the time change in simulation time units.
     *
     * @return the time change in simulation time units.
     */
    public double getSimulationTimeChange() {
        return clock.getSimulationTimeChange();
    }

    /**
     * Get the current running time of the simulation.
     *
     * @return the current running time of the simulation.
     */
    public double getSimulationTime() {
        return clock.getSimulationTime();
    }

    /**
     * Determine the last read wall-clock time.
     *
     * @return the last read wall-clock time.
     */
    public long getWallTime() {
        return clock.getWallTime();
    }

}