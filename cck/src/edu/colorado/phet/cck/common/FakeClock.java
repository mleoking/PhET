/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.model.ThreadPriority;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.model.clock.ClockTickListener;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 12:48:27 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class FakeClock {
    public void addClockStateListener(ClockStateListener csl) {
    }

    public double getRunningTime() {
        return 0;
    }

    public double getTimeLimit() {
        return 0;
    }

    public void setTimeLimit(double timeLimit) {
    }

    public double getRequestedDT() {
        return 0;
    }

    public int getRequestedWaitTime() {
        return 0;
    }

    public void setRequestedDT(double requestedDT) {
    }

    public void setRequestedWaitTime(int requestedWaitTime) {
    }

    public void setThreadPriority(ThreadPriority tp) {
    }

    public boolean isActiveAndRunning() {
        return false;
    }

    public void setTimeIncrement(double dt) {
    }

    public void start() {
    }

    public void setAlive(boolean b) {
    }

    public void setRunning(boolean b) {
    }

    public void stop() {
    }

    public void reset() {
    }

    public void tickOnce(double dt) {
    }

    public void run() {
    }

    public ThreadPriority getThreadPriority() {
        return null;
    }

    public boolean isStarted() {
        return false;
    }

    public void removeClockTickListener(ClockTickListener listener) {
    }

    public void addClockTickListener(ClockTickListener tickListener) {
    }

    public void setParent(ApplicationModel parent) {
    }

}

