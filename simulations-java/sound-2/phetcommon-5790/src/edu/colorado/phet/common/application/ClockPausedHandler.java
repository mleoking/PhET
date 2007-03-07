/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.common.application;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;


/**
 * ClockPausedHandler periodically "refreshes" an active Module while 
 * the clock is paused. It does nothing if the clock is running or the
 * Module is inactive.
 * <p>
 * This functionality was introduced to solve two problems:
 * <br>
 * (1) When the clock is paused, changes made in the control panel were
 * not reflected in the apparatus panel until the mouse cursor was moved
 * into the apparatus panel, forcing a redraw.
 * <br>
 * (2) When the clock is paused, dirty PhetJComponents were never 
 * repainted, and the list of dirty compononents would grow excessively
 * (and unnecessarilty) large.
 * <p>
 * Repainting the above things is handled by Module.refresh.
 * If your Module needs to refresh other things, then override
 * Module.refresh.
 * <p>
 * Since "refreshing" the Module involves painting Swing components,
 * we use a javax.swing.Timer to avoid synchronization issues.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class ClockPausedHandler implements ClockStateListener, ActionListener {

    private static int DEFAULT_DELAY = 500; // time between refreshes, in milliseconds
    
    private Module module; // the Module that we're associated with
    private Timer timer;
    
    /**
     * Creates a ClockPausedHandler with a default delay.
     * 
     * @param module
     */
    public ClockPausedHandler( Module module ) {
        this( module, DEFAULT_DELAY );
    }
    
    /**
     * Creates a ClockPausedHandler with a specific delay.
     * 
     * @param module
     * @param delay the delay, in milliseconds
     */
    public ClockPausedHandler( Module module, int delay ) {
        this.module = module;
        timer = new Timer( delay, this );
    }
    
    /**
     * ClockStateListener implementation.
     * Starts and stops the timer when the state of the clock changes.
     */
    public void stateChanged( ClockStateEvent event ) {
        if( event.getIsPaused() ) {
            // Start the timer while the clock is paused.
            timer.start();
        }
        else {
            // Stop the timer while the clock is running.
            timer.stop();
        }
    }
    
    /**
     * ActionListener implementation.
     * Anything that needs to be refreshed should be done here.
     * The module will be refreshed only while it is active.
     */
    public void actionPerformed( ActionEvent event ) {
        if( event.getSource() == timer && module.isActive() ) {
            module.refresh();
        }
    }
}
