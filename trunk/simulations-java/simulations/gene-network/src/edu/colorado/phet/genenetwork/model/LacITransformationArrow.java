package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

public class LacITransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final LacI lacIToAddToModel;
	private boolean lacIAdded = false;
	
	public LacITransformationArrow(IObtainGeneModelElements model, Point2D initialPosition, LacI lacI) {
		super(model, initialPosition, ARROW_LENGTH);
		lacIToAddToModel = lacI;
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		if (!lacIAdded && getExistenceState() == ExistenceState.FADING_OUT){
			// Time to add our LacZ to the model.
			lacIToAddToModel.setPosition(getPositionRef().getX(), getPositionRef().getY() + 8);
			getModel().addLacI(lacIToAddToModel);
			lacIAdded = true;
		}
	}
}
