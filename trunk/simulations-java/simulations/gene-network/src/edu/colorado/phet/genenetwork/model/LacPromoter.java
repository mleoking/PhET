/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

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
	public Dimension2D RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT/2);
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private RnaPolymerase rnaPolymeraseAttachmentPartner = null;
	private AttachmentState attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE; 

    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------

	public LacPromoter(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		addAttachmentPoint(new AttachmentPoint(ModelElementType.RNA_POLYMERASE, 
				RNA_POLYMERASE_ATTACHMENT_POINT_OFFSET));
	}
	
	public LacPromoter(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public void stepInTime(double dt) {
		switch (attachmentState){
		case UNATTACHED_AND_AVAILABLE:
			attemptToStartAttaching();
			break;
		case MOVING_TOWARDS_ATTACHMENT:
			checkAttachmentCompleted();
			break;
		case ATTACHED:
			// TODO
			break;
		case UNATTACHED_BUT_UNAVALABLE:
			// TODO
			break;
		default:
			// Should never get here, should be debugged if it does.
			assert false;
			break;
		}
		super.stepInTime(dt);
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
					attachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
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
			attachmentState = AttachmentState.ATTACHED;
		}
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_PROMOTER;
	}
	
	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.RNA_POLYMERASE && rnaPolymeraseAttachmentPartner == null){
			available = true;
		}
		return available;
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
	
}
