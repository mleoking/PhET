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
    private static final boolean USE_ANIMATED_CLOCK_CONTROL = true;

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

        // Attach listeners to send messages to the clock.
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

        // Add ability to update view based on clock state changes
        clockListener = new ClockAdapter() {//use inner anonymous instead of outer so we can extend adapter

            public void clockStarted( ClockEvent clockEvent ) {
                update();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                update();
            }
            
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                update();
            }
        };
        clock.addClockListener( clockListener );
        update();

        if ( USE_ANIMATED_CLOCK_CONTROL ) {
            addToLeft( new AnimatedClockJComponent( clock ) );
        }
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

    /*
     * Updates the control panel correspond to the clock state.
     */
    protected void update() {
        setPaused( clock.isPaused() );
        setTimeDisplay( clock.getSimulationTime() );
    }
}
