/* Copyright 2003-2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.model.clock;

import javax.swing.*;

/**
 * This extension of Clock uses a Thread to send events.
 */
public class ThreadClock extends Clock {
    private int delay;
    private Thread thread;
    private volatile boolean timerThreadAlive;
    private volatile boolean shouldRun;
    private volatile boolean paused = false;

    /**
     * Constructs a ThreadClock with specified wall time delay between events
     * and constant simulation time change per tick.  The same dt is used for stepClockWhilePaused().
     *
     * @param delay time between clock ticks, in wall time
     * @param dt    time per clock tick, in simulation time
     */
    public ThreadClock( int delay, double dt ) {
        this( delay, new TimingStrategy.Constant( dt ) );
    }

    /**
     * Constructs a ThreadClock with a specified wall time between clock ticks,
     * and a TimingStrategy for determining elapsed simulation time.
     * <p/>
     *
     * @param delay          time between clock ticks, in wall time
     * @param timingStrategy calculates simulation time change based on wall time change
     */
    public ThreadClock( final int delay, TimingStrategy timingStrategy ) {
        super( timingStrategy );
        this.delay=delay;

    }

    /**
     * Starts the Clock.
     */
    public synchronized void start() {
        if ( isPaused() ) {
            this.paused = false;
        }
        if ( !isRunning() ) {
            shouldRun = true;

            thread = new Thread( new ClockUpdatingRunnable() );
            thread.start();

            while ( !timerThreadAlive ) {
                Thread.yield();
            }
        }
    }

    /**
     * Pauses the Clock.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Get whether the clock is paused.
     *
     * @return whether the clock is paused.
     */
    public boolean isPaused() {
        return paused;
    }

    /**
     * Get whether the clock is running.
     *
     * @return whether the clock is running.
     */
    public boolean isRunning() {
        return timerThreadAlive && !paused;
    }

    /**
     * Sets the requested wall time delay between ticks.
     *
     * @param delay
     */
    public void setDelay( int delay ) {
        this.delay = delay;
    }

    /**
     * Gets the requested wall time delay between ticks.
     *
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    public synchronized void stop() {
        super.stop();

        this.shouldRun = false;

        while ( timerThreadAlive ) {
            Thread.yield();
        }
    }

    private class ClockUpdatingRunnable implements Runnable {
        public void run() {
            timerThreadAlive = true;

            try {
                while ( shouldRun ) {
                    try {
                        long timeBeforeTick=System.currentTimeMillis();
                        
                        if ( !isPaused() ) {
                            SwingUtilities.invokeAndWait( new Runnable() {
                                public void run() {
                                    doTick();
                                }
                            } );
                        }
                        long elapsed=System.currentTimeMillis()-timeBeforeTick;

                        long remainingTime= ThreadClock.this.delay-elapsed;
    //                        System.out.println( "delay="+ThreadClock.this.delay+", elapsed = " + elapsed +", remaining="+remainingTime);
                        if ( remainingTime > 0 ) {
                            Thread.sleep( remainingTime );
                        }
                        else {
                            //work done in doTick took longer (or as long as ) requested delay time;
                            //we must yield anyways so other threads (e.g. Swing EDT) have a chance
                            Thread.yield();
                        }
                    }
                    catch( Exception e ) {
                        e.printStackTrace();
                    }
                }
            }
            finally {
                timerThreadAlive = false;
            }
        }
    }
}