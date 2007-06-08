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

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;

/**
 * ClockControlPanel implements a Swing component for play/pause and step in PhET simulations
 * that is specific for controlling and observing state for an IClock.
 *
 * @author Chris Malley, Sam Reid
 * @version $Revision$
 */
public class ClockControlPanel extends TimeControlPanel implements ClockListener {

    private IClock clock;

    public ClockControlPanel( final IClock clock ) {
        addTimeControlListener( new TimeControlAdapter(){

            public void stepPressed() {
                clock.stepClockWhilePaused();
            }

            public void playPressed() {
                clock.start();
            }

            public void pausePressed() {
                clock.pause();
            }
        });
        if( clock == null ) {
            throw new RuntimeException( "Cannot have a control panel for a null clock." );
        }
        this.clock = clock;
        clock.addClockListener( this );
        updateButtons();
    }

    /**
     * Gets the clock that's being controlled.
     * 
     * @return IClock
     */
    protected IClock getClock() {
        return clock;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Updaters
    //
    
    /*
     * Updates the state of the buttons to correspond to
     * the state of the clock and the control panel.
     */

    ////////////////////////////////////////////////////////////////////////////
    // Event handlers
    //

    public void clockTicked( ClockEvent clockEvent ) {
    }

    public void clockStarted( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void clockPaused( ClockEvent clockEvent ) {
        updateButtons();
    }

    public void simulationTimeChanged( ClockEvent clockEvent ) {
    }

    public void simulationTimeReset( ClockEvent clockEvent ) {
    }

    protected void updateButtons() {
        super.updateButtons(clock.isPaused(),isEnabled());
    }

        /**
     * Enables or disables the clock control panel.
     * When disabled, all buttons (play/pause/step) are also disabled.
     * When enabled, the buttons are enabled to correspond to the clock state.
     *
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        super.setEnabled( enabled );
        updateButtons();
    }
}
