/**
 * Class: SwingTimerClock
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common_cck.model.clock.AbstractClock;
import edu.colorado.phet.common_cck.model.clock.ClockStateListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DynamicSwingWaitClock extends AbstractClock {

    Timer timer;
    private long lastTickTime;

    public DynamicSwingWaitClock( double dt, int delay ) {
        this( dt, delay, true );
    }

    /**
     * This uses a TimeScalingClockModel.
     */
    public DynamicSwingWaitClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        timer = new Timer( delay, new Ticker() );
    }

    public void doStart() {
        lastTickTime = System.currentTimeMillis();
        timer.start();
    }

    public void setDelay( int delay ) {
        super.setDelay( delay );
        timer.setDelay( delay );
    }

    protected void doStop() {
        timer.stop();
    }

    protected void doPause() {
        timer.stop();
        for( int i = 0; i < getClockStateListeners().size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)getClockStateListeners().get( i );
            clockStateListener.pausedStateChanged( true );
        }
    }

    protected void doUnpause() {
        lastTickTime = System.currentTimeMillis();
        timer.restart();
        for( int i = 0; i < getClockStateListeners().size(); i++ ) {
            ClockStateListener clockStateListener = (ClockStateListener)getClockStateListeners().get( i );
            clockStateListener.pausedStateChanged( false );
        }
    }

    private class Ticker implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            if( isRunning() ) {
                long tickTime = System.currentTimeMillis();
                long actualWaitTime = tickTime - lastTickTime;
                lastTickTime = tickTime;
                if( isRunning() ) {
                    long beforeTick = System.currentTimeMillis();
                    clockTicked( getSimulationTime( actualWaitTime ) );
                    long afterTick = System.currentTimeMillis();
                    long elapsed = afterTick - beforeTick;
                    int toSleep = (int)Math.max( DynamicSwingWaitClock.this.getDelay() - elapsed, 5 );
                    timer.setDelay( toSleep );
                }

            }
        }
    }
}
