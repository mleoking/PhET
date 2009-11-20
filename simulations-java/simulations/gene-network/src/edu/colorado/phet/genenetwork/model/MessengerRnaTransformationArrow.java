package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;

/**
 * This class represents a transformation arrow that creates a strand of
 * messenger RNA.
 * 
 * @author John Blanco
 */
public class MessengerRnaTransformationArrow extends TransformationArrow {

	private static final double ARROW_LENGTH = 5;
	private final MessengerRna mRna;
	private boolean lacIAdded = false;
	
	public MessengerRnaTransformationArrow(IObtainGeneModelElements model, Point2D initialPosition, MessengerRna mRna) {
		super(model, initialPosition, ARROW_LENGTH);
		this.mRna = mRna;
	}

	public MessengerRnaTransformationArrow(IObtainGeneModelElements model, MessengerRna mRna) {
		this(model, new Point2D.Double(0,0), mRna);
	}

	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
		if (!lacIAdded && getExistenceState() == ExistenceState.EXISTING){
			// Time to add our messenger RNA to the model.
			mRna.setPosition(getPositionRef().getX() - mRna.getShape().getBounds2D().getWidth() / 2,
					getPositionRef().getY() + 4);
			mRna.setMotionStrategy(new LinearMotionStrategy(mRna, LacOperonModel.getMotionBounds(), 
					new Point2D.Double(mRna.getPositionRef().getX(), mRna.getPositionRef().getY() + 30), 3));
			getModel().addMessengerRna(mRna);
			lacIAdded = true;
		}
	}
}
