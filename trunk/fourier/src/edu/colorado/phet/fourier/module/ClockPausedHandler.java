/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.ClockStateEvent;
import edu.colorado.phet.common.model.clock.ClockStateListener;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;


/**
 * ClockPausedHandler periodically refreshes an active Module while the clock is paused.
 * It does nothing if the clock is running or the Module is inactive.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
class ClockPausedHandler implements ClockStateListener, ActionListener {

    private static int DEFAULT_DELAY = 500; // time between refreshes, in milliseconds
    
    private Module _module; // the Module that we're associated with
    private Timer _timer;
    
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
        _module = module;
        _timer = new Timer( delay, this );
    }
    
    /**
     * ClockStateListener implementation.
     * Starts and stops the timer when the state of the clock changes.
     */
    public void stateChanged( ClockStateEvent event ) {
        if ( event.getIsPaused() ) {
            // Start the timer while the clock is paused.
            _timer.start();
        }
        else {
            // Stop the timer while the clock is running.
            _timer.stop();
        }
    }
    
    /**
     * ActionListener implementation.
     * Anything that needs to be refreshed should be done here.
     * The module will be refreshed only while it is active.
     */
    public void actionPerformed( ActionEvent event ) {
        if ( _module.isActive() ) {
            // Repaint all dirty PhetJComponents
            PhetJComponent.getRepaintManager().updateGraphics();
            // Paint the apparatus panel
            _module.getApparatusPanel().paint();
        }
    }
}
