/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.coreadditions.clock2.clocks;

import edu.colorado.phet.coreadditions.clock2.*;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Clock implementation that uses the javax.swing.Timer class to cause events.
 */
public class SwingTimerClock implements AbstractClock {
    CompositeTickListener compositeTickListener = new CompositeTickListener();
    CompositeClockStateListener stateListeners = new CompositeClockStateListener();
    int requestedWaitTime;
    private Timer timer;
    boolean running=false;

    public SwingTimerClock(int requestedWaitTime) {
        this.requestedWaitTime = requestedWaitTime;
        timer = new Timer(requestedWaitTime, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                compositeTickListener.clockTicked(SwingTimerClock.this);
            }
        });
    }

    public void addClockStateListener(ClockStateListener csl) {
        stateListeners.addClockStateListener(csl);
    }

    public void setRequestedDelay(int requestedWaitTime) {
        this.requestedWaitTime = requestedWaitTime;
        timer.setDelay(requestedWaitTime);
        stateListeners.clockDelayChanged(this, requestedWaitTime);
    }

    public int getRequestedDelay() {
        return requestedWaitTime;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        timer.start();
        stateListeners.clockStarted(this);
        running=true;
    }

    public void stop() {
        timer.stop();
        stateListeners.clockStopped(this);
        running=false;
    }

    public void kill() {
        timer.stop();
        timer = null;
        stateListeners.clockDestroyed(this);
        running=false;
    }

    public void removeTickListener(TickListener t) {
        compositeTickListener.removeTickListener(t);
    }

    public void addTickListener(TickListener listener) {
        compositeTickListener.addTickListener(listener);
    }

}
