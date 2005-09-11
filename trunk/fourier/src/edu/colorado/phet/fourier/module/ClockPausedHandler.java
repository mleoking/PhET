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
 * ClockPausedHandler periodically refreshes a Module while the clock is paused.
 * While the clock is running, it does nothing.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ClockPausedHandler implements ClockStateListener, ActionListener {

    private static int DEFAULT_DELAY = 250; // milliseconds
    
    private Module _module;
    private Timer _timer;
    
    /**
     * Creates a ClockPausedHandler with a default delay (500 ms).
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
            _timer.start();
        }
        else {
            _timer.stop();
        }
    }
    
    /**
     * ActionListener implementation.
     * Redraws the apparatus panel and PhetJComponent each time the timer goes off.
     */
    public void actionPerformed( ActionEvent event ) {
        PhetJComponent.getRepaintManager().updateGraphics();
        _module.getApparatusPanel().paint();
    }
}
