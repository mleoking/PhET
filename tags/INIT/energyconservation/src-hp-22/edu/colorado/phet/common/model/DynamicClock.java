package edu.colorado.phet.common.model;

import java.util.ArrayList;

public class DynamicClock implements IClock {
    CompositeClockTickListener listeners = new CompositeClockTickListener();
    boolean isRunning = false;//When running, the clock sends tick messages to listeners
    boolean isAlive = false;//The Thread has not yet exited run()
    boolean started = false;

    private ApplicationModel parent;
    protected double requestedDT;
    int requestedWaitTime;
    private ThreadPriority priority;
    double timeLimit = Double.POSITIVE_INFINITY;
    double runningTime;
    private Thread t;
    ArrayList clockStateListeners = new ArrayList();

    public DynamicClock( double dt, int waitTime, ThreadPriority priority ) {
        this.requestedDT = dt;
        this.requestedWaitTime = waitTime;
        this.priority = priority;
        this.t = new Thread( this );
        t.setPriority( priority.intValue() );
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
        return requestedDT;
    }

    public int getRequestedWaitTime() {
        return requestedWaitTime;
    }

    public void setRequestedDT( double requestedDT ) {
        this.requestedDT = requestedDT;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.dtChanged( requestedDT );
        }
    }

    public void setRequestedWaitTime( int requestedWaitTime ) {
        this.requestedWaitTime = requestedWaitTime;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.waitTimeChanged( requestedWaitTime );
        }
    }

    public void setThreadPriority( ThreadPriority tp ) {
        t.setPriority( tp.intValue() );
        this.priority = tp;
        for( int i = 0; i < clockStateListeners.size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)clockStateListeners.get( i );
            clockStateListener.threadPriorityChanged( tp );
        }
    }

    public boolean isActiveAndRunning() {
        return isAlive && isRunning;
    }

    public synchronized void setTimeIncrement( double dt ) {
        this.requestedDT = dt;
    }

    public void start() {
        if( started ) {
            throw new RuntimeException( "Already started." );
        }
        else {
            t.start();
            started = true;
        }
    }

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

    public void run() {
        this.isAlive = true;
        this.isRunning = true;
        runningTime = 0;

        while( isAlive ) {
            parent.clockTicked();

            long initialSystemTime = System.currentTimeMillis();
            try {
                Thread.sleep( requestedWaitTime );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            long finalSystemTime = System.currentTimeMillis();
            long actualWaitTime = finalSystemTime - initialSystemTime;

            if( isRunning ) {
                double actualDT = ( ( (double)actualWaitTime ) / requestedWaitTime ) * requestedDT;
                tickOnce( actualDT );
            }
        }
    }

    public String toString() {
        return getClass().getName() + ", time=" + this.getRunningTime();
    }

    public ThreadPriority getThreadPriority() {
        return priority;
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

}

