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

	private Vector2D velocity;
	
	public LinearMotionStrategy(IMovable movableModelElement, Vector2D velocity) {
		super(movableModelElement);
		this.velocity = velocity;
	}

	@Override
	public void move(double dt) {
		Point2D currentPosition = getMovableModelElement().getPosition();
		getMovableModelElement().setPosition(currentPosition.getX() + velocity.getX() * dt,
				currentPosition.getY() + velocity.getY() * dt);
	}
}
