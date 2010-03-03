/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.umd.cs.piccolo.util.PDimension;

public class Glucose extends SimpleSugar {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Dimension2D GALACTOSE_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_Z_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final Dimension2D LAC_I_ATTACHMENT_POINT_OFFSET = new PDimension(getWidth()/2, 0);
	private static final double HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT = 2; // In seconds.
	private static final double POST_ATTACHMENT_RECOVERY_TIME = 1; // In seconds.
	
	// If this molecule is moved by the user to within this distance from a
	// LacI, it should try to attach to it.
	private static final double LAC_I_IMMEDIATE_ATTACH_DISTANCE = 8;
	
	// If this molecule is moved by the user to within this distance from a
	// LacZ, it should try to attach to it.
	private static final double LAC_Z_IMMEDIATE_ATTACH_DISTANCE = 8;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private Galactose galactoseAttachmentPartner;
	private LacZ lacZAttachmentPartner;
	private AttachmentState lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
	private LacI lacIAttachmentPartner;
	private AttachmentState lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
	private double holdoffPriorToFirstAttachmentCountdown = HOLDOFF_TIME_UNTIL_FIRST_ATTACHMENT;
	private double postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public Glucose(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, Color.BLUE);
	}
	
	public Glucose(IGeneNetworkModelControl model){
		this(model, new Point2D.Double());
	}
	
	public Glucose(){
		this(null);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
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
		return ( lacZAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE &&
				 lacIAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE );
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
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(lacZ, offsetFromTarget,
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
			setMotionStrategy(new CloseOnMovingTargetMotionStrategy(lacI, offsetFromTarget,
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
	
	/**
	 * Detach from LacZ.  This is intended to be used by a LacZ instance that
	 * wants to detach.  After being released by LacZ, it is assumed that the
	 * lactose has been "digested", so this molecule fades out of existence.
	 * 
	 * @param lacI
	 */
	public void detach(LacZ lacZ){
		assert lacZ == lacZAttachmentPartner;
		lacZAttachmentPartner = null;
		lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		setMotionStrategy(new LinearThenRandomMotionStrategy(LacOperonModel.getMotionBoundsAboveDna(),
				getPositionRef(), new Vector2D.Double(-3, -8), 1));
	}

	/**
	 * Detach from LacI.  This is intended to be used by a LacI instance that
	 * wants to detach.
	 * 
	 * @param lacI
	 */
	public void detach(LacI lacI){
		assert lacI == lacIAttachmentPartner;
		lacIAttachmentPartner = null;
		if (lacIAttachmentState == AttachmentState.ATTACHED){
			// Move up and away, then get random.
			setMotionStrategy(new LinearThenRandomMotionStrategy(LacOperonModel.getMotionBoundsAboveDna(), 
					getPositionRef(), new Vector2D.Double(0, 8), 0.5));
			// Recover for a while before attaching to something else.
			lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
			postAttachmentRecoveryCountdown = POST_ATTACHMENT_RECOVERY_TIME;
		}
		else{
			// Go back to random motion.
			setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
			// Okay to attach to something else right away.
			lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			postAttachmentRecoveryCountdown = 0;
		}
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
		
		// Once broken down from being a part of lactose, this fades away.
		setExistenceTime(0.5);
	}
	
	/**
	 * Get the location in absolute model space of this element's attachment
	 * point for LacZ.
	 */
	public Point2D getLacZAttachmentPointLocation(){
		return (new Point2D.Double(getPositionRef().getX() + LAC_Z_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_Z_ATTACHMENT_POINT_OFFSET.getHeight()));
	}

	@Override
	public void stepInTime(double dt) {
		if (holdoffPriorToFirstAttachmentCountdown > 0){
			holdoffPriorToFirstAttachmentCountdown -= dt;
			if (holdoffPriorToFirstAttachmentCountdown <= 0){
				lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
				lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		else if (lacIAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE  && !isUserControlled()){
			postAttachmentRecoveryCountdown -= dt;
			if (postAttachmentRecoveryCountdown <= 0){
				// Recovery complete - we are ready to attach again.
				lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		super.stepInTime(dt);
	}

	@Override
	public void setDragging(boolean dragging) {
		if (dragging == true){
			// The user has grabbed this node and is moving it.  Is it
			// attached to a LacI or a LacZ?
			if (lacIAttachmentPartner != null){
				// It is attached to a LacI, so it needs to detach.
				assert lacIAttachmentState == AttachmentState.ATTACHED || lacIAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
				lacIAttachmentPartner.detach(this);
				lacIAttachmentPartner = null;
				setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
			}
			else if (lacZAttachmentPartner != null){
				// It is attached to a LacZ, so it needs to detach.
				assert lacZAttachmentState == AttachmentState.ATTACHED || lacZAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT;  // State consistency test.
				lacZAttachmentPartner.detach(this);
				lacZAttachmentPartner = null;
				setMotionStrategy(new RandomWalkMotionStrategy(LacOperonModel.getMotionBoundsAboveDna()));
			}
			// Make it unavailable for other attachments.
			lacZAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
			lacIAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
			
			// If the initial holdoff timer is running, stop it.
			if (holdoffPriorToFirstAttachmentCountdown > 0){
				holdoffPriorToFirstAttachmentCountdown = 0;
			}
		}
		else {
			// This element has just been released by the user.  It should be
			// considered available.
			lacIAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			lacZAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			assert lacIAttachmentPartner == null;
			assert lacZAttachmentPartner == null;
			
			// If this was dropped close to a LacI, it should try to
			// attach to it.
			ArrayList<LacI> lacIList = getModel().getLacIList();
			for (LacI lacI : lacIList){
				if (lacI.getPositionRef().distance(getPositionRef()) < LAC_I_IMMEDIATE_ATTACH_DISTANCE){
					if (lacI.requestImmediateAttach(this)){
						// The request was accepted, so our work here is done.
						// Note that all of the setting of state variables
						// related to this attachment will be done as a result
						// of requesting the immediate attachment, so nothing
						// else needs to be done here.
						break;
					}
				}
			}
			if (lacIAttachmentState != AttachmentState.ATTACHED && 
				lacIAttachmentState != AttachmentState.MOVING_TOWARDS_ATTACHMENT){

				// If we were dropped close to a LacZ, and we weren't already
				// picked up by a lacI, we should attach to the LacZ.
				ArrayList<LacZ> lacZList = getModel().getLacZList();
				for (LacZ lacZ : lacZList){
					if (lacZ.getPositionRef().distance(getPositionRef()) < LAC_Z_IMMEDIATE_ATTACH_DISTANCE){
						if (lacZ.requestImmediateAttach(this)){
							// The request was accepted, so our work here is done.
							// Note that all of the setting of state variables
							// related to this attachment will be done as a result
							// of requesting the immediate attachment, so nothing
							// else needs to be done here.
							break;
						}
					}
				}
			}
		}
			
		super.setDragging(dragging);
	}
}
