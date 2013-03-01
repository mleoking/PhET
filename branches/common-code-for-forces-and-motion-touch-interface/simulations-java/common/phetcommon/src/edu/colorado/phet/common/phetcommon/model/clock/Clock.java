// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.model.clock;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This default clock implementation handles ClockListeners
 * and time conversion through an abstraction.
 */

public abstract class Clock implements IClock {
    private ArrayList listeners = new ArrayList();
    private TimingStrategy timingStrategy;
    private double lastSimulationTime = 0.0;
    private double simulationTime = 0.0;
    private long lastWallTime = 0;
    private long wallTime = 0;

    /**
     * Construct a Clock to use the specified conversion between Wall and Simulation time,
     * with the specified single-tick simulation time change (used by <code>tickOnce</code>).
     *
     * @param timingStrategy
     */
    public Clock( TimingStrategy timingStrategy ) {
        this.timingStrategy = timingStrategy;
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
     * Adds a listener that is notified when the simulation time changes.  The listener receives the value dt as a parameter.
     * Note: this method does not allow for removal of the listener, do not use this method if you need to remove the listener.
     * This method was written to enable closure-folding at call sites, since this method is commonly used.
     *
     * @param listener the function to be called when the simulation time changes.
     */
    public void addSimulationTimeChangeListener( final VoidFunction1<Double> listener ) {
        addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                listener.apply( clockEvent.getSimulationTimeChange() );
            }
        } );
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
     * Update the clock, updating the wall time and possibly simulation time.
     */
    protected void tick( double simulationTimeChange ) {
        lastWallTime = wallTime;
        wallTime = System.currentTimeMillis();

        setSimulationTimeNoUpdate( simulationTime + simulationTimeChange );

        notifyClockTicked();
        testNotifySimulationTimeChange();
    }

    /**
     * Advance the clock the time produced by converting the wall time.
     */
    protected void doTick() {
        tick( timingStrategy.getSimulationTimeChange( lastWallTime, wallTime ) );
    }

    /**
     * Advance the clock by the tickOnceTimeChange.
     */
    public void stepClockWhilePaused() {
        tick( timingStrategy.getSimulationTimeChangeForPausedClock() );
    }

    /**
     * Move the clock backwards by the tickOnceTimeChange.
     */
    public void stepClockBackWhilePaused() {
        tick( -timingStrategy.getSimulationTimeChangeForPausedClock() );
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
     * Gets the TimingStrategy, responsible for converting wall to simulation time.
     *
     * @return the TimingStrategy
     */
    public TimingStrategy getTimingStrategy() {
        return timingStrategy;
    }

    /**
     * Sets the TimingStrategy, responsible for determining the simulation timing.
     *
     * @param timingStrategy
     */
    public void setTimingStrategy( TimingStrategy timingStrategy ) {
        this.timingStrategy = timingStrategy;
    }

    /**
     * Sends out notification that the clock ticked.
     */
    protected void notifyClockTicked() {
        ClockEvent clockEvent = new ClockEvent( this );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener) listeners.get( i );
            clockListener.clockTicked( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was paused.
     */
    protected void notifyClockPaused() {
        ClockEvent clockEvent = new ClockEvent( this );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener) listeners.get( i );
            clockListener.clockPaused( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was started.
     */
    protected void notifyClockStarted() {
        ClockEvent clockEvent = new ClockEvent( this );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener) listeners.get( i );
            clockListener.clockStarted( clockEvent );
        }
    }

    /**
     * Sends out notification that the clock was reset.
     */
    protected void notifySimulationTimeReset() {
        ClockEvent clockEvent = new ClockEvent( this );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener) listeners.get( i );
            clockListener.simulationTimeReset( clockEvent );
        }
    }


    private void testNotifySimulationTimeChange() {
        if ( getSimulationTimeChange() != 0.0 ) {
            notifySimulationTimeChanged();
        }
    }

    /**
     * Sends notification the simulation time changed.
     */
    protected void notifySimulationTimeChanged() {
        ClockEvent clockEvent = new ClockEvent( this );
        for ( int i = 0; i < listeners.size(); i++ ) {
            ClockListener clockListener = (ClockListener) listeners.get( i );
            clockListener.simulationTimeChanged( clockEvent );
        }
    }

    /**
     * This method releases all resources associated with the clock.
     */
    public void stop() {
        this.pause();
    }

    public boolean containsClockListener( ClockListener clockListener ) {
        return listeners.contains( clockListener );
    }

    public void removeAllClockListeners() {
        listeners.clear();
    }
}