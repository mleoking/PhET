/**
 * Class: SwingTimerClock
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.greenhouse.coreadditions.clock;

import edu.colorado.phet.greenhouse.phetcommon.model.ThreadPriority;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTimerClock extends AbstractClock {

    Timer timer;
    protected ThreadPriority priority;
    protected Thread t;

    public SwingTimerClock( final ClockModel clockModel ) {
        super( clockModel );
        ActionListener ticker = new ActionListener() {
            private long lastTickTime = System.currentTimeMillis();
            public void actionPerformed( ActionEvent e ) {

                if( isAlive() ) {
                    parent.clockTicked();

                    long tickTime = System.currentTimeMillis();
                    long actualWaitTime = tickTime - lastTickTime;
                    lastTickTime = tickTime;
                    if( isRunning() ) {
//                        double actualDT = ( ( (double)actualWaitTime ) / getRequestedWaitTime() ) * getRequestedDT();
                        tickOnce( clockModel.getMillisSinceLastTick() );
                    }
                }
            }
        };
        timer = new Timer( getRequestedWaitTime(), ticker );
    }

    public void start() {
        timer.start();
        setStarted( true );
        setAlive( true );
        setRunning( true );
        setRunningTime( 0 );
    }

    public void run() {

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
