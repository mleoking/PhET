/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {
	
	private static final Dimension2D GALACTOSE_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_Z_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	
	private Galactose galactoseAttachmentPartner;
	private LacZ lacZAttachmentPartner;
	private AttachmentState lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;

	public Glucose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.BLUE);
	}
	
    public Glucose(IGeneNetworkModelControl model, double x,double y) {
        this(model, new Point2D.Double(x,y));
    }

	public Glucose(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public Glucose(){
		this(null);
	}
	
	public Dimension2D getGalactoseAttachmentPointOffset(){
		return new PDimension(GALACTOSE_ATTACHMENT_POINT_OFFSET);
	}
	
	public void attach(Galactose galactose){
		assert galactoseAttachmentPartner == null; // Should not be requested to attach if already attached.
		
		galactoseAttachmentPartner = galactose;
		
		// Glucose is (arbitrarily) assumed to be the dominant partner, and
		// galactose is expected to move to wherever it is rather than the
		// other way round.  So there is no movement here to the partner's
		// location or adjustment of the motion strategy.
	}
	
	public boolean isBoundToGalactose(){
		return !(galactoseAttachmentPartner == null);
	}
}
