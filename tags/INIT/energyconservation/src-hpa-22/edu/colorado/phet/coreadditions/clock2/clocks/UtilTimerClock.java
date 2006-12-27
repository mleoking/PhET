/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2.clocks;

import edu.colorado.phet.coreadditions.clock2.*;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Clock implementation that uses the java.util.Timer to create events.
 */
public class UtilTimerClock implements AbstractClock {
    CompositeTickListener ctl = new CompositeTickListener();
    CompositeClockStateListener stateListeners = new CompositeClockStateListener();
    int requestedWaitTime;
    private Timer timer;
    private MyTask task;
    boolean running = false;

    public UtilTimerClock( int requestedWaitTime ) {
        this.requestedWaitTime = requestedWaitTime;
        timer = new Timer();
        task = new MyTask();
    }

    private class MyTask extends TimerTask {
        public void run() {
            ctl.clockTicked( UtilTimerClock.this );
        }
    }

    public void addTickListener( TickListener listener ) {
        ctl.addTickListener( listener );
    }

    public void start() {
        timer.scheduleAtFixedRate( task, 0, requestedWaitTime );
        running = true;
        stateListeners.clockStarted( this );
    }

    public void stop() {
        timer.cancel();
        timer = new Timer();
        running = false;
        stateListeners.clockStopped( this );
    }

    public void kill() {
        timer.cancel();
        running = false;
        stateListeners.clockDestroyed( this );
    }

    public void removeTickListener( TickListener t ) {
        ctl.removeTickListener( t );
    }

    public void addClockStateListener( ClockStateListener listener ) {
        stateListeners.addClockStateListener( listener );
    }

    public void setRequestedDelay( int requestedWaitTime ) {
        stop();
        this.requestedWaitTime = requestedWaitTime;
        if( running ) {
            timer.scheduleAtFixedRate( task, 0, requestedWaitTime );
        }
        stateListeners.clockDelayChanged( this, requestedWaitTime );
    }

    public int getRequestedDelay() {
        return requestedWaitTime;
    }

    public boolean isRunning() {
        return running;
    }
}
