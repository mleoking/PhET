// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Base class for the various motion strategies that can be exhibited by a
 * model element.
 * 
 * @author John Blanco
 */
public abstract class AbstractMotionStrategy {
	
	private Point2D destination = null;
	private Rectangle2D bounds = new Rectangle2D.Double(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
			Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	
	public AbstractMotionStrategy(Rectangle2D bounds){
		if (bounds != null){
			this.bounds.setFrame(bounds);
		}
	}

	public AbstractMotionStrategy(){
		this(null);
	}

	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}
	
	public Rectangle2D getBoundsRef() {
		return bounds;
	}
	
	public void setBounds(Rectangle2D bounds) {
		this.bounds.setFrame(bounds);
	}
	
    // Clients should generally call this method to cause motion, since it
	// checks to see whether the element is user controlled.
	public void doUpdatePositionAndMotion(double dt, SimpleModelElement modelElement){
        if (!modelElement.isUserControlled()){
            updatePositionAndMotion(dt, modelElement);
        }
    }

	public abstract void updatePositionAndMotion(double dt, SimpleModelElement modelElement);

	public void setDestination(double x, double y) {
		if (destination == null){
			destination = new Point2D.Double();
		}
		destination.setLocation(x, y);
	}

	/**
	 * Set the destination towards which the element should be moving.
	 * Setting the value to null indicates no destination, which makes sense
	 * in some cases (such as a random walk), but not in others (such as a
	 * linear strategy).
	 * 
	 * @param destination
	 */
	public void setDestination(Point2D destination){
		if (destination == null){
			this.destination = null;
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
	
	/**
	 * Get the current destination.
	 * 
	 * @return The current destination or null if no destination is set.
	 */
	protected Point2D getDestinationRef(){
		return destination;
	}
}
