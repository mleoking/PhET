package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

public class LacZTransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final LacZ lacZToAddToModel;
	
	public LacZTransformationArrow(IObtainGeneModelElements model, Point2D initialPosition, LacZ lacZ) {
		super(model, initialPosition, ARROW_LENGTH);
		lacZToAddToModel = lacZ;
	}

	@Override
	protected void onTransitionToFadingOutState() {
		// Time to add our LacZ to the model.
		lacZToAddToModel.setPosition(getPositionRef().getX(), getPositionRef().getY() + 8);
		getModel().addLacZ(lacZToAddToModel);
	}
}
