/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that moves in a straight line towards a destination point.
 * If the element being controlled goes outside of the motion bounds, the
 * element "bounces".
 * 
 * @author John Blanco
 */
public class LinearMotionStrategy extends AbstractMotionStrategy {
	
	private Rectangle2D bounds;
	
	public LinearMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Point2D destination, double velocity) {
		super(modelElement);
		this.bounds = bounds;
		setDestination(destination.getX(),	destination.getY());
		double angleOfTravel = Math.atan2(getDestination().getY() - modelElement.getPositionRef().getY(), 
    			getDestination().getX() - modelElement.getPositionRef().getX());
		getModelElement().setVelocity(velocity * Math.cos(angleOfTravel), velocity * Math.sin(angleOfTravel));
	}

	@Override
	public void updatePositionAndMotion(double dt) {
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
		
		modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt, 
				modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
	}
}
