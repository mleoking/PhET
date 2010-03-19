package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class LacZTransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final LacZ lacZToAddToModel;
	
	public LacZTransformationArrow(IGeneNetworkModelControl model, Point2D initialPosition, LacZ lacZ, double pointingAngle) {
		super(model, initialPosition, ARROW_LENGTH, true, pointingAngle);
		lacZToAddToModel = lacZ;
	}

	@Override
	protected void onTransitionToFadingOutState() {
		// Time to add our LacZ to the model.
		double xOffset = Math.cos(getPointingAngle()) * getHeadHeight() + 6;
		double yOffset = Math.sin(getPointingAngle()) * getHeadHeight() + 4;
		lacZToAddToModel.setPosition(getPositionRef().getX() + xOffset, getPositionRef().getY() + yOffset);
		lacZToAddToModel.setMotionStrategy(new LinearMotionStrategy( 
				MotionBoundsTrimmer.trimMotionBounds(getModel().getInteriorMotionBoundsAboveDna(), lacZToAddToModel), 
				lacZToAddToModel.getPositionRef(), 
				new Vector2D.Double(getVelocityRef()),
				5.0));
		getModel().addLacZ(lacZToAddToModel);
	}
}
