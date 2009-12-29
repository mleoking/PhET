package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

public class LacITransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final LacI lacIToAddToModel;
	
	public LacITransformationArrow(IGeneNetworkModelControl model, Point2D initialPosition, LacI lacI) {
		super(model, initialPosition, ARROW_LENGTH, true);
		lacIToAddToModel = lacI;
	}

	public LacITransformationArrow(IGeneNetworkModelControl model, LacI lacI) {
		this(model, new Point2D.Double(0,0), lacI);
	}

	@Override
	protected void onTransitionToExistingState() {
		// Time to add our LacI to the model.
		lacIToAddToModel.setPosition(getPositionRef().getX(), getPositionRef().getY() + 5);
		getModel().addLacI(lacIToAddToModel);
	}
}
