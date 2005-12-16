/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.clock;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.clock.SwingTimerClock;


/**
 * QTClock adds some things that are missing from the SwingTimerClock.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class QTClock extends SwingTimerClock {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param dt
     * @param delay
     * @param isFixed
     */
    public QTClock( double dt, int delay, boolean isFixed ) {
        super( dt, delay, isFixed );
        _listenerList = new EventListenerList();
    }

    //----------------------------------------------------------------------------
    // SwingTimerClock overrides
    //----------------------------------------------------------------------------
    
    /**
     * Resets the clock's running time and notifies listeners.
     */
    public void resetRunningTime() {
        super.resetRunningTime();
        fireClockReset( new QTClockChangeEvent( this ) );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /**
     * Adds a QTClockChangeListener.
     * 
     * @param listener
     */
    public void addQTClockChangeListener( QTClockChangeListener listener ) {
        _listenerList.add( QTClockChangeListener.class, listener );
    }
  
    /**
     * Removes a QTClockChangeListener.
     * 
     * @param listener
     */
    public void removeQTClockChangeListener( QTClockChangeListener listener ) {
        _listenerList.remove( QTClockChangeListener.class, listener );
    }
    
    /**
     * Fires an event indicating that the clock's running time has been reset.
     * 
     * @param event
     */
    public void fireClockReset( QTClockChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i += 2 ) {
            if ( listeners[i] == QTClockChangeListener.class ) {
                QTClockChangeListener listener = (QTClockChangeListener) listeners[i + 1];
                listener.clockReset( event );
            }
        }
    } 
}
