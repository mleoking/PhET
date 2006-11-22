/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.model.clock;

import java.util.*;


/**
 * ModelClock
 * <p/>
 * A clock for running a model in its own thread.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ModelClock extends Clock {
    private int delay;
    private Timer timer;
    private boolean isRunning;
    private List workQueue = Collections.synchronizedList( new ArrayList() );

    /**
     * @param delay
     * @param dt
     */
    public ModelClock( int delay, double dt ) {
        this( delay, new TimingStrategy.Constant( dt ) );
    }

    /**
     * @param delay
     * @param timingStrategy
     */
    public ModelClock( int delay, TimingStrategy timingStrategy ) {
        super( timingStrategy );
        this.delay = delay;
        timer = new Timer();
    }

    /**
     *
     */
    public void start() {
        timer.schedule( new Ticker(), delay, delay );
        isRunning = true;
        notifyClockStarted();
    }

    /**
     *
     */
    public void pause() {
        if( isRunning() ) {
            isRunning = false;
            super.notifyClockPaused();
        }
    }

    /**
     * @return
     */
    public boolean isPaused() {
        return !isRunning;
    }

    /**
     * @return
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Puts a Runnable on the work queue, to be invoked on the
     * next clock tick
     *
     * @param workItem
     */
    public void invokeLater( Runnable workItem ) {
        workQueue.add( workItem );
    }


    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    /**
     * The TimerTask that runs when the Timer ticks
     */
    private class Ticker extends TimerTask {
        public void run() {
            if( isRunning() ) {
                // Process the work queue
                while( workQueue.size() > 0 ) {
                    Runnable workItem = (Runnable)workQueue.remove( 0 );
                    workItem.run();
                }

                // Do superclass stuff. Includes notifying listeners
                ModelClock.super.doTick();
            }
        }
    }
}
