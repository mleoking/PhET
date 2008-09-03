/**
 * Class: SwingTimerClock
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.model.clock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTimerClock extends AbstractClock {

    Timer timer;
    private long lastTickTime;

    /**
     * This uses a TimeScalingClockModel.
     */
    public SwingTimerClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        timer = new Timer( delay, new Ticker() );
    }

    public void doStart() {
        lastTickTime = System.currentTimeMillis();
        timer.start();
    }

    protected void doUnpause() {
        lastTickTime = System.currentTimeMillis();
        timer.restart();
    }

    private class Ticker implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            if( isRunning() ) {
                long tickTime = System.currentTimeMillis();
                long actualWaitTime = tickTime - lastTickTime;
                lastTickTime = tickTime;
                if( isRunning() ) {
                    clockTicked( getSimulationTime( actualWaitTime ) );
                }
            }
        }
    }
}
