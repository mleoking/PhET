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
		Point2D transformationArrowPos = new Point2D.Double(bounds.getCenterX() + getPositionRef().getX() + 3,
				bounds.getMaxY() + getPositionRef().getY() + 1);
		LacZTransformationArrow transformationArrow = new LacZTransformationArrow(getModel(), transformationArrowPos,
				new LacZ(getModel(), true), Math.PI/4);
		transformationArrow.setMotionStrategy(new LinearMotionStrategy( LacOperonModel.getMotionBoundsAboveDna(),
				transformationArrowPos, new Vector2D.Double(getVelocityRef()), 5.0));
		getModel().addTransformationArrow(transformationArrow);
	}
	
	private static int generateSpawnCount(){
		return 2 + RAND.nextInt(3);
	}
}
