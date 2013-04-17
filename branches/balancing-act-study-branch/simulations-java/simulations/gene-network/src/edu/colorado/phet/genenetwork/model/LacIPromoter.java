// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;

public class LacIPromoter extends Promoter {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(0, 153, 175);
	private static final double ATTACHMENT_RECOVERY_TIME = 12;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacIPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		setAttachmentRecoveryTime(ATTACHMENT_RECOVERY_TIME);
	}
	
	public LacIPromoter(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public LacIPromoter() {
		this(null, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacIPromoterLocation()) < LOCK_TO_DNA_DISTANCE;
	}

	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacIPromoterLocation();
	}
}
