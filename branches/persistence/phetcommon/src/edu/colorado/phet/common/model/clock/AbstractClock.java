/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;

/**
 * AbstractClock
 *
 * @author ?
 * @version $Revision$
 */
public abstract class AbstractClock {
//    private CompositeClockTickListener timeListeners = new CompositeClockTickListener();
    private double runningTime;
    private TickConverter tickConverter;
    private int delay;
    private static final int NOT_STARTED = 1;
    private static final int RUNNING = 1;
    private static final int PAUSED = 2;
    private static final int DEAD = 3;
    private int executionState = NOT_STARTED;
    private double dt;
    private EventListenerList eventRegistry = new EventListenerList();

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

//    public CompositeClockTickListener getTimeListeners() {
//        return timeListeners;
//    }

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
        ClockTickEvent event = new ClockTickEvent( this, dt );
        Object[] listeners = eventRegistry.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ClockTickListener.class ) {
                ( (ClockTickListener)listeners[i + 1] ).clockTicked( event );
            }
        }
    }

    public String toString() {
        return getClass().getName() + ", time=" + this.getRunningTime();
    }

    protected void setRunningTime( double runningTime ) {
        this.runningTime = runningTime;
    }

    protected ArrayList getClockStateListeners() {
        ArrayList clockStateListeners = new ArrayList();
        Object[] listeners = eventRegistry.getListenerList();
        for( int i = 0; i < listeners.length; i++ ) {
            Object listener = listeners[i];
            if( listener instanceof ClockStateListener ) {
                clockStateListeners.add( listener );
            }
        }
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
        fireClockStateEvent();
    }

    public void setDelay( int delay ) {
        this.delay = delay;
        fireClockStateEvent();
    }

    protected void fireClockStateEvent() {
        ClockStateEvent event = new ClockStateEvent( this );
        Object[] listeners = eventRegistry.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ClockStateListener.class ) {
                ( (ClockStateListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }

    public void removeClockTickListener( ClockTickListener listener ) {
        eventRegistry.remove( ClockTickListener.class, listener );
    }

    public void addClockTickListener( ClockTickListener tickListener ) {
        eventRegistry.add( ClockTickListener.class, tickListener );
    }

    public void addClockStateListener( ClockStateListener csl ) {
        eventRegistry.add( ClockStateListener.class, csl );
    }

    public void removeClockStateListener( ClockStateListener csl ) {
        eventRegistry.remove( ClockStateListener.class, csl );
    }

    ///////////////////////////////////////////////////////////////////////
    // Inner classes
    //

    public class Static implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt;
        }
    }

    private class TimeScaling implements TickConverter {
        public double getSimulationTime( long wallTimeSinceLastTick ) {
            return dt / delay * wallTimeSinceLastTick;
        }
    }

    private interface TickConverter {
        double getSimulationTime( long wallTimeSinceLastTick );
    }

}
