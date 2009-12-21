package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacIMessengerRna extends MessengerRna {

	private static final double EXISTENCE_TIME = 2.0; // In seconds.
	
	public LacIMessengerRna(IGeneNetworkModelControl model, Point2D initialPosition, double initialLength, boolean fadeIn) {
		super(model, initialPosition, initialLength, fadeIn, EXISTENCE_TIME);
	}

	public LacIMessengerRna(IGeneNetworkModelControl model, double initialLength) {
		this(model, new Point2D.Double(), initialLength, true);
	}
	
	// This constructor was created to allow this class to be used in the view
	// without a corresponding element that actually exists in the model.
	// This is needed for things like legends.
	public LacIMessengerRna(double initialLength) {
		this(null, new Point2D.Double(), initialLength, false);
	}
	
	@Override
	protected void onTransitionToFadingOutState() {
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacITransformationArrow(getModel(), processArrowPos,
				new LacI(getModel())));
		setMotionStrategy(new StillnessMotionStrategy(this));
	}
}
