/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

/**
 * Base class for the various motion strategies that can be exhibited by a
 * model element.
 * 
 * @author John Blanco
 */
public abstract class AbstractMotionStrategy {
	
	private IModelElement modelElement;
	private Point2D destination = null;
	
	public AbstractMotionStrategy(IModelElement modelElement){
		this.modelElement = modelElement;
	}
	
	abstract public void updatePositionAndMotion(double dt);
	
	protected IModelElement getModelElement(){
		return modelElement;
	}

	public void setDestination(double x, double y) {
		if (destination == null){
			destination = new Point2D.Double();
		}
		destination.setLocation(x, y);
	}

	/**
	 * The the destination towards which the element should be moving.
	 * Setting the value to null indicates no destination, which may not make
	 * sense for all motion strategies.
	 * 
	 * @param destination
	 */
	public void setDestination(Point2D destination){
		if (destination == null){
			this.destination = destination;
		}
		else{
			setDestination(destination.getX(), destination.getY());
		}
	}
	
	public void clearDestination(){
		destination = null;
	}
	
	/**
	 * Clean up any memory references so as not to cause memory leaks.
	 */
	public void cleanup(){
		// Does nothing in base class.
	}
	
	protected Point2D getDestination(){
		return destination;
	}
}
