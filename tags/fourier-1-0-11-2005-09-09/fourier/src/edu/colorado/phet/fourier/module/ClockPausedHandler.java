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

import java.util.Timer;
import java.util.TimerTask;

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
public class ClockPausedHandler implements ClockStateListener {

    private static long DEFAULT_DELAY = 0; // milliseconds
    private static long DEFAULT_PERIOD = 500; // milliseconds
    
    private Module _module;
    private long _period;
    private Timer _timer;
    private boolean _isRunning;
    
    /**
     * Creates a ClockPausedHandler with a default period (500 ms).
     * 
     * @param module
     */
    public ClockPausedHandler( Module module ) {
        this( module, DEFAULT_PERIOD );
    }
    
    /**
     * Creates a ClockPausedHandler with a specific period.
     * 
     * @param module
     * @param period the period, in milliseconds
     */
    public ClockPausedHandler( Module module, long period ) {
        _module = module;
        _period = period;
        _timer = null;
        _isRunning = false;
    }
    
    /**
     * ClockStateListener implementation, called whenever the
     * state of the clock changes.
     */
    public void stateChanged( ClockStateEvent event ) {
        if ( event.getIsPaused() ) {
            startTimer();
        }
        else {
            stopTimer();
        }
    }
    
    /*
     * Starts a timer that periodically refreshes the apparatus panel and PhetJComponents.
     */
    private void startTimer() {
        TimerTask task = new TimerTask() {
            public void run() {
                PhetJComponent.getRepaintManager().updateGraphics();
                _module.getApparatusPanel().paint();
            }
        };
        _timer = new Timer();
        _timer.schedule( task, DEFAULT_DELAY, _period );
        _isRunning = true;
    }
    
    /*
     * Stops the timer.
     */
    private void stopTimer() {
        if ( _timer != null ) {
            _timer.cancel();
            _timer = null;
        }
        _isRunning = false;
    }
}
