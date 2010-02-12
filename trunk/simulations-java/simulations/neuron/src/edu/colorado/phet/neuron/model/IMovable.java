/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

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
	 * Set the motion strategy for the element.
	 */
	void setMotionStrategy(MotionStrategy motionStrategy);
}
