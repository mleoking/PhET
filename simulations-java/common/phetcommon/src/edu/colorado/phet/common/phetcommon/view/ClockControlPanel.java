/*Copyrig/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.view;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * ClockControlPanel implements a Swing component for play/pause and step in PhET simulations
 * that is specific for controlling and observing state for an IClock.
 *
 * @author Chris Malley, Sam Reid
 * @version $Revision$
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

        //Attach listeners to send messages to the clock.
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
        } );

        //Add ability to update view based on clock state changes
        clockListener = new ClockAdapter() {//use inner anonymous instead of outer so we can extend adapter

            public void clockStarted( ClockEvent clockEvent ) {
                updateStateFromClock();
            }

            public void clockPaused( ClockEvent clockEvent ) {
                updateStateFromClock();
            }
        };
        clock.addClockListener( clockListener );
        updateStateFromClock();

        if ( USE_ANIMATED_CLOCK_CONTROL ) {
            addControlFarLeft( new AnimatedClockJComponent( clock ) );
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
     * Updates the state of the buttons to correspond to
     * the state of the clock and the control panel.
     */

    protected void updateStateFromClock() {
        setPaused( clock.isPaused() );
    }

}
