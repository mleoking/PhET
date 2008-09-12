/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * ClockControlPanel implements a Swing component for controlling the clock in PhET simulations
 * that is specific for controlling and observing state for an IClock.
 *
 * @author Chris Malley, Sam Reid
 */
public class ClockControlPanel extends TimeControlPanel {

    private IClock clock;
    private ClockAdapter clockListener;

    /**
     * Constructs a ClockControlPanel
     *
     * @param clock must be non-null
     */
    public ClockControlPanel( final IClock clock ) {
        if ( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        this.clock = clock;

        // Update the clock in response to control panel events
        addTimeControlListener( new TimeControlAdapter() {
            public void stepPressed() {
                clock.stepClockWhilePaused();
            }

            public void playPressed() {
                clock.start();
            }

            public void pausePressed() {
                clock.pause();
            }

            public void restartPressed() {
                clock.resetSimulationTime();
            }
        } );

        // Update the control panel when the clock changes
        clockListener = new ClockAdapter() {

            public void clockStarted( ClockEvent clockEvent ) {
                setPaused( clock.isPaused() );
            }

            public void clockPaused( ClockEvent clockEvent ) {
                setPaused( clock.isPaused() );
            }

            public void simulationTimeChanged( ClockEvent clockEvent ) {
                setTimeDisplay( clock.getSimulationTime() );
                advanceAnimatedClockIcon();
            }

            public void simulationTimeReset( ClockEvent clockEvent ) {
                resetAnimatedClockIcon();
            }
        };
        clock.addClockListener( clockListener );

        // initial state
        setPaused( clock.isPaused() );
        setTimeDisplay( clock.getSimulationTime() );
    }

    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        clock.removeClockListener( clockListener );
        //I don't think the TimeControlListener needs to be removed in order to properly clean up this object.
    }

    /**
     * Gets the clock that's being controlled.
     *
     * @return IClock
     */
    protected IClock getClock() {
        return clock;
    }
}
