/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

/**
 * Base class for the various motion strategies that can be exhibited by a
 * model element.
 * 
 * @author John Blanco
 */
public abstract class AbstractMotionStrategy {
	
	private IModelElement modelElement;
	
	public AbstractMotionStrategy(IModelElement modelElement){
		this.modelElement = modelElement;
	}
	
	abstract public void updatePositionAndMotion();
	
	protected IModelElement getModelElement(){
		return modelElement;
	}
}
