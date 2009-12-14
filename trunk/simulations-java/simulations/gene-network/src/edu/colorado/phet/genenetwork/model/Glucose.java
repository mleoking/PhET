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
	private AttachmentState lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;

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
		return lacZAttachmentState;
	}
	
	public boolean isBoundToGalactose(){
		return !(galactoseAttachmentPartner == null);
	}
	
	public boolean considerProposalFrom(LacZ lacZ){
		boolean proposalAccepted = false;
		
		if (lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING){
			
			assert lacZAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacZAttachmentPartner = lacZ;
			lacZAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
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
		setPosition(lacZ.getGlucoseAttachmentPointLocation().getX() - LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				lacZ.getGlucoseAttachmentPointLocation().getY() - LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight());
		Dimension2D followingOffset = new PDimension(
				LacZ.getGlucoseAttachmentPointOffset().getWidth() - LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				LacZ.getGlucoseAttachmentPointOffset().getHeight() - LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight());
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, lacZ, followingOffset));
		lacZAttachmentState = AttachmentState.ATTACHED;
	}
	
	public void detach(LacZ lacZ){
		assert lacZ == lacZAttachmentPartner;
		lacZAttachmentPartner = null;
		lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		setMotionStrategy(new RandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
	}
	
	/**
	 * This is called to force this molecule to release the attached galactose
	 * molecule, essentially breaking down from lactose into the constituent
	 * molecules.
	 */
	public void releaseGalactose(){
		if (galactoseAttachmentPartner == null){
			System.err.println(getClass().getName() + " - Error: Told to detach galactose when not attached.");
			return;
		}
		galactoseAttachmentPartner.detach(this);
		galactoseAttachmentPartner = null;
	}
	
	/**
	 * Get the location in absolute model space of this element's attachment
	 * point for LacZ.
	 */
	public Point2D getLacZAttachmentPointLocation(){
		return (new Point2D.Double(getPositionRef().getX() + LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight()));
	}
}
