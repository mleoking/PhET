/**
 * Class: ThreadedClock
 * Package: edu.colorado.phet.common.model
 * Author: Another Guy
 * Date: Aug 7, 2003
 */
package edu.colorado.phet.coreadditions.clock;

import edu.colorado.phet.common.model.ClockStateListener;
import edu.colorado.phet.common.model.ThreadPriority;

public class ThreadedClock extends AbstractClock {
    protected ThreadPriority priority;
    protected Thread t;
    private ClockModel clockModel;

    public ThreadedClock( ClockModel clockModel, ThreadPriority priority ) {
        super( clockModel );
        this.clockModel = clockModel;
        this.priority = priority;
        t = new Thread( this );
    }

    public void start() {
        if( isStarted() ) {
            throw new RuntimeException( "Already started." );
        }
        else {
            t.start();
            setStarted( true );
        }
    }

    public void run() {
        setAlive( true );
        setRunning( true );
        setRunningTime( 0 );

        while( isAlive() ) {
            parent.clockTicked();

            try {
                Thread.sleep( getRequestedWaitTime() );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            long actualWaitTime = clockModel.getMillisSinceLastTick();

            if( isRunning() ) {
                double actualDT = ( ( (double)actualWaitTime ) / getRequestedWaitTime() ) * getRequestedDT();
                tickOnce( clockModel.getMillisSinceLastTick() );
//                tickOnce( actualDT );
            }
        }
    }

    public void setThreadPriority( ThreadPriority tp ) {
        t.setPriority( tp.intValue() );
        this.priority = tp;
        for( int i = 0; i < getClockStateListeners().size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)getClockStateListeners().get( i );
            clockStateListener.threadPriorityChanged( tp );
        }
    }

    public ThreadPriority getThreadPriority() {
        return priority;
    }

    protected ThreadPriority getPriority() {
        return priority;
    }

    protected Thread getT() {
        return t;
    }
}
