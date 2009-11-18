package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacIMessengerRna extends MessengerRna {

	public LacIMessengerRna(IObtainGeneModelElements model, Point2D initialPosition, double initialLength) {
		super(model, initialPosition, initialLength);
	}

	public LacIMessengerRna(IObtainGeneModelElements model, double initialLength) {
		super(model, initialLength);
	}
	
	@Override
	protected void spawnTransformationArrow() {
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacITransformationArrow(getModel(), processArrowPos,
				new LacI(getModel())));
	}
}
