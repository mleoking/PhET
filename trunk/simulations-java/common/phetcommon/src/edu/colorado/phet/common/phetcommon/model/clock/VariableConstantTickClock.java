/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.model.clock;

import edu.colorado.phet.common.phetcommon.util.EventChannel;

/**
 * VariableConstantTickClock
 * <p/>
 * A Clock that reports a constant amount of simulation time passed for
 * each wrappedClock tick, but that time can be changed explicitly
 * while the clock is running. This allows for the execution rate of the
 * simulation to be easily changed. This is useful, for example, to put
 * a simulation into slow motion.
 * <p/>
 * This class is implemented as a decorator on an IClock, adding a method
 * that allows clients to change the time reported each time the wrappedClock
 * ticks.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class VariableConstantTickClock implements IClock, ClockListener {
    private IClock wrappedClock;
    private double dt;
    private EventChannel clockEventChannel = new EventChannel( ClockListener.class );
    private ClockListener clockListenerProxy = (ClockListener) clockEventChannel.getListenerProxy();

    /**
     * @param clock The IClock that is to be decorated
     * @param dt    The dt that the clock is to start with
     */
    public VariableConstantTickClock( IClock clock, double dt ) {
        this.wrappedClock = clock;
        wrappedClock.addClockListener( this );
        this.dt = dt;
    }

    /**
     * Sets the amount of simulation time that elapses for each wrappedClock tick.
     *
     * @param dt
     */
    public void setDt( double dt ) {
        this.dt = dt;
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of ClockListener
    //--------------------------------------------------------------------------------------------------

    public void clockTicked( ClockEvent clockEvent ) {
        ClockEvent proxyEvent = new ClockEvent( this );
        clockListenerProxy.clockTicked( proxyEvent );
    }

    public void clockStarted( ClockEvent clockEvent ) {
        ClockEvent proxyEvent = new ClockEvent( this );
        clockListenerProxy.clockStarted( proxyEvent );
    }

    public void clockPaused( ClockEvent clockEvent ) {
        ClockEvent proxyEvent = new ClockEvent( this );
        clockListenerProxy.clockPaused( proxyEvent );
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
        ClockEvent proxyEvent = new ClockEvent( this );
        clockListenerProxy.simulationTimeChanged( proxyEvent );
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
        ClockEvent proxyEvent = new ClockEvent( this );
        clockListenerProxy.simulationTimeReset( proxyEvent );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of IClock
    //--------------------------------------------------------------------------------------------------

    /**
     * Overrides
     */
    public double getSimulationTimeChange() {
        return dt;
    }

    public void addClockListener( ClockListener clockListener ) {
        clockEventChannel.addListener( clockListener );
    }

    public void removeClockListener( ClockListener clockListener ) {
        clockEventChannel.removeListener( clockListener );
    }

    /**
     * Forwards
     */
    public void start() {
        wrappedClock.start();
    }

    public void pause() {
        wrappedClock.pause();
    }

    public boolean isPaused() {
        return wrappedClock.isPaused();
    }

    public boolean isRunning() {
        return wrappedClock.isRunning();
    }

    public void resetSimulationTime() {
        wrappedClock.resetSimulationTime();
    }

    public long getWallTime() {
        return wrappedClock.getWallTime();
    }

    public long getWallTimeChange() {
        return wrappedClock.getWallTimeChange();
    }

    public double getSimulationTime() {
        return wrappedClock.getSimulationTime();
    }

    public void setSimulationTime( double simulationTime ) {
        wrappedClock.setSimulationTime( simulationTime );
    }

    public void stepClockWhilePaused() {
        wrappedClock.stepClockWhilePaused();
    }

    public void stepClockBackWhilePaused() {
        wrappedClock.stepClockBackWhilePaused();
    }

    public boolean containsClockListener( ClockListener clockListener ) {
        return wrappedClock.containsClockListener( clockListener );
    }
}
