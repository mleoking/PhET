// Copyright 2002-2011, University of Colorado

/**
 * 
 */
package edu.colorado.phet.genenetwork.model;

/**
 * Interface for listening to events from a gene network model.
 * 
 * @author John Blanco
 */
public interface IGeneNetworkModelListener {
	
	/**
	 * Let the listener know that a new element has been added to the model.
	 * Note that removal notifications come from the individual elements.
	 * 
	 * @param modelElement
	 */
    void modelElementAdded(SimpleModelElement modelElement);
    
    /**
     * Notify the listener that a change has occurred to the state variable
     * that tracks whether or not lactose injection is allowed.
     */
    void lactoseInjectionAllowedStateChange();
    
    /**
     * Notify the listener that a change has occurred to the state variable
     * that tracks whether or not automatic lactose injection is enabled.
     */
    void automaticLactoseInjectionEnabledStateChange();
        
    /**
     * Notify the listener that a change has occurred to the state variable
     * that tracks whether or not the legend should be visible to the user.
     */
    void legendVisibilityStateChange();
    
    /**
     * Notify the listener that a change has occurred to the state variable
     * that tracks whether or not the lactose meter should be visible to the
     * user.
     */
    void lactoseMeterVisibilityStateChange();
    
    /**
     * Notify the listener that a change has occurred to the amount of lactose
     * in the cell. 
     */
    void lactoseLevelChanged();
    
    
}