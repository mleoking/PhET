/**
 * 
 */
package edu.colorado.phet.genenetwork.model;

/**
 * Interface for listening to events from a gene network model.
 * 
 * @author John Blanco
 */
public interface GeneNetworkModelListener {
	
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
     * that tracks whether or not the legend should be visible to the user.
     */
    void legendVisibilityStateChange();
    
    
}