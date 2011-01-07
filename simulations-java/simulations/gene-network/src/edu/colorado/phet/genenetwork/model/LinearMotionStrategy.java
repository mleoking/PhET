// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
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
	
	private boolean initialVelocitySet = false;
	private Vector2D initialVelocity = new Vector2D();
	private boolean isDestinationReached = false;
	private boolean isBoundsReached = false;
	
	public LinearMotionStrategy(Rectangle2D bounds, Point2D initialLocation, Point2D destination, double velocityScaler) {
		super(bounds);
		setDestination(destination.getX(),	destination.getY());
		double angleOfTravel = Math.atan2(getDestinationRef().getY() - initialLocation.getY(), 
    			getDestinationRef().getX() - initialLocation.getX());
		initialVelocity.setComponents(velocityScaler * Math.cos(angleOfTravel), velocityScaler * Math.sin(angleOfTravel));
	}
	
	public LinearMotionStrategy(Rectangle2D bounds, Point2D initialLocation, Vector2D velocityVector, double time){
		this(bounds, initialLocation, velocityAndTimeToPoint(initialLocation, velocityVector, time), velocityVector.getMagnitude());
	}

	@Override
	public void updatePositionAndMotion(double dt, SimpleModelElement modelElement) {

		if (!initialVelocitySet){
			modelElement.setVelocity(initialVelocity);
			initialVelocitySet = true;
		}
		
		Point2D position = modelElement.getPositionRef();
		Vector2D velocity = modelElement.getVelocityRef();
		double distanceToDestination = getDestinationRef().distance(modelElement.getPositionRef());
		double distanceToTravelThisTimeStep = velocity.getMagnitude() * dt;
		
		if (distanceToDestination > 0 && distanceToTravelThisTimeStep > distanceToDestination){
			// We have pretty much arrived at the destination, so set the
			// position to be exactly at the destination.
			modelElement.setPosition(getDestinationRef());
			modelElement.setVelocity(0, 0);
			isDestinationReached = true;
		}
		else if ((position.getX() > getBounds().getMaxX() && velocity.getX() > 0) ||
			     (position.getX() < getBounds().getMinX() && velocity.getX() < 0) ||
			     (position.getY() > getBounds().getMaxY() && velocity.getY() > 0) ||
		         (position.getY() < getBounds().getMinY() && velocity.getY() < 0)){
			
			// We are at or past the boundary, so stop forward motion.
			modelElement.setVelocity(0, 0);
			isBoundsReached = true;
		}
		
		if (modelElement.getVelocityRef().getMagnitude() > 0){
			modelElement.setPosition( modelElement.getPositionRef().getX() + modelElement.getVelocityRef().getX() * dt, 
					modelElement.getPositionRef().getY() + modelElement.getVelocityRef().getY() * dt );
		}
	}
	
	public boolean isDestinationReached(){
		return isDestinationReached;
	}
	
	public boolean isBoundsReached(){
		return isBoundsReached;
	}
	
	static private Point2D velocityAndTimeToPoint(Point2D startingLocation, Vector2D velocity, double time){
		return ( new Point2D.Double( startingLocation.getX() + velocity.getX() * time,
				startingLocation.getY() + velocity.getY() * time ) );
	}
}
