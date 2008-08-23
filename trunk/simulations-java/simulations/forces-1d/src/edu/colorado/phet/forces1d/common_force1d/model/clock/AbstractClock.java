/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.model.clock;

import edu.colorado.phet.forces1d.common_force1d.util.EventChannel;

/**
 * AbstractClock
 *
 * @author ?
 * @version $Revision$
 */
public abstract class AbstractClock {

    static public final int MILLISECONDS_PER_TICK = 0;
    static public final int FRAMES_PER_SECOND = 1;

    private double runningTime;
    private TickConverter tickConverter;
    private int delay;
    private static final int NOT_STARTED = 1;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;
    private static final int DEAD = 3;
    private int executionState = NOT_STARTED;
    private double dt;
    private EventChannel clockStateEventChannel = new EventChannel( ClockStateListener.class );
    private ClockStateListener clockStateListenerProxy = (ClockStateListener) clockStateEventChannel.getListenerProxy();
    private CompositeClockTickListener tickHandler = new CompositeClockTickListener();


    /**
     * Constructor that allows tick to be specified either in milliseconds between ticks,
     * or frames-per-second
     *
     * @param dt           The simulation time between ticks
     * @param tickSpec
     * @param tickSpecType
     * @param isFixed      Specifies if the simulation time reported at each tick is always
     *                     dt, or is scaled according to the desired tick spacing and the actual time between ticks.
     */
    public AbstractClock( double dt, int tickSpec, int tickSpecType, boolean isFixed ) {
        if ( isFixed ) {
            tickConverter = new Static();
        }
        else {
            tickConverter = new TimeScaling();
        }
        switch( tickSpecType ) {
            case FRAMES_PER_SECOND:
                this.delay = 1000 / tickSpec;
                break;
            case MILLISECONDS_PER_TICK:
                this.delay = tickSpec;
                break;
            default:
                throw new RuntimeException( "Invalid tick type" );
        }
        this.dt = dt;
    }

    public AbstractClock( double dt, int delay, boolean isFixed ) {
        if ( isFixed ) {
            tickConverter = new Static();
        }
        else {
            tickConverter = new TimeScaling();
        }
        this.delay = delay;
        this.dt = dt;
    }

    public boolean isDead() {
        return executionState == DEAD;
    }

    public boolean isRunning() {
        return executionState == RUNNING;
    }

    public double getDt() {
        return dt;
    }

    public boolean isPaused() {
        return executionState == PAUSED;
    }

    public boolean hasStarted() {
        return executionState != NOT_STARTED;
    }

    public double getRunningTime() {
        return runningTime;
    }

    public synchronized void start() {
        if ( executionState == NOT_STARTED || executionState == DEAD ) {
            doStart();
            setRunningTime( 0 );
            this.executionState = RUNNING;
        }
        else {
            throw new RuntimeException( "Clock cannot be started twice." );
        }
    }

    public void setPaused( boolean paused ) {
        if ( paused ) {
            if ( executionState == RUNNING ) {
                this.executionState = PAUSED;
                doPause();
            }
            else {
//                throw new RuntimeException( "Only running clocks can be paused." );
            }
        }
        else {
            if ( executionState == PAUSED ) {
                this.executionState = RUNNING;
                doUnpause();
            }
            else {
//                throw new RuntimeException( "Only paused clocks can be unpaused." );
            }
        }
        clockStateListenerProxy.stateChanged( new ClockStateEvent( this ) );
    }

    /**
     * The clock must be running and paused to do tickOnce().
     */
    public void tickOnce() {
        clockTicked( getSimulationTime( delay ) );
    }

    protected abstract void doPause();

    protected abstract void doUnpause();

    protected abstract void doStart();

    protected abstract void doStop();

    public void stop() {
        this.executionState = DEAD;
        doStop();
    }

    public void resetRunningTime() {
        this.runningTime = 0;
    }

    protected void clockTicked( double dt ) {
        runningTime += dt;
        ClockTickEvent event = new ClockTickEvent( this, dt );
        tickHandler.clockTicked( event );
    }

    public String toString() {
        return getClass().getName() + ", time=" + this.getRunningTime();
    }

    protected void setRunningTime( double runningTime ) {
        this.runningTime = runningTime;
    }

    protected double getSimulationTime( long actualDelay ) {
        return tickConverter.getSimulationTime( actualDelay );
    }

    public long getDelay() {
        return delay;
    }

    public void setDt( double dt ) {
        this.dt = dt;
        clockStateListenerProxy.stateChanged( new ClockStateEvent( this ) );
    }

    public void setDelay( int delay ) {
        this.delay = delay;
        clockStateListenerProxy.stateChanged( new ClockStateEvent( this ) );
    }

    public void removeClockTickListener( ClockTickListener listener ) {
        tickHandler.removeClockTickListener( listener );
    }

    public void addClockTickListener( ClockTickListener tickListener ) {
        tickHandler.addClockTickListener( tickListener );
    }

    public void addClockStateListener( ClockStateListener csl ) {
        clockStateEventChannel.addListener( csl );
    }

    public void removeClockStateListener( ClockStateListener csl ) {
        clockStateEventChannel.removeListener( csl );
    }

    public boolean containsClockTickListener( ClockTickListener tickListener ) {
        return tickHandler.containsClockTickListener( tickListener );
//        return tickEventChannel.containsListener( tickListener );
    }

    public void setStaticTickConverter() {
        setTickConverter( new Static() );
    }

    public void setTimeScalingConverter() {
        setTickConverter( new TimeScaling() );
    }

    public void setTickConverter( TickConverter tickConverter ) {
        this.tickConverter = tickConverter;
    }

    ///////////////////////////////////////////////////////////////////////
    // Inner classes
    //

    public interface TickConverter {
        double getSimulationTime( long wallTimeSinceLastTick );
    }

    public class Static implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt;
        }
    }

    public class TimeScaling implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt / delay * wallTimeSinceLastTick;
        }
    }

}
