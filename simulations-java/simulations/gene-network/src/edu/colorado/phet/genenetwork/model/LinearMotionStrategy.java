/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Motion strategy that moves in a straight line towards a destination point.
 * Once the destination point is reached, the element stops.  If the
 * destination is outside of the allowable bounds, it will stop at or near
 * the boundary crossing.
 * 
 * @author John Blanco
 */
public class LinearMotionStrategy extends AbstractMotionStrategy {
	
	public LinearMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Point2D destination, double velocityScaler) {
		super(modelElement, bounds);
		setDestination(destination.getX(),	destination.getY());
		double angleOfTravel = Math.atan2(getDestination().getY() - modelElement.getPositionRef().getY(), 
    			getDestination().getX() - modelElement.getPositionRef().getX());
		getModelElement().setVelocity(velocityScaler * Math.cos(angleOfTravel), velocityScaler * Math.sin(angleOfTravel));
	}
	
	public LinearMotionStrategy(IModelElement modelElement, Rectangle2D bounds, Vector2D velocityVector, double time){
		this(modelElement, bounds, velocityAndTimeToPoint(modelElement, velocityVector, time), velocityVector.getMagnitude());
	}

	@Override
	public void updatePositionAndMotion(double dt) {
		IModelElement modelElement = getModelElement();
		
		Point2D position = modelElement.getPositionRef();
		Vector2D velocity = modelElement.getVelocityRef();
		double distanceToDestination = getDestination().distance(getModelElement().getPositionRef());
		double distanceToTravelThisTimeStep = velocity.getMagnitude() * dt;
		
		if (distanceToDestination > 0 && distanceToTravelThisTimeStep > distanceToDestination){
			// We have pretty much arrived at the destination, so set the
			// position to be exactly at the destination.
			getModelElement().setPosition(getDestination());
			getModelElement().setVelocity(0, 0);
		}
		else if ((position.getX() > getBounds().getMaxX() && velocity.getX() > 0) ||
			     (position.getX() < getBounds().getMinX() && velocity.getX() < 0) ||
			     (position.getY() > getBounds().getMaxY() && velocity.getY() > 0) ||
		         (position.getY() < getBounds().getMinY() && velocity.getY() < 0)){
			
			// We are at or past the boundary, so stop forward motion.
			modelElement.setVelocity(0, 0);
		}
		
		if (modelElement.getVelocityRef().getMagnitude() > 0){
			modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt, 
					modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
		}
	}
	
	public boolean isDestinationReached(){
		return (getModelElement().getPositionRef().distance(getDestination()) == 0);
	}
	
	static private Point2D velocityAndTimeToPoint(IModelElement modelElement, Vector2D velocity, double time){
		return ( new Point2D.Double( modelElement.getPositionRef().getX() + velocity.getX() * time,
				modelElement.getPositionRef().getY() + velocity.getY() * time ) );
	}
}
