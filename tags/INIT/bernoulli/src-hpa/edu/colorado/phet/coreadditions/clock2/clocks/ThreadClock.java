/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2.clocks;

import edu.colorado.phet.coreadditions.clock2.*;

/**
 * Clock implementation that uses an internally maintained Thread to fire events.
 */
public class ThreadClock implements AbstractClock {
    Thread thread;
    int requestedWaitTime;
    private int pauseWaitTime;
    private boolean alive = true;
    CompositeTickListener tickListener = new CompositeTickListener();
    CompositeClockStateListener stateListeners = new CompositeClockStateListener();
    private boolean paused = false;
    boolean running=true;
    public ThreadClock(final int requestedWaitTime, final int pauseWaitTime) {
        this.requestedWaitTime = requestedWaitTime;
        this.pauseWaitTime = pauseWaitTime;

        thread = new Thread(new Runnable() {
            public void run() {
                while (alive) {
                    if (paused) {
                        try {
                            Thread.sleep(pauseWaitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                        }
                    } else {
                        tickListener.clockTicked(ThreadClock.this);
                        try {
                            Thread.sleep(requestedWaitTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
                        }
                    }
                }
            }
        });
    }

    public void addTickListener(TickListener listener) {
        tickListener.addTickListener(listener);
    }

    public void start() {
        alive = true;
        thread.start();
        stateListeners.clockStarted(this);
        running=true;
    }

    public void stop() {
        paused = true;
        stateListeners.clockStopped(this);
        running=false;
    }

    public void kill() {
        killThread();
        running=false;
        stateListeners.clockDestroyed(this);
    }

    public void removeTickListener(TickListener t) {
        this.tickListener.removeTickListener(t);
    }

    public void addClockStateListener(ClockStateListener listener) {
        stateListeners.addClockStateListener(listener);
    }

    public void setRequestedDelay(int requestedWaitTime) {
        this.requestedWaitTime = requestedWaitTime;
        stateListeners.clockDelayChanged(this, requestedWaitTime);
    }

    public int getRequestedDelay() {
        return requestedWaitTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void killThread() {
        alive = false;
    }

    public void setPauseWaitTime(int pauseWaitTime) {
        this.pauseWaitTime = pauseWaitTime;
    }
}
