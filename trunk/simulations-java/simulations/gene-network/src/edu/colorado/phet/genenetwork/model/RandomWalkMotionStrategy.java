/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that moves things around in a way that emulates the "random
 * walk" behavior exhibited by particles in a fluid.
 * 
 * @author John Blanco
 */
public class RandomWalkMotionStrategy extends AbstractMotionStrategy {

	private Rectangle2D bounds;
	
	public RandomWalkMotionStrategy(IModelElement modelElement, Rectangle2D bounds) {
		super(modelElement);
		this.bounds = bounds;
	}

	@Override
	public void updatePositionAndMotion() {
		IModelElement modelElement = getModelElement();
		
		Point2D position = modelElement.getPositionRef();
		Vector2D velocity = modelElement.getVelocityRef();
		
		if ((position.getX() > bounds.getMaxX() && velocity.getX() > 0) ||
			(position.getX() < bounds.getMinX() && velocity.getX() < 0))	{
			// Reverse direction in the X direction.
			modelElement.setVelocity(-velocity.getX(), velocity.getY());
		}
		if ((position.getY() > bounds.getMaxY() && velocity.getY() > 0) ||
    		(position.getY() < bounds.getMinY() && velocity.getY() < 0))	{
    		// Reverse direction in the Y direction.
    		modelElement.setVelocity(velocity.getX(), -velocity.getY());
    	}
		
		modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX(), 
				modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() );
	}
}
