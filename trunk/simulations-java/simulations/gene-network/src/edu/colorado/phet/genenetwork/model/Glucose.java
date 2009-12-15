/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {
	
	private static final Dimension2D GALACTOSE_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_Z_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_I_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	
	private Galactose galactoseAttachmentPartner;
	private LacZ lacZAttachmentPartner;
	private AttachmentState lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private LacI lacIAttachmentPartner;
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
	
	public static Dimension2D getGalactoseAttachmentPointOffset(){
		return new PDimension(GALACTOSE_ATTACHMENT_POINT_OFFSET);
	}
	
	public static Dimension2D getLacZAttachmentPointOffset(){
		return new PDimension(LAC_Z_ATTACHMENT_POINT_OFFSET);
	}
	
	public static Dimension2D getLacIAttachmentPointOffset(){
		return new PDimension(LAC_I_ATTACHMENT_POINT_OFFSET);
	}
	
	public void formLactose(Galactose galactose){
		assert galactoseAttachmentPartner == null; // Should not be requested to attach if already attached.
		
		galactoseAttachmentPartner = galactose;
		galactoseAttachmentPartner.attach(this);  // This will move galactose to the appropriate location.
	}
	
	public boolean isAvailableForAttaching(){
		return (lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE);
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
			Dimension2D offsetFromTarget = new PDimension(
					LacZ.getGlucoseAttachmentPointOffset().getWidth() - getLacZAttachmentPointOffset().getWidth(),
					LacZ.getGlucoseAttachmentPointOffset().getHeight() - getLacZAttachmentPointOffset().getHeight());
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(this, lacZ, offsetFromTarget,
					LacOperonModel.getMotionBounds()));
		}
		
		return proposalAccepted;
	}
	
	public boolean considerProposalFrom(LacI lacI){
		boolean proposalAccepted = false;
		
		if (lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE && 
			getExistenceState() == ExistenceState.EXISTING){
			
			assert lacIAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacIAttachmentPartner = lacI;
			lacIAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			proposalAccepted = true;
			
			// Set ourself up to move toward the attaching location.
			Dimension2D offsetFromTarget = new PDimension(
					LacI.getGlucoseAttachmentPointOffset().getWidth() - getLacIAttachmentPointOffset().getWidth(),
					LacI.getGlucoseAttachmentPointOffset().getHeight() - getLacIAttachmentPointOffset().getHeight());
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(this, lacI, offsetFromTarget,
					LacOperonModel.getMotionBounds()));
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
	
	public void attach(LacI lacI){
		if (lacI != lacIAttachmentPartner){
			// For this bond, it is expected that we were already moving
			// towards this partner.  If not, it's unexpected.
			System.err.println(getClass().getName() + " - Error: Attach request from non-partner.");
			assert false;
			return;
		}
		setPosition(lacI.getGlucoseAttachmentPointLocation().getX() - LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				lacI.getGlucoseAttachmentPointLocation().getY() - LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
		Dimension2D followingOffset = new PDimension(
				LacI.getGlucoseAttachmentPointOffset().getWidth() - LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				LacI.getGlucoseAttachmentPointOffset().getHeight() - LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
		setMotionStrategy(new FollowTheLeaderMotionStrategy(this, lacI, followingOffset));
		lacIAttachmentState = AttachmentState.ATTACHED;
	}
	
	public void detach(LacZ lacZ){
		assert lacZ == lacZAttachmentPartner;
		lacZAttachmentPartner = null;
		lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		setMotionStrategy(new LinearThenRandomMotionStrategy(this, LacOperonModel.getMotionBounds(), 
				new Vector2D.Double(-3, -8), 1));
		
		// Once broken down from being a part of lactose, this fades away.
		setExistenceTime(0.5);
	}
	
	/**
	 * Set the time for lactose to exist, after which it will fade out.  This
	 * was created to allow lactose to fade out at the same time as lacI when
	 * they are all bonded together.
	 * 
	 * TODO: As of this writing (Dec 15, 2009), lactose fades out after being
	 * bonded to LacI.  We don't know if this is the desired behavior, since
	 * it isn't specified, so the behavior may eventually be changed such that
	 * lactose can only be removed after being broken down by LacZ.  If that
	 * becomes the case, this method should go away.
	 * 
	 * @param existenceTime
	 */
	public void setLactoseExistenceTime(double existenceTime){
		
		assert galactoseAttachmentPartner != null;
		galactoseAttachmentPartner.setOkayToFade(true);
		galactoseAttachmentPartner.setExistenceTime(existenceTime);
		setExistenceTime(existenceTime);
		setOkayToFade(true);
		
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
