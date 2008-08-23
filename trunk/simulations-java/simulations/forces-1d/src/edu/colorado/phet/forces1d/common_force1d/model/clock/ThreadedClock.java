/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.common_force1d.model.clock;


/**
 * ThreadedClock
 *
 * @author ?
 * @version $Revision$
 */
public class ThreadedClock extends AbstractClock implements Runnable {
    protected int priority;
    protected Thread t;
    private static final int PAUSE_WAIT = Integer.MAX_VALUE;
    private boolean selfInterrupt;

    public ThreadedClock( double dt, int delay, boolean isFixed ) {
        this( dt, delay, isFixed, Thread.NORM_PRIORITY );
    }

    public ThreadedClock( double dt, int waitTime, boolean isFixed, int priority ) {
        super( dt, waitTime, isFixed );
        this.priority = priority;
        t = new Thread( this );
    }

    public void doStart() {
        t.start();
    }

    //Handled in the run() loop.
    protected void doStop() {
    }

    protected void doPause() {
    }

    protected void doUnpause() {
        this.selfInterrupt = true;
        t.interrupt();
    }

    public void run() {
        //exits cleanly on deadthread
        while ( !super.isDead() ) {
            try {
                if ( isPaused() ) {
                    Thread.sleep( PAUSE_WAIT );
                }
                else {
                    long beforeSleep = System.currentTimeMillis();
                    Thread.sleep( super.getDelay() );
                    long afterSleep = System.currentTimeMillis();
                    clockTicked( getSimulationTime( afterSleep - beforeSleep ) );
                }
            }
            catch( InterruptedException e ) {
                if ( selfInterrupt ) {
                    selfInterrupt = false;
                }
                else {
                    throw new RuntimeException( e );
                }
            }
        }
    }

    public void setThreadPriority( int tp ) {
        t.setPriority( tp );
        this.priority = tp;
//        fireClockStateEvent();
    }

    public int getThreadPriority() {
        return priority;
    }

}
