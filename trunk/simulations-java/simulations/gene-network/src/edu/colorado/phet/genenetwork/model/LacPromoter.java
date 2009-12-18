/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents lac promoter, which is the binding region on the DNA
 * strand for the RNA polymerase.
 * 
 * @author John Blanco
 */
public class LacPromoter extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(0, 137, 225);
	private static float HEIGHT = 2.5f;
	public static float WIDTH = 10;
	public static final  Dimension2D RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT/2);
	public static final  double ATTACH_TO_RNA_POLYMERASE_TIME = 0.5;   // In seconds.
	public static final  double ATTACHMENT_RECOVERY_TIME = 3; // In seconds.
	public static final Random RAND = new Random(); 
	private static final int MAX_BUMPS = 10;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private RnaPolymerase rnaPolymeraseAttachmentPartner = null;
	private AttachmentState rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private double rnaPolymeraseAttachmentCountdownTimer;
	private double recoveryCountdownTimer;
	private int bumpCount = 0;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
	}
	
	public LacPromoter(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
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
	
	@Override
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

	private void attemptToStartAttaching(){
		assert rnaPolymeraseAttachmentPartner == null;
		// Search for a partner to attach to.
		ArrayList<RnaPolymerase> potentialPartnerList = getModel().getRnaPolymeraseList();
		
		for (RnaPolymerase rnaPolymerase : potentialPartnerList){
			if (getPositionRef().distance(rnaPolymerase.getPositionRef()) < ATTACHMENT_INITIATION_RANGE){
				if (rnaPolymerase.considerProposalFrom(this)){
					// Attachment formed.
					rnaPolymeraseAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
					rnaPolymeraseAttachmentPartner = rnaPolymerase;
					break;
				}
			}
		}
	}
	
	private void checkAttachmentCompleted(){
		assert rnaPolymeraseAttachmentPartner != null;

		// Calculate the current location of our RnaPolymerase attachment point.
		Point2D rnaPolymeraseAttachmentPtLocation = 
			new Point2D.Double(getPositionRef().getX() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET.getHeight());
		
		// Check the distance between the attachment points.
		if (rnaPolymeraseAttachmentPtLocation.distance(
				rnaPolymeraseAttachmentPartner.getAttachmentPointLocation(this)) < ATTACHMENT_FORMING_DISTANCE){
			// Close enough to attach.
			rnaPolymeraseAttachmentPartner.attach(this);
			rnaPolymeraseAttachmentState = AttachmentState.ATTACHED;
			rnaPolymeraseAttachmentCountdownTimer = ATTACH_TO_RNA_POLYMERASE_TIME;
			bumpCount = 0;
		}
	}
	
	private void checkReadyToDetach(double dt){
		assert rnaPolymeraseAttachmentPartner != null;
		
		rnaPolymeraseAttachmentCountdownTimer -= dt;
		
		if (rnaPolymeraseAttachmentCountdownTimer <= 0){
			// It's time to detach.  Is the way clear to traverse the DNA?
			if (!getModel().isLacIAttachedToDna()){
				// It is possible to traverse the DNA, so just detach the
				// polymerase so that it can do this.
				rnaPolymeraseAttachmentPartner.detach(this);
				rnaPolymeraseAttachmentPartner = null;
				recoveryCountdownTimer = ATTACHMENT_RECOVERY_TIME;
				rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
			}
			else{
				// The way is blocked.  Based on probability, either simulate
				// a bumping of the LacI, or just detach and float away.
				if (RAND.nextDouble() < (double)(MAX_BUMPS - bumpCount) / (double)MAX_BUMPS){
					// Make the polymerase bump the lacI.
					rnaPolymeraseAttachmentPartner.doLacIBump();
					// Reset the timer.
					rnaPolymeraseAttachmentCountdownTimer = ATTACH_TO_RNA_POLYMERASE_TIME + 1; // Need extra time for the bump.
					bumpCount++;
				}
				else{
					// Just detach the polymerase.
					rnaPolymeraseAttachmentPartner.detach(this);
					rnaPolymeraseAttachmentPartner = null;
					recoveryCountdownTimer = ATTACHMENT_RECOVERY_TIME;
					rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_BUT_UNAVALABLE;
				}
			}
		}
	}
	
	private void checkWhetherRecovered(double dt){
		recoveryCountdownTimer -= dt;
		if (recoveryCountdownTimer < 0){
			rnaPolymeraseAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
		}
	}
	
	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof CapBindingRegion && rnaPolymeraseAttachmentPartner == null){
			rnaPolymeraseAttachmentPartner = (RnaPolymerase)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
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

	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
	
	@Override
	protected boolean isInAllowableLocation() {
		// Find out if we are within range of our location on the DNA strand.
		return getPositionRef().distance(getModel().getDnaStrand().getLacPromoterLocation()) < LOCK_TO_DNA_DISTANCE;
	}

	@Override
	protected Point2D getDefaultLocation() {
		return getModel().getDnaStrand().getLacPromoterLocation();
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
}
