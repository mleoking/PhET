/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Base class for motion strategies that can be used to set the type of motion
 * for elements within the model.
 * 
 * @author John Blanco
 */
public abstract class MotionStrategy {
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    protected ArrayList<Listener> listeners = new ArrayList<Listener>();


    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
    
	/**
	 * Move the associated model element according to the specified amount of
	 * time and the nature of the motion strategy.
	 * 
	 * @param moveableModelElement
	 * @param dt
	 */
	public abstract void move(IMovable moveableModelElement, double dt);
	
	/**
	 * Obtain the instantaneous velocity exhibited by an element that is using
	 * this motion strategy.
	 * @return
	 */
	public abstract Vector2D getInstantaneousVelocity();
	
    public void addListener(Listener listener) {
        if (listeners.contains( listener ))
        {
            // Don't bother re-adding.
            System.err.println(getClass().getName() + "- Warning: Attempting to re-add a listener that is already listening.");
            assert false;
            return;
        }
        
        listeners.add( listener );
    }
    
    public void removeListener(Listener listener){
        listeners.remove(listener);
    }
    
    public void notifyStrategyComplete(IMovable movableElement){
        ArrayList<Listener> copyOfListenerList = new ArrayList<Listener>( listeners );
        for (Listener listener : copyOfListenerList){
            listener.strategyComplete(movableElement);
        }
    }

    //------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

	public interface Listener {
	    /**
	     * Notify listeners that the strategy has completed whatever it was
	     * supposed to do.  Not all strategies will send this notification,
	     * since some are basically infinite.
	     */
	    public void strategyComplete(IMovable movableElement);
	}
	
	public static class Adapter implements Listener {
        public void strategyComplete( IMovable movableElement ) {}
	}
}
