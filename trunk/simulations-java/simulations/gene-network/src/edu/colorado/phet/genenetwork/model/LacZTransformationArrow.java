package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

public class LacZTransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 8;
	private final LacZ lacZToAddToModel;
	private boolean lacZAdded = false;
	
	public LacZTransformationArrow(IObtainGeneModelElements model, Point2D initialPosition, LacZ lacZ) {
		super(model, initialPosition, ARROW_LENGTH);
		lacZToAddToModel = lacZ;
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		if (!lacZAdded && getExistenceState() == ExistenceState.FADING_OUT){
			// Time to add our LacZ to the model.
			lacZToAddToModel.setPosition(getPositionRef().getX(), getPositionRef().getY() + 10);
			getModel().addLacZ(lacZToAddToModel);
			lacZAdded = true;
		}
	}
}
