package edu.colorado.phet.common.model;

import java.util.ArrayList;

public class Clock extends CompositeClockTickListener implements Runnable {
    CompositeClockTickListener listeners;
    boolean isRunning = false;
    boolean isAlive = false;
    boolean isSingleStepEnabled = false;
    boolean started = false;

    private ClockTickListener parent;
    double dt;
    int waitTime;
    private ThreadPriority priority;
    double timeLimit = Double.POSITIVE_INFINITY;
    double runningTime;
    private Thread t;
    ArrayList clockStateListeners = new ArrayList();

    public Clock(ClockTickListener parent, double dt, int waitTime, ThreadPriority priority) {
        this.parent = parent;
//        this.system = system;
        this.dt = dt;
        this.waitTime = waitTime;
        this.priority = priority;
        this.t = new Thread(this);
        t.setPriority(priority.intValue());
    }

    public void addClockStateListener(ClockStateListener csl) {
        clockStateListeners.add(csl);
    }

    public double getRunningTime() {
        return runningTime;
    }

    public double getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(double timeLimit) {
        this.timeLimit = timeLimit;
    }

    public double getDt() {
        return dt;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setDt(double dt) {
        this.dt = dt;
        for (int i = 0; i < clockStateListeners.size(); i++) {
            ClockStateListener clockStateListener = (ClockStateListener) clockStateListeners.get(i);
            clockStateListener.dtChanged(dt);
        }
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        for (int i = 0; i < clockStateListeners.size(); i++) {
            ClockStateListener clockStateListener = (ClockStateListener) clockStateListeners.get(i);
            clockStateListener.waitTimeChanged(waitTime);
        }
    }

    public void setThreadPriority(ThreadPriority tp) {
        t.setPriority(tp.intValue());
        this.priority = tp;
        for (int i = 0; i < clockStateListeners.size(); i++) {
            ClockStateListener clockStateListener = (ClockStateListener) clockStateListeners.get(i);
            clockStateListener.threadPriorityChanged(tp);
        }
    }

    public boolean isActiveAndRunning() {
        return isAlive && isRunning;
    }

    public synchronized void setTimeIncrement(double dt) {
        this.dt = dt;
    }

    public void start() {
        if (started) {
            throw new RuntimeException("Already started.");
        } else {
            t.start();
            started = true;
        }
    }

    public void setAlive(boolean b) {
        this.isAlive = b;
    }

    public void setRunning(boolean b) {
        this.isRunning = b;
    }

    public boolean isSingleStepEnabled() {
        return isSingleStepEnabled;
    }

    public void setSingleStepEnabled(boolean singleStepEnabled) {
        isSingleStepEnabled = singleStepEnabled;
    }

    public void stop() {
        setRunning(false);
        setAlive(false);
    }

    public void reset() {
        stop();
        this.runningTime = 0;
    }

    public void tickOnce() {
        super.clockTicked(this, dt);
    }

    public void run() {
        this.isAlive = true;
        this.isRunning = true;
        runningTime = 0;
        while (isAlive) {
            parent.clockTicked(this, dt);
            if (isRunning) {
                tickOnce();
            }
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
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
}

