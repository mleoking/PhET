package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacIMessengerRna extends MessengerRna {

	private static final double EXISTENCE_TIME = 3; // In seconds.
	
	public LacIMessengerRna(IObtainGeneModelElements model, Point2D initialPosition, double initialLength) {
		super(model, initialPosition, initialLength);
		// Set up to fade in.
		setExistenceState(ExistenceState.FADING_IN);
		setExistenceStrength(0.01);
		setExistenceTime(EXISTENCE_TIME); 
	}

	public LacIMessengerRna(IObtainGeneModelElements model, double initialLength) {
		this(model, new Point2D.Double(), initialLength);
	}
	
	@Override
	protected void spawnTransformationArrow() {
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacITransformationArrow(getModel(), processArrowPos,
				new LacI(getModel())));
	}

	@Override
	public void stepInTime(double dt) {
		// TODO Auto-generated method stub
		super.stepInTime(dt);
	}
	
	
}
