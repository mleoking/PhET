/**
 * Class: AbstractClock
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.common_cck.model.clock;

import java.util.ArrayList;

public abstract class AbstractClock {
    private CompositeClockTickListener timeListeners = new CompositeClockTickListener();
    private double runningTime;
    private ArrayList clockStateListeners = new ArrayList();
    private TickConverter tickConverter;
    private int delay;
    private static final int NOT_STARTED = 1;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;
    private static final int DEAD = 3;
    private int executionState = NOT_STARTED;
    private double dt;

    public AbstractClock( double dt, int delay, boolean isFixed ) {
        if( isFixed ) {
            tickConverter = new Static();
        }
        else {
            tickConverter = new TimeScaling();
        }
        this.delay = delay;
        this.dt = dt;
    }

    public CompositeClockTickListener getTimeListeners() {
        return timeListeners;
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

    public void addClockStateListener( ClockStateListener csl ) {
        clockStateListeners.add( csl );
    }

    public double getRunningTime() {
        return runningTime;
    }

    public synchronized void start() {
        if( executionState == NOT_STARTED || executionState == DEAD ) {
            doStart();
            setRunningTime( 0 );
            this.executionState = RUNNING;
        }
        else {
            throw new RuntimeException( "Clock cannot be started twice." );
        }
    }

    public void setPaused( boolean paused ) {
        if( paused ) {
            if( executionState == RUNNING ) {
                this.executionState = PAUSED;
            }
            else {
                throw new RuntimeException( "Only running clocks can be paused." );
            }
        }
        else {
            if( executionState == PAUSED ) {
                doUnpause();
                this.executionState = RUNNING;
            }
            else {
                throw new RuntimeException( "Only paused clocks can be unpaused." );
            }
        }
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
        timeListeners.clockTicked( this, dt );
    }

    public String toString() {
        return getClass().getName() + ", time=" + this.getRunningTime();
    }

    public void removeClockTickListener( ClockTickListener listener ) {
        timeListeners.removeClockTickListener( listener );
    }

    public void addClockTickListener( ClockTickListener tickListener ) {
        timeListeners.addClockTickListener( tickListener );
    }

    protected void setRunningTime( double runningTime ) {
        this.runningTime = runningTime;
    }

    protected ArrayList getClockStateListeners() {
        return clockStateListeners;
    }

    protected double getSimulationTime( long actualDelay ) {
        return tickConverter.getSimulationTime( actualDelay );
    }

    public long getDelay() {
        return delay;
    }

    public void setDt( double dt ) {
        this.dt = dt;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.dtChanged( dt );
        }
    }

    public void setDelay( int delay ) {
        this.delay = delay;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.delayChanged( delay );
        }
    }

    private interface TickConverter {
        double getSimulationTime( long wallTimeSinceLastTick );
    }

    private class TimeScaling implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt / delay * wallTimeSinceLastTick;
        }
    }

    public class Static implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt;
        }
    }
}
