package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class LacITransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final LacI lacIToAddToModel;
	
	public LacITransformationArrow(IGeneNetworkModelControl model, Point2D initialPosition, LacI lacI, double pointingAngle) {
		super(model, initialPosition, ARROW_LENGTH, true, pointingAngle);
		lacIToAddToModel = lacI;
	}

	public LacITransformationArrow(IGeneNetworkModelControl model, LacI lacI) {
		this(model, new Point2D.Double(0,0), lacI, 0);
	}

	@Override
	protected void onTransitionToExistingState() {
		// Time to add our LacI to the model.
		double xOffset = Math.cos(getPointingAngle()) * getHeadHeight() + 6;
		double yOffset = Math.sin(getPointingAngle()) * getHeadHeight() + 4;
		lacIToAddToModel.setPosition(getPositionRef().getX() + xOffset, getPositionRef().getY() + yOffset);
		lacIToAddToModel.setMotionStrategy(new LinearMotionStrategy(lacIToAddToModel,
				LacOperonModel.getMotionBoundsAboveDna(), new Vector2D.Double(getVelocityRef()), 5.0));
		getModel().addLacI(lacIToAddToModel);
	}
}
