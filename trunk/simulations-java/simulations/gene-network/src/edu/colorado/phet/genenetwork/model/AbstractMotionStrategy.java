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
	
	public void setDestination(Point2D destination){
		setDestination(destination.getX(), destination.getY());
	}
	
	public void clearDestination(){
		destination = null;
	}
	
	protected Point2D getDestination(){
		return destination;
	}
}
