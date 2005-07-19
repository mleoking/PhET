/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.view;

import java.util.EventListener;
import java.util.EventObject;

import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;


/**
 * AnimationCycleController controls all cyclic animation.
 * <br>
 * The client wires an instance of this object to an AbstractClock
 * using AbstractClock.addClockTickListener.
 * The object then advances the animation on each clock tick it 
 * receives. Registered listeners are notified each time the 
 * animation advances.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AnimationCycleController implements ClockTickListener {

    //----------------------------------------------------------------------------
    // Related interfaces & classes
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all clients that are interested in
     * animation cycle updates. 
     */
    public interface AnimationCycleListener extends EventListener {
        /**
         * Invoked when the animation cycle point has changed.
         * @param event
         */
        public void animate( AnimationCycleEvent event );
    }
    
    /**
     * AnimationCycleEvent contains the information about where 
     * we are in the animation cycle.  The current point in the 
     * animation cycle is called the "cycle point".
     */
    public static class AnimationCycleEvent extends EventObject {
        private double _cyclePoint;
        private double _delta;
        
        public AnimationCycleEvent( Object source, double cyclePoint, double delta ) {
            super( source );
            assert( cyclePoint >= 0 && cyclePoint <= 1 );
            assert( delta >= -1 && delta <= 1 );
            _cyclePoint = cyclePoint;
            _delta = delta;
        }
        
        /**
         * Gets the animation cycle point.
         * This is a value between 0.0 and 1.0 that indicates the 
         * percentage of the animation that has been completed.
         * 
         * @return the animation cycle point
         */
        public double getCyclePoint() {
            return _cyclePoint;
        }
        
        /**
         * Gets the change in cycle point since the last clock tick.
         * 
         * @return the change
         */
        public double getDelta() {
            return _delta;
        }
    }
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private double _ticksPerCycle; // clock ticks per animation cycle
    private boolean _enabled; // false pauses the animation
    private double _cyclePoint; // 0-1, the point in the animation cycle
    private EventListenerList _listenerList;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param ticksPerCycle number of clock ticks per animation cycle
     */
    public AnimationCycleController( double ticksPerCycle ) {
        _ticksPerCycle = ticksPerCycle;
        _enabled = true;
        _cyclePoint = 0;
        _listenerList = new EventListenerList();
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Resets the animation to the beginning of its cycle.
     */
    public void reset() {
        double delta = -( _cyclePoint );
        _cyclePoint = 0;
        fireAnimationCycleEvent( _cyclePoint, delta );
    }
    
    /**
     * Enables or disables the animation cycle controller.
     * Disabling effectively pauses the animation.
     * 
     * @param enabled true or false
     */
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }
    
    //----------------------------------------------------------------------------
    // Events management
    //----------------------------------------------------------------------------
    
    /**
     * Adds a listener.
     * 
     * @param listener
     */
    public void addAnimationCycleListener( AnimationCycleListener listener ) {
        _listenerList.add( AnimationCycleListener.class, listener );
    }
  
    /**
     * Removes a listener.
     * 
     * @param listener
     */
    public void removeAnimationCycleListener( AnimationCycleListener listener ) {
        _listenerList.remove( AnimationCycleListener.class, listener );
    }
    
    /**
     * Removes all listeners.
     */
    public void removeAllAnimationCycleListeners() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == AnimationCycleListener.class ) {
                _listenerList.remove( AnimationCycleListener.class, (AnimationCycleListener) listeners[ i + 1 ] );
            }
        }
    }
    
    /*
     * Fires an AnimationCycleEvent for all registers listeners.
     * 
     * @param cyclePoint
     */
    private void fireAnimationCycleEvent( double cyclePoint, double delta ) {
        AnimationCycleEvent event = new AnimationCycleEvent( this, cyclePoint, delta );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == AnimationCycleListener.class ) {
                ((AnimationCycleListener) listeners[ i + 1 ] ).animate( event );
            }
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockTickListener implementation
    //----------------------------------------------------------------------------
    
    /**
     * Advances the animation each time the clock ticks.
     * 
     * @param event
     */
    public void clockTicked( ClockTickEvent event ) {
        if ( _enabled ) {
            double newCyclePoint = ( _cyclePoint + ( event.getDt() / _ticksPerCycle ) ) % 1.0;
            double delta = newCyclePoint - _cyclePoint;
            _cyclePoint = newCyclePoint;
            fireAnimationCycleEvent( _cyclePoint, delta );
        }
    }
}
