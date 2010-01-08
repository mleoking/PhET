package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

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
		// Create and position the transformation arrow, which will in turn
		// create the LacI.
		Rectangle2D bounds = getShape().getBounds2D();
		Point2D transformationArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX() + 3,
				bounds.getMaxY() + getPositionRef().getY() + 1);
		LacITransformationArrow transformationArrow = new LacITransformationArrow(getModel(), transformationArrowPos,
				new LacI(getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy( LacOperonModel.getMotionBoundsAboveDna(),
				transformationArrowPos, new Vector2D.Double(getVelocityRef()), 5.0));
		getModel().addTransformationArrow(transformationArrow);
	}
	
	private static int generateSpawnCount(){
		return 2 + RAND.nextInt(2);
	}
}
