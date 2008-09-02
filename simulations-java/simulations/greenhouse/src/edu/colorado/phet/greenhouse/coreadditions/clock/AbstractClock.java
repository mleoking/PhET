/**
 * Class: AbstractClock
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.clock;

import edu.colorado.phet.greenhouse.common_greenhouse.model.*;

import java.util.ArrayList;

public abstract class AbstractClock implements IClock {
    CompositeClockTickListener listeners = new CompositeClockTickListener();
    boolean isRunning = false;//When running, the clock sends tick messages to listeners
    boolean isAlive = false;//The Thread has not yet exited run()
    boolean started = false;
    protected ApplicationModel parent;
//    protected double requestedDT;
//    int requestedWaitTime;
    double timeLimit = Double.POSITIVE_INFINITY;
    double runningTime;
    ArrayList clockStateListeners = new ArrayList();
    private ClockModel clockModel;

    public AbstractClock( ClockModel clockModel ) {
        this.clockModel = clockModel;
//        this.requestedDT = dt;
//        this.requestedWaitTime = waitTime;
    }
//    public AbstractClock( double dt, int waitTime ) {
//        this.requestedDT = dt;
//        this.requestedWaitTime = waitTime;
//    }

    public AbstractClock() {
    }

    public void setParent( ApplicationModel parent ) {
        this.parent = parent;
    }

    public void addClockStateListener( ClockStateListener csl ) {
        clockStateListeners.add( csl );
    }

    public double getRunningTime() {
        return runningTime;
    }

    public double getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit( double timeLimit ) {
        this.timeLimit = timeLimit;
    }

    public double getRequestedDT() {
        return clockModel.getDt();
//        return requestedDT;
    }

    public int getRequestedWaitTime() {
        return clockModel.getWaitTime();
//        return requestedWaitTime;
    }

    public void setRequestedDT( double requestedDT ) {
        clockModel.setDt( requestedDT );
//        this.requestedDT = requestedDT;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.dtChanged( requestedDT );
        }
    }

    public void setRequestedWaitTime( int requestedWaitTime ) {
        clockModel.setWaitTime( requestedWaitTime );
//        this.requestedWaitTime = requestedWaitTime;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.waitTimeChanged( requestedWaitTime );
        }
    }

    public boolean isActiveAndRunning() {
        return isAlive && isRunning;
    }

    public synchronized void setTimeIncrement( double dt ) {
        clockModel.setDt( dt
        );
//        this.requestedDT = dt;
    }

    public abstract void start();

    public void setAlive( boolean b ) {
        this.isAlive = b;
    }

    public void setRunning( boolean b ) {
        this.isRunning = b;
    }

    public void stop() {
        setRunning( false );
        setAlive( false );
    }

    public void reset() {
        stop();
        this.runningTime = 0;
    }

    public void tickOnce( double dt ) {
        listeners.clockTicked( this, dt );
    }

    public abstract void run();

    public String toString() {
        return getClass().getName() + ", time=" + this.getRunningTime();
    }

    public boolean isStarted() {
        return started;
    }

    public void removeClockTickListener( ClockTickListener listener ) {
        listeners.removeClockTickListener( listener );
    }

    public void addClockTickListener( ClockTickListener tickListener ) {
        listeners.addClockTickListener( tickListener );
    }

    protected boolean isRunning() {
        return isRunning;
    }

    protected boolean isAlive() {
        return isAlive;
    }

    protected ApplicationModel getParent() {
        return parent;
    }

    protected void setStarted( boolean started ) {
        this.started = started;
    }

    protected void setRunningTime( double runningTime ) {
        this.runningTime = runningTime;
    }

    protected ArrayList getClockStateListeners() {
        return clockStateListeners;
    }
}
