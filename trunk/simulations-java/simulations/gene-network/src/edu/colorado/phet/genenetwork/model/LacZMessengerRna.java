package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacZMessengerRna extends MessengerRna {
	
	public static final double EXISTENCE_TIME = 8; // In seconds.

	public LacZMessengerRna(IGeneNetworkModelControl model,double initialLength) {
		super(model, initialLength, false, EXISTENCE_TIME);
	}

	@Override
	protected void onTransitionToFadingOutState() {
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacZTransformationArrow(getModel(), processArrowPos,
				new LacZ(getModel())));
		setMotionStrategy(new StillnessMotionStrategy(this));
	}
}
