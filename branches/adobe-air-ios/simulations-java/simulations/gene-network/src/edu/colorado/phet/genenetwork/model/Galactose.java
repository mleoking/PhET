// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

public class Galactose extends SimpleSugar {
	
	private static final Dimension2D GLUCOSE_ATTACHMENT_OFFSET = new PDimension(-getWidth()/2, 0);

	private Glucose glucoseAttachmentPartner;

	public Galactose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.ORANGE);
	}

	public Galactose(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public Galactose(){
		this(null);
	}
	
	public void attach(Glucose glucose){
		assert glucoseAttachmentPartner == null; // Should not be requested to attach if already attached.
		
		glucoseAttachmentPartner = glucose;
		Dimension2D offset = Glucose.getGalactoseAttachmentPointOffset();
		offset.setSize(offset.getWidth() - GLUCOSE_ATTACHMENT_OFFSET.getWidth(),
				offset.getHeight() - GLUCOSE_ATTACHMENT_OFFSET.getHeight());
		Point2D position = new Point2D.Double(glucose.getPositionRef().getX() + offset.getWidth(),
				glucose.getPositionRef().getY() + offset.getHeight());
		setPosition(position);
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, glucoseAttachmentPartner, offset));
		setDragBounds(glucoseAttachmentPartner.getDragBounds());
	}
	
	public void detach(Glucose glucose){
		assert glucose == glucoseAttachmentPartner;
		
		glucoseAttachmentPartner = null;
		setMotionStrategy(new LinearThenRandomMotionStrategy(getModel().getInteriorMotionBounds(), getPositionRef(),
				new Vector2D(3, -8), 1));
		
		// This should fade out shortly after detaching.
		setExistenceTime(0.5);
	}

	@Override
	public void setDragging(boolean dragging) {
		super.setDragging(dragging);
		if (glucoseAttachmentPartner != null){
			// Our attached partner needs to be dragged or released along with
			// this molecule.
			glucoseAttachmentPartner.setDragging(dragging);
		}
	}

	@Override
	public void setPosition(double xPos, double yPos) {
		super.setPosition(xPos, yPos);
		if (isUserControlled() && glucoseAttachmentPartner != null){
			// If this galactose is user controlled and has a partner, the
			// partner should also be user controlled.
			assert glucoseAttachmentPartner.isUserControlled();
			
			// Move our partner appropriately.
			glucoseAttachmentPartner.setPosition(xPos + GLUCOSE_ATTACHMENT_OFFSET.getWidth() * 2,
					yPos + GLUCOSE_ATTACHMENT_OFFSET.getHeight());
		}
	}
}
