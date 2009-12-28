package edu.colorado.phet.genenetwork.model;


import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * 
 * @author John
 *
 */
public abstract class Promoter extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static float HEIGHT = 2.5f;  // In nanometers.
	public static float WIDTH = 10;      // In nanometers.
	public static final  Dimension2D RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT/2);
	public static final  double ATTACH_TO_RNA_POLYMERASE_TIME = 0.5;   // In seconds.
	public static final  double ATTACHMENT_RECOVERY_TIME = 3; // In seconds.
	public static final Random RAND = new Random(); 
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	protected RnaPolymerase rnaPolymeraseAttachmentPartner = null;
	protected AttachmentState rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	protected double rnaPolymeraseAttachmentCountdownTimer;
	protected double recoveryCountdownTimer;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------


	public Promoter(IGeneNetworkModelControl model, Point2D initialPosition, Paint paint,
			boolean fadeIn, double existenceTime) {
		
		super(model, createShape(), new Point2D.Double(), paint, false, Double.POSITIVE_INFINITY);
	}

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public void stepInTime(double dt) {
		
		if (!isUserControlled()){
			switch (rnaPolymeraseAttachmentState){
			case UNATTACHED_AND_AVAILABLE:
				attemptToStartAttaching();
				break;
			case MOVING_TOWARDS_ATTACHMENT:
				checkAttachmentCompleted();
				break;
			case ATTACHED:
				checkReadyToDetach(dt);
				break;
			case UNATTACHED_BUT_UNAVALABLE:
				checkWhetherRecovered(dt);
				break;
			default:
				// Should never get here, should be debugged if it does.
				assert false;
				break;
			}
			super.stepInTime(dt);
		}
	}
	
	/**
	 * This method is used when a molecule of RNA polymerase was recently
	 * attached, then detached, and wants to attach again after a short
	 * period.  This was created for the case where the RNA polymerase
	 * is blocked from traversing the DNA strand and so does some sort of
	 * "bumping" against the blocking agent.
	 * 
	 * This should NOT be used by a polymerase molecule that wants to attach
	 * but was not recently attached - use the "attach" method for that.
	 * 
	 * @param rnaPolymerase
	 * @return
	 */
	public boolean requestReattach(RnaPolymerase rnaPolymerase){

		boolean reattachRequestAccepted = false;
		
		if ( rnaPolymeraseAttachmentPartner == null &&
			 rnaPolymeraseAttachmentState == AttachmentState.UNATTACHED_BUT_UNAVALABLE){
			// We are in the correct state to accept the reattachment.  Note
			// that we trust the molecule to have been recently attached and
			// we don't verify it.
			if (rnaPolymerase.considerProposalFrom(this)){
				rnaPolymeraseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
				rnaPolymeraseAttachmentPartner = rnaPolymerase;
				reattachRequestAccepted = true;
			}
			else{
				// This should never happen, because the RNA polymerase
				// requested reattachment.
				assert false;
			}
		}
		
		return reattachRequestAccepted;
	}

	public void setDragging(boolean dragging) {
		if (dragging == true){
			if (rnaPolymeraseAttachmentPartner != null){
				// Reaching this point in the code indicates that the user is
				// dragging this node, but an RNA polymerase is either
				// attached or about to be attached.  This RNA polymerase
				// should be released.
				rnaPolymeraseAttachmentPartner.detach(this);
				rnaPolymeraseAttachmentPartner = null;
				
				// Set our state to unattached and available.  This is safe
				// because we won't be stepped again until we are released.
				rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
			}
		}
		super.setDragging(dragging);
	}

	/**
	 * Get the location in absolute space of the attachment point for the
	 * specified type of model element.
	 */
	public Point2D getAttachmentPointLocation(RnaPolymerase rnaPolymerase){
		return new Point2D.Double(getPositionRef().getX() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	/**
	 * Release any relationship that we have with a specific RNA polymerase
	 * molecule.  This was created to allow RNA polymerase to terminate the
	 * relationship when it is grabbed by the user.
	 * 
	 * @param rnaPolymerase
	 */
	public void detach(RnaPolymerase rnaPolymerase) {
		assert rnaPolymerase == rnaPolymeraseAttachmentPartner;
		rnaPolymeraseAttachmentPartner = null;
		rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		recoveryCountdownTimer = ATTACHMENT_RECOVERY_TIME;
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
	
	private void attemptToStartAttaching(){
		assert rnaPolymeraseAttachmentPartner == null;
		// Search for a partner to attach to.
		ArrayList<RnaPolymerase> potentialPartnerList = getModel().getRnaPolymeraseList();
		
		for (RnaPolymerase rnaPolymerase : potentialPartnerList){
			if (getPositionRef().distance(rnaPolymerase.getPositionRef()) < ATTACHMENT_INITIATION_RANGE){
				if (rnaPolymerase.considerProposalFrom(this)){
					// Proposal accepted.
					rnaPolymeraseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
					rnaPolymeraseAttachmentPartner = rnaPolymerase;
					break;
				}
			}
		}
	}
	
	protected void checkAttachmentCompleted(){
		assert rnaPolymeraseAttachmentPartner != null;

		// Calculate the current location of our RnaPolymerase attachment point.
		Point2D rnaPolymeraseAttachmentPtLocation = 
			new Point2D.Double(getPositionRef().getX() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getHeight());
		
		// Check the distance between the attachment points.
		if (rnaPolymeraseAttachmentPtLocation.distance(
				rnaPolymeraseAttachmentPartner.getLacPromoterAttachmentPointLocation()) < ATTACHMENT_FORMING_DISTANCE){
			// Close enough to attach.
			rnaPolymeraseAttachmentPartner.attach(this);
			rnaPolymeraseAttachmentState = AttachmentState.ATTACHED;
			rnaPolymeraseAttachmentCountdownTimer = ATTACH_TO_RNA_POLYMERASE_TIME;
		}
	}
	
	protected void checkReadyToDetach(double dt){
		assert rnaPolymeraseAttachmentPartner != null;
		
		rnaPolymeraseAttachmentCountdownTimer -= dt;
		
		if (rnaPolymeraseAttachmentCountdownTimer <= 0){
			// It's time to detach.
			rnaPolymeraseAttachmentPartner.detach(this);
			rnaPolymeraseAttachmentPartner = null;
			recoveryCountdownTimer = ATTACHMENT_RECOVERY_TIME;
			rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
		}
	}
	
	private void checkWhetherRecovered(double dt){
		recoveryCountdownTimer -= dt;
		if (recoveryCountdownTimer < 0){
			rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
		}
	}
	
	private static Shape createShape(){
		
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(WIDTH/2, HEIGHT/2);
		outline.lineTo(WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, HEIGHT/2);
		outline.lineTo(-WIDTH/4, 0);
		outline.lineTo(0, HEIGHT/2);
		outline.lineTo(WIDTH/4, 0);
		outline.closePath();
		
		return outline;
	}
}
