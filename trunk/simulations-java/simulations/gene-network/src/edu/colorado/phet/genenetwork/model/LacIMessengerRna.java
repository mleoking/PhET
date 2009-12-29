package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 * Messenger RNA that leads to the creation of LacZ.
 * 
 * @author John Blanco
 */
public class LacIMessengerRna extends MessengerRna {
	
	private static final Random RAND = new Random();

	public LacIMessengerRna(IGeneNetworkModelControl model, Point2D initialPosition, double initialLength, boolean fadeIn) {
		super(model, initialPosition, initialLength, generateSpawnCount(), fadeIn);
	}

	public LacIMessengerRna(IGeneNetworkModelControl model, double initialLength) {
		this(model, new Point2D.Double(), initialLength, false);
	}
	
	// This constructor was created to allow this class to be used in the view
	// without a corresponding element that actually exists in the model.
	// This is needed for things like legends.
	public LacIMessengerRna(double initialLength) {
		this(null, new Point2D.Double(), initialLength, false);
	}
	
	@Override
	protected void spawn() {
		// Create and position the process arrow, which will in turn
		// create the macromolecule.
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D processArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX(),
				bounds.getMaxY() + getPositionRef().getY() + 3);
		getModel().addTransformationArrow(new LacITransformationArrow(getModel(), processArrowPos,
				new LacI(getModel(), true), 0));
	}
	
	private static int generateSpawnCount(){
		return 1 + RAND.nextInt(4);
	}
}
