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
 * AnimationCycleController
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AnimationCycleController implements ClockTickListener {

    private double _stepsPerCycle;
    private boolean _enabled;
    private double _cyclePoint;
    private EventListenerList _listenerList;
    
    public interface AnimationCycleListener extends EventListener {
        public void animate( AnimationCycleEvent event );
    }
    
    public class AnimationCycleEvent extends EventObject {
        private double _cyclePoint;
        
        public AnimationCycleEvent( Object source, double cyclePoint ) {
            super( source );
            _cyclePoint = cyclePoint;
        }
        
        public double getCyclePoint() {
            return _cyclePoint;
        }
    }
    
    public AnimationCycleController( double stepsPerCycle ) {
        _stepsPerCycle = stepsPerCycle;
        _enabled = true;
        _cyclePoint = 0;
        _listenerList = new EventListenerList();
    }
    
    public void reset() {
        _cyclePoint = 0;
        fireAnimationCycleEvent( _cyclePoint );
    }
    
    public void setEnabled( boolean enabled ) {
        _enabled = enabled;
    }
    
    public void addAnimationCycleListener( AnimationCycleListener listener ) {
        _listenerList.add( AnimationCycleListener.class, listener );
    }
  
    public void removeAnimationCycleListener( AnimationCycleListener listener ) {
        _listenerList.remove( AnimationCycleListener.class, listener );
    }
    
    public void removeAllAnimationCycleListeners() {
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == AnimationCycleListener.class ) {
                _listenerList.remove( AnimationCycleListener.class, (AnimationCycleListener) listeners[ i + 1 ] );
            }
        }
    }
    
    private void fireAnimationCycleEvent( double cyclePoint ) {
        AnimationCycleEvent event = new AnimationCycleEvent( this, cyclePoint );
        Object[] listeners = _listenerList.getListenerList();
        for ( int i = 0; i < listeners.length; i+=2 ) {
            if ( listeners[i] == AnimationCycleListener.class ) {
                ((AnimationCycleListener) listeners[ i + 1 ] ).animate( event );
            }
        }
    }
    
    public void clockTicked( ClockTickEvent event ) {
        if ( _enabled ) {
            _cyclePoint += event.getDt() / _stepsPerCycle;
            _cyclePoint %= 1.0;
            fireAnimationCycleEvent( _cyclePoint );
        }
    }
}
