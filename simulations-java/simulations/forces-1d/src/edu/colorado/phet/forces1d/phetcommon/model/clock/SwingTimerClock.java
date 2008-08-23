/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.model.clock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

/**
 * SwingTimerClock
 *
 * @author ?
 * @version $Revision$
 */
public class SwingTimerClock extends AbstractClock {

    private Timer timer;
    private long lastTickTime;
    private DelayStrategy delayStrategy;

    /**
     * Clock specified on terms of milliseconds between ticks
     *
     * @param dt
     * @param delay
     */
    public SwingTimerClock( double dt, int delay ) {
        this( dt, delay, true );
    }

    /**
     * Constructor that allows tick to be specified either in millisecond between ticks,
     * or frames-per-second
     *
     * @param dt
     * @param tickSpec
     * @param tickSpecType
     */
    public SwingTimerClock( double dt, int tickSpec, int tickSpecType ) {
        this( dt, tickSpec, tickSpecType, true );
    }

    /**
     * Clock specified in terms of milliseconds between ticks
     *
     * @param dt
     * @param delay
     * @param isFixed
     */
    public SwingTimerClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        timer = new Timer( delay, new Ticker() );
        delayStrategy = new DynamicDelay( delay );
    }

    /**
     * Constructor that allows tick to be specified either in millisecond between ticks,
     * or frames-per-second
     *
     * @param dt
     * @param tickSpec
     * @param tickSpecType
     * @param isFixed
     */
    public SwingTimerClock( double dt, int tickSpec, int tickSpecType, boolean isFixed ) {
        super( dt, tickSpec, tickSpecType, isFixed );
        timer = new Timer( (int) super.getDelay(), new Ticker() );
        delayStrategy = new DynamicDelay( (int) super.getDelay() );
    }

    public void doStart() {
        lastTickTime = System.currentTimeMillis();
        timer.start();
    }

    public void setDelay( int delay ) {
        super.setDelay( delay );
        timer.setDelay( delay );
        this.delayStrategy = new ConstantDelay( delay );
    }

    protected void doStop() {
        timer.stop();
    }

    protected void doPause() {
        timer.stop();
    }

    protected void doUnpause() {
        lastTickTime = System.currentTimeMillis();
        timer.restart();
    }

    private static interface DelayStrategy {
        int getDelay( long actualWaitTime );
    }

    private static class ConstantDelay implements DelayStrategy {
        private int delay;

        public ConstantDelay( int delay ) {
            this.delay = delay;
        }

        public int getDelay( long actualWaitTime ) {
            return delay;
        }
    }

    private static class DynamicDelay implements DelayStrategy {
        private int delay;
        private long lastRequestedDelay;
        private int min;

        public DynamicDelay( int delay ) {
            this( delay, 5 );
        }

        public DynamicDelay( int delay, int min ) {
            this.delay = delay;
            this.min = min;
        }

        /**
         * Determines the amount of time to ask the clock to sleep before the next tick, based on
         * how much time actually elapsed between the last two ticks. This computation compensates
         * for overhead in the timing loop. If, for example, the nominal delay is 10ms, but the last
         * loop took 12ms, this method will return 8ms as the request for the delay in the next tick.
         *
         * @param actualWaitTime
         * @return
         */
        public int getDelay( long actualWaitTime ) {
            long dt = actualWaitTime - lastRequestedDelay;
            long nextRequest = delay - dt;
            int result = Math.min( delay, Math.max( (int) nextRequest, min ) );
            lastRequestedDelay = result;
            return result;
        }
    }

    private class Ticker implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            if ( isRunning() ) {
                long tickTime = System.currentTimeMillis();
                long actualWaitTime = tickTime - lastTickTime;
                int delay = delayStrategy.getDelay( actualWaitTime );
                timer.setDelay( delay );
                lastTickTime = tickTime;
                if ( isRunning() ) {
                    clockTicked( getSimulationTime( actualWaitTime ) );
                }
            }
        }
    }
}
