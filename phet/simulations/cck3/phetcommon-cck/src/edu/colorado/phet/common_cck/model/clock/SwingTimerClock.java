/**
 * Class: SwingTimerClock
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 6, 2003
 */
package edu.colorado.phet.common_cck.model.clock;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SwingTimerClock extends AbstractClock {

    private Timer timer;
    private long lastTickTime;
    private DelayStrategy delayStrategy;

    public SwingTimerClock( double dt, int delay ) {
        this( dt, delay, true );
    }

    /**
     * This uses a TimeScalingClockModel.
     */
    public SwingTimerClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        timer = new Timer( delay, new Ticker() );
        delayStrategy = new DynamicDelay( delay );
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
        private int min;

        public DynamicDelay( int delay ) {
            this( delay, 5 );
        }

        public DynamicDelay( int delay, int min ) {
            this.delay = delay;
            this.min = min;
        }

        public int getDelay( long actualWaitTime ) {
            int result = Math.max( delay - (int)actualWaitTime, min );
            return result;
        }
    }

    private class Ticker implements ActionListener {

        public void actionPerformed( ActionEvent e ) {
            if( isRunning() ) {
                long tickTime = System.currentTimeMillis();
                long actualWaitTime = tickTime - lastTickTime;
                int delay = delayStrategy.getDelay( actualWaitTime );
                timer.setDelay( delay );
                lastTickTime = tickTime;
                if( isRunning() ) {
                    clockTicked( getSimulationTime( actualWaitTime ) );
                }
            }
        }
    }
}
