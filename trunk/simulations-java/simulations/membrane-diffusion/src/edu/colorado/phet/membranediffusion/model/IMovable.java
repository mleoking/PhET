/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.geom.Point2D;

/**
 * Interface implemented by model elements that can be moved.  This was
 * created originally for use with motion strategies, but may have other
 * uses.
 * 
 * @author John Blanco
 */
public interface IMovable {

	/**
	 * Set the position of the model element.
	 *  
	 * @param newPosition
	 */
	void setPosition(Point2D newPosition);
	
	/**
	 * Set the position of the model element.
	 *  
	 * @param xPos
	 * @param yPos
	 */
	void setPosition(double xPos, double yPos);
	
	/**
	 * Get the current position of the model element.
	 *  
	 * @param newPosition
	 */
	Point2D getPosition();
	
	/**
	 * Get a reference to the current position of the model element.  Be
	 * careful with this.  It is provided primarily for optimization purposes,
	 * and the value should not be changed.
	 *  
	 * @param newPosition
	 */
	Point2D getPositionReference();
	
	/**
	 * Set the motion strategy for the element.
	 */
	void setMotionStrategy(MotionStrategy motionStrategy);
	
	/**
	 * Get the radius of the object being moved.  This is generally used when
	 * the object needs to "bounce" (i.e. change direction because some limit
	 * has been reached).  Note that this assumes a circular object or one that
	 * is fairly close to circular.  If this assumption of approximate
	 * roundness proves to be too much of a limitation at some point in the
	 * future, this may need to be generalized to be a bounding rectangle or
	 * some such thing.
	 */
	double getRadius();
}
