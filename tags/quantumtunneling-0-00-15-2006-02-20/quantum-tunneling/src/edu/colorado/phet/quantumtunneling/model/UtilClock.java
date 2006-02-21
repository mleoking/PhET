/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.model;

import java.util.Timer;
import java.util.TimerTask;

import edu.colorado.phet.common.model.clock.Clock;
import edu.colorado.phet.common.model.clock.TimingStrategy;


/**
 * UtilClock is a clock implementation that uses java.util.Timer.
 * All notifications are done in the application (main) thread.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class UtilClock extends Clock {

    private int delay;
    private Timer timer;

    /**
     * Constructs a UtilClock with specified wall time delay 
     * between events and constant simulation time change per tick.  
     * The same dt is used for stepClockWhilePaused().
     *
     * @param delay time between clock ticks, in wall time
     * @param dt    time per clock tick, in simulation time
     */
    public UtilClock( int delay, double dt ) {
        this( delay, new TimingStrategy.Constant( dt ) );
    }

    /**
     * Constructs a UtilClock with a specified wall time between clock ticks,
     * and a TimingStrategy for determining elapsed simulation time.
     *
     * @param delay          time between clock ticks, in wall time
     * @param timingStrategy calculates simulation time change based on wall time change
     */
    public UtilClock( int delay, TimingStrategy timingStrategy ) {
        super( timingStrategy );
        this.delay = delay;
    }

    /**
     * Starts the clock (sets its state to running).
     */
    public void start() {
        if ( isPaused() ) {
            timer = new Timer();
            timer.schedule( new UtilTask(), delay, delay );
            super.notifyClockStarted();
        }
    }

    /**
     * Pauses the clock.
     */
    public void pause() {
        if ( isRunning() ) {
            // cancelled Timers and TimerTasks cannot be reused!
            timer.cancel();
            timer = null;
            super.notifyClockPaused();
        }
    }

    /**
     * Is the clock paused?
     *
     * @return true or false
     */
    public boolean isPaused() {
        return ( timer == null );
    }

    /**
     * Is the clock running?
     *
     * @return true or false
     */
    public boolean isRunning() {
        return ( timer != null );
    }

    /**
     * Sets the requested wall time delay between ticks.
     *
     * @param delay
     */
    public void setDelay( int delay ) {
        if ( delay != this.delay ) {
            if ( isRunning() ) {
                // cancelled Timers and TimerTasks cannot be reused!
                timer.cancel();
                timer = new Timer();
                timer.schedule( new UtilTask(), delay, delay );
            }
        }
    }

    /**
     * Gets the requested wall time delay between ticks.
     *
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * TimerTask run by this clock's timer.
     */
    private class UtilTask extends TimerTask {

        public UtilTask() {
            super();
        }

        public void run() {
            if ( !isPaused() ) {
                doTick();
            }
        }
    }
}
