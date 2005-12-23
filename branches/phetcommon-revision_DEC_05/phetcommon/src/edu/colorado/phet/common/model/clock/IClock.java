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
 * The IClock models passage of time through a Module in a PhetApplication.  <br>
 * IClocks are always in one of two states, running or paused.  All clocks may have listeners attached,
 * to receive notification of state changes or time changes.  <br><br>
 * The IClock maintains a separation between wall time and simulation time.
 */
public interface IClock {
    /**
     * Starts the clock (puts it in the running state).  If the clock was already running, nothing happens,
     * otherwise the clock starts.  It is legal to call start on an already running clock.
     * Fires a clockStarted event.
     */
    void start();

    /**
     * Pauses the clock (puts it in the paused state).  If the clock was already paused, nothing happens,
     * otherwise the clock is newly paused.  It is legal to call pause on an already paused clock.
     * <p/>
     * Fires a clockPaused event.
     */
    void pause();

    /**
     * Get whether the clock is paused.
     *
     * @return whether the clock is paused.
     */
    boolean isPaused();

    /**
     * Get whether the clock is running.
     *
     * @return whether the clock is running.
     */
    boolean isRunning();

    /**
     * Add a ClockListener to this IClock.
     *
     * @param clockListener
     */
    void addClockListener( ClockListener clockListener );

    /**
     * Remove a ClockListener from this IClock.
     *
     * @param clockListener
     */
    void removeClockListener( ClockListener clockListener );

    /**
     * Set the simulation time to zero.  This may fire a simulation time change (if the time was nonzero).
     * This method fires a simulationTimeReset, and may fire a simulationTimeChange (if there was a change
     * in the corresponding simulation time.
     */
    void resetSimulationTime();

    /**
     * Determine the last read wall-clock time.
     *
     * @return the last read wall-clock time.
     */
    long getWallTime();

    /**
     * Determine how many milliseconds passed since the previous tick.
     *
     * @return how many milliseconds of wall-time passed since the previous tick.
     */
    long getWallTimeChange();

    /**
     * Get the time change in simulation time units.
     *
     * @return the time change in simulation time units.
     */
    double getSimulationTimeChange();

    /**
     * Get the current running time of the simulation.
     *
     * @return the current running time of the simulation.
     */
    double getSimulationTime();

    /**
     * Specify an exact simulation time.  This may fire a simulation time change event.
     *
     * @param simulationTime
     */
    void setSimulationTime( double simulationTime );

    /**
     * Advance the clock by one tick.
     * This fires a clockTicked, and may also fire a simulationTimeChange event,
     * if indeed the simulation time changed.
     */
    void tickOnce();

}
