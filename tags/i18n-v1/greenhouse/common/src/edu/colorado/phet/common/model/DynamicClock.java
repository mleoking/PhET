package edu.colorado.phet.common.model;

import edu.colorado.phet.coreadditions.clock.ClockModel;
import edu.colorado.phet.coreadditions.clock.ThreadedClock;


public class DynamicClock extends ThreadedClock {

    public DynamicClock( ClockModel clockModel, ThreadPriority priority ) {
        super( clockModel, priority );
        this.priority = priority;
        t.setPriority( priority.intValue() );
    }

    public void run() {
        setAlive( true );
        setRunning( true );
        setRunningTime( 0 );

        while( isAlive() ) {
            parent.clockTicked();

            long initialSystemTime = System.currentTimeMillis();
            try {
                Thread.sleep( getRequestedWaitTime() );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            long finalSystemTime = System.currentTimeMillis();
            long actualWaitTime = finalSystemTime - initialSystemTime;

            if( isRunning() ) {
                double actualDT = ( ( (double)actualWaitTime ) / getRequestedWaitTime() ) * getRequestedDT();
                tickOnce( actualDT );
            }
        }
    }

}

