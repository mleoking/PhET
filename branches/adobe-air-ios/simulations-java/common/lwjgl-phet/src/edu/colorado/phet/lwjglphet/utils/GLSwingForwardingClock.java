// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.utils;

import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * Handles forwarding clock events between the EDT and LWJGL threads.
 */
public class GLSwingForwardingClock implements IClock {
    private final IClock clock;

    private final Map<ClockListener, ClockListener> swingToGLListenerMap = new HashMap<ClockListener, ClockListener>();

    public GLSwingForwardingClock( IClock clock ) {
        this.clock = clock;
    }

    @Override public void start() {
        clock.start();
    }

    @Override public void pause() {
        clock.pause();
    }

    @Override public boolean isPaused() {
        return clock.isPaused();
    }

    @Override public boolean isRunning() {
        return clock.isRunning();
    }

    @Override public void addClockListener( final ClockListener clockListener ) {
        ClockListener swingListener = new ClockListener() {
            @Override public void clockTicked( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        clockListener.clockTicked( clockEvent );
                    }
                } );
            }

            @Override public void clockStarted( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        clockListener.clockStarted( clockEvent );
                    }
                } );
            }

            @Override public void clockPaused( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        clockListener.clockPaused( clockEvent );
                    }
                } );
            }

            @Override public void simulationTimeChanged( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        clockListener.simulationTimeChanged( clockEvent );
                    }
                } );
            }

            @Override public void simulationTimeReset( final ClockEvent clockEvent ) {
                SwingUtilities.invokeLater( new Runnable() {
                    @Override public void run() {
                        clockListener.simulationTimeReset( clockEvent );
                    }
                } );
            }
        };
        swingToGLListenerMap.put( clockListener, swingListener );
        clock.addClockListener( swingListener );
    }

    @Override public void removeClockListener( ClockListener clockListener ) {
        clock.removeClockListener( swingToGLListenerMap.get( clockListener ) );
    }

    @Override public void resetSimulationTime() {
        clock.resetSimulationTime();
    }

    @Override public long getWallTime() {
        return clock.getWallTime();
    }

    @Override public long getWallTimeChange() {
        return clock.getWallTimeChange();
    }

    @Override public double getSimulationTimeChange() {
        return clock.getSimulationTimeChange();
    }

    @Override public double getSimulationTime() {
        return clock.getSimulationTime();
    }

    @Override public void setSimulationTime( double simulationTime ) {
        clock.setSimulationTime( simulationTime );
    }

    @Override public void stepClockWhilePaused() {
        clock.stepClockWhilePaused();
    }

    @Override public void stepClockBackWhilePaused() {
        clock.stepClockBackWhilePaused();
    }

    @Override public boolean containsClockListener( ClockListener clockListener ) {
        return clock.containsClockListener( swingToGLListenerMap.get( clockListener ) );
    }
}
