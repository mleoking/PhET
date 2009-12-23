/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Point2D;


/**
 * Class that represents lac promoter, which is the binding region on the DNA
 * strand for the RNA polymerase.
 * 
 * @author John Blanco
 */
public class LacPromoter extends Promoter {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(0, 137, 225);
	private static final int MAX_BUMPS = 10;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private int bumpCount = 0;

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacPromoter(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
	}
	
	public LacPromoter(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof CapBindingRegion && rnaPolymeraseAttachmentPartner == null){
			rnaPolymeraseAttachmentPartner = (RnaPolymerase)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
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

	@Override
	protected void checkAttachmentCompleted() {
		super.checkAttachmentCompleted();
		bumpCount = 0;
	}
	
	@Override
	protected void checkReadyToDetach(double dt){
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
}
