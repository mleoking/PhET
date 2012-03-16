// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.neuron.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * A simple motion strategy for moving in a straight line.  This was created
 * primarily for testing and, if it is no longer used, can be removed.
 * 
 * @author John Blanco
 */
public class LinearMotionStrategy extends MotionStrategy {

	private Vector2D velocity; // In nanometers per second of simulation time.
	
	public LinearMotionStrategy( Vector2D velocity) {
		this.velocity = velocity;
	}

	@Override
	public void move(IMovable movableModelElement, IFadable fadableModelElement, double dt) {
		Point2D currentPositionRef = movableModelElement.getPositionReference();
		movableModelElement.setPosition(currentPositionRef.getX() + velocity.getX() * dt,
				currentPositionRef.getY() + velocity.getY() * dt);
	}
}
