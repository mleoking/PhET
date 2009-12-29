package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacZMessengerRna extends MessengerRna {

	private static final Random RAND = new Random();
	
	public LacZMessengerRna(IGeneNetworkModelControl model,double initialLength) {
		super(model, initialLength, generateSpawnCount(), false);
	}

	@Override
	protected void spawn() {
		// Create and position the process arrow, which will in turn
		// create the macromolecule.
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacZTransformationArrow(getModel(), processArrowPos,
				new LacZ(getModel(), true), 0));
	}
	
	private static int generateSpawnCount(){
		return 2 + RAND.nextInt(4);
	}
}
