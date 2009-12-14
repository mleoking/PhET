/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Galactose extends SimpleSugar {
	
	private static final Dimension2D GLUCOSE_ATTACHMENT_OFFSET = new PDimension(-getWidth()/2, 0);
	private static final Dimension2D LAC_Z_ATTACHMENT_OFFSET = new PDimension(-getWidth()/2, 0);

	private Glucose glucoseAttachmentPartner;

	public Galactose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.ORANGE);
	}

    public Galactose(IGeneNetworkModelControl model, double x, double y) {
        this(model, new Point2D.Double(x,y));
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
		Dimension2D offset = glucoseAttachmentPartner.getGalactoseAttachmentPointOffset();
		offset.setSize(offset.getWidth() - GLUCOSE_ATTACHMENT_OFFSET.getWidth(),
				offset.getHeight() - GLUCOSE_ATTACHMENT_OFFSET.getHeight());
		Point2D position = new Point2D.Double(glucose.getPositionRef().getX() + offset.getWidth(),
				glucose.getPositionRef().getY() + offset.getHeight());
		setPosition(position);
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, glucoseAttachmentPartner, offset));
	}
	
	public void detach(Glucose glucose){
		assert glucose == glucoseAttachmentPartner;
		
		glucoseAttachmentPartner = null;
		setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
		
		// As soon as this detaches it starts to fade out.
		setExistenceTime(0);
	}
}
