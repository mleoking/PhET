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

import java.util.ArrayList;

/**
 * This default clock implementation handles ClockListeners
 * and time conversion through an abstraction.
 */

public abstract class Clock implements IClock {
    private ArrayList listeners = new ArrayList();
    private TimeConverter timeConverter;
    private double lastSimulationTime = 0.0;
    private double simulationTime = 0.0;
    private long lastWallTime = 0;
    private long wallTime = 0;
    private double tickOnceTimeChange;//only used when someone calls tickOnce()

    /**
     * Construct a Clock to use the specified conversion between Wall and Simulation time,
     * with the specified single-tick simulation time change (used by <code>tickOnce</code>).
     *
     * @param timeConverter
     * @param tickOnceTimeChange
     */
    public Clock( TimeConverter timeConverter, double tickOnceTimeChange ) {
        this.timeConverter = timeConverter;
        this.tickOnceTimeChange = tickOnceTimeChange;
    }

    /**
     * Add a ClockListener to this Clock.
     *
     * @param clockListener
     */
    public void addClockListener( ClockListener clockListener ) {
        listeners.add( clockListener );
    }

    /**
     * Remove a ClockListener from this Clock.
     *
     * @param clockListener
     */
    public void removeClockListener( ClockListener clockListener ) {
        listeners.remove( clockListener );
    }

    /**
     * Set the simulation time to zero.
     * This may fire a simulation time change (if the time was nonzero).
     */
    public void resetSimulationTime() {
        setSimulationTime( 0.0 );
        notifySimulationTimeReset();
        testNotifySimulationTimeChange();
    }

    /**
     * Update the clock, updating wall time and possibly simulation time.
     */
    protected void doTick() {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTimeNoUpdate( simulationTime + timeConverter.getSimulationTimeChange( lastWallTime, wallTime ) );

        notifyClockTicked();
        testNotifySimulationTimeChange();
    }

    /**
     * Advance the clock by one tick.
     */
    public void tickOnce() {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTimeNoUpdate( simulationTime + tickOnceTimeChange );

        notifyClockTicked();
        testNotifySimulationTimeChange();
    }

    private void testNotifySimulationTimeChange() {
        if( getSimulationTimeChange() != 0.0 ) {
            notifySimulationTimeChanged();
        }
    }

    private void setSimulationTimeNoUpdate( double simulationTime ) {
        lastSimulationTime = this.simulationTime;
        this.simulationTime = simulationTime;
    }

    /**
     * Determine how much wall-time changed since the previous tick.
     *
     * @return how much wall-time changed since the previous tick.
     */
    public long getWallTimeChange() {
        return wallTime - lastWallTime;
    }

    /**
     * Get the time change in simulation time units.
     *
     * @return the time change in simulation time units.
     */
    public double getSimulationTimeChange() {
        return simulationTime - lastSimulationTime;
    }

    /**
     * Get the current running time of the simulation.
     *
     * @return the current running time of the simulation.
     */
    public double getSimulationTime() {
        return simulationTime;
    }

    /**
     * Determine the last read wall-clock time.
     *
     * @return the last read wall-clock time.
     */
    public long getWallTime() {
        return wallTime;
    }

    /**
     * Specify an exact simulation time.  This may fire a simulation time change event.
     *
     * @param simulationTime
     */
    public void setSimulationTime( double simulationTime ) {
        setSimulationTimeNoUpdate( simulationTime );
        testNotifySimulationTimeChange();
    }

    /**
     * Gets the TimeConverter, responsible for converting wall to simulation time.
     *
     * @return the TimeConverter
     */
    public TimeConverter getTimeConverter() {
        return timeConverter;
    }

    /**
     * Sets the TimeConverter, responsible for converting wall to simulation time.
     *
     * @param timeConverter
     */
    public void setTimeConverter( TimeConverter timeConverter ) {
        this.timeConverter = timeConverter;
    }

    /**
     * Sends out notification that the clock ticked.
     */
    protected void notifyClockTicked() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockTicked( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was paused.
     */
    protected void notifyClockPaused() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockPaused( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was started.
     */
    protected void notifyClockStarted() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.clockStarted( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was reset.
     */
    protected void notifySimulationTimeReset() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.simulationTimeReset( clockEvent );
        }
    }

    /**
     * Sends notification the simulation time changed.
     */
    protected void notifySimulationTimeChanged() {
        ClockEvent clockEvent = new ClockEvent( this );
        for( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener)listeners.get( i );
            clockListener.simulationTimeChanged( clockEvent );
        }
    }

    /**
     * Get the simulation time to pass during tickOnce().
     *
     * @return the simulation time to pass during tickOnce().
     */
    public double getTickOnceTimeChange() {
        return tickOnceTimeChange;
    }

    /**
     * Specifies the simulation time to pass during tickOnce().
     *
     * @param tickOnceTimeChange the simulation time to pass during tickOnce().
     */
    public void setTickOnceTimeChange( double tickOnceTimeChange ) {
        this.tickOnceTimeChange = tickOnceTimeChange;
    }

}