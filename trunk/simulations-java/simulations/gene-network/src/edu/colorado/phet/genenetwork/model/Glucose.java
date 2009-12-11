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
	
    public Glucose(IGeneNetworkModelControl model, double x, double y) {
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
	
	public void formLactose(Galactose galactose){
		assert galactoseAttachmentPartner == null; // Should not be requested to attach if already attached.
		
		galactoseAttachmentPartner = galactose;
		galactoseAttachmentPartner.attach(this);  // This will move galactose to the appropriate location.
	}
	
	public AttachmentState getLacZAttachmentState(){
		return lacIAttachmentState;
	}
	
	public boolean isBoundToGalactose(){
		return !(galactoseAttachmentPartner == null);
	}
	
	public boolean considerProposalFrom(LacZ lacZ){
		boolean proposalAccepted = false;
		
		if (lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING){
			
			assert lacZAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacZAttachmentPartner = lacZ;
			lacIAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			proposalAccepted = true;
			
			// Set ourself up to move toward the attaching location.
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(this, lacZ,
					LacZ.getGlucoseAttachmentPointOffset(), LacOperonModel.getMotionBounds()));
		}
		
		return proposalAccepted;
	}
	
	public void attach(LacZ lacZ){
		if (lacZ != lacZAttachmentPartner){
			// For this bond, it is expected that we were already moving
			// towards this partner.  If not, it's unexpected.
			System.err.println(getClass().getName() + " - Error: Attach request from non-partner.");
			assert false;
			return;
		}
		setPosition(lacZ.getGlucoseAttachmentPointLocation());
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, lacZ, LacZ.getGlucoseAttachmentPointOffset()));
		lacIAttachmentState = AttachmentState.ATTACHED;
	}
}
