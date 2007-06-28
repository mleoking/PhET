/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.clock;


import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.opticaltweezers.clock.ConstantDtClockDelegation.ConstantDtClockEvent;
import edu.colorado.phet.opticaltweezers.clock.ConstantDtClockDelegation.ConstantDtClockListener;

/**
 * ConstantDtClockInheritance is a clock with a constant dt.
 * This implementation inherits most of its behavior from SwingClock.
 * Changes to delay and dt are handled via a new listener/event pair.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstantDtClockInheritance extends SwingClock {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private EventListenerList listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConstantDtClockInheritance( int delay, double dt ) {
        super( delay, dt );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setDelay( int delay ) {
        if ( delay != getDelay() ) {
            super.setDelay( delay );
            fireDelayChanged( new ConstantDtClockEvent( this ) );
        }
    }
    
    public void setDt( double dt ) {
        if ( dt != getDt() ) {
            setTimingStrategy( new TimingStrategy.Constant( dt ) );
        }
    }
    
    public double getDt() {
        return ((TimingStrategy.Constant) getTimingStrategy()).getSimulationTimeChange( 0, 0 );
    }
    
    //----------------------------------------------------------------------------
    // SwingClock overrides
    //----------------------------------------------------------------------------
    
    /**
     * Sets the timing strategy for the clock.
     * Only constant timing strategies are allowed.
     * It is preferrable to call setDt.
     * 
     * @param timingStrategy
     * @throws IllegalArgumentExeption if timingStrategy is not of type TimingStrategy.Constant
     */
    public void setTimingStrategy( TimingStrategy timingStrategy ) {
        if ( ! ( timingStrategy instanceof TimingStrategy.Constant ) ) {
            throw new IllegalArgumentException( "timingStrategy must be of type TimingStrategy.Constant" );
        }
        super.setTimingStrategy( timingStrategy );
        fireDtChanged( new ConstantDtClockEvent( this ) );
    }
    
    //----------------------------------------------------------------------------
    // ConstantDtClockListener
    //----------------------------------------------------------------------------
    
    public interface ConstantDtClockListener extends EventListener {
        
        public void delayChanged( ConstantDtClockEvent event );
        
        public void dtChanged( ConstantDtClockEvent event );
    }
    
    public static class ConstantDtClockAdapter implements ConstantDtClockListener {
        
        public void delayChanged( ConstantDtClockEvent event ) {};
        
        public void dtChanged( ConstantDtClockEvent event ) {};
    }
    
    public static class ConstantDtClockEvent extends EventObject {

        public ConstantDtClockEvent( ConstantDtClockInheritance source ) {
            super( source );
        }
        
        public ConstantDtClockDelegation getClock() {
            return (ConstantDtClockDelegation) getSource();
        }
    }
    
    public void addConstantDtClockListener( ConstantDtClockListener listener ) {
        listenerList.add( ConstantDtClockListener.class, listener );
    }
    
    public void removeConstantDtClockListener( ConstantDtClockListener listener ) {
        listenerList.remove( ConstantDtClockListener.class, listener );
    }
    
    private void fireDelayChanged( ConstantDtClockEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ConstantDtClockListener.class ) {
                ( (ConstantDtClockListener)listeners[i + 1] ).delayChanged( event );
            }
        }
    }
    
    private void fireDtChanged( ConstantDtClockEvent event ) {
        Object[] listeners = listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ConstantDtClockListener.class ) {
                ( (ConstantDtClockListener)listeners[i + 1] ).dtChanged( event );
            }
        }
    }
}
