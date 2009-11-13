/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacI, which in real life is a protein that inhibits
 * (hence the 'I' in the name) the expression of genes coding for proteins
 * involved in lactose metabolism in bacteria.
 * 
 * @author John Blanco
 */
public class LacOperator extends SimpleModelElement {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	private static final Paint ELEMENT_PAINT = new Color(200, 200, 200);
	private static final double WIDTH = 7;   // In nanometers.
	private static final double HEIGHT = 3;  // In nanometers.
	private static final Dimension2D LAC_I_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT/2); 
	private static final Dimension2D ATTACHMENT_REGION_SIZE = new PDimension(WIDTH * 0.5, HEIGHT / 2);
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	private LacI lacIAttachmentPartner = null;
	private AttachmentState attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE; 
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------

	public LacOperator(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		// Add binding point for LacI.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_I, LAC_I_ATTACHMENT_POINT_OFFSET));
	}
	
	public LacOperator(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_OPERATOR;
	}
	
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
	
	public boolean isLacIAttached(){
		return (attachmentState == AttachmentState.ATTACHED);
	}
	
	private void attemptToStartAttaching(){
		assert lacIAttachmentPartner == null;
		// Search for a partner to attach to.
		ArrayList<LacI> potentialPartnerList = getModel().getLacIList();
		
		for (LacI lacI : potentialPartnerList){
			if (getPositionRef().distance(lacI.getPositionRef()) < ATTACHMENT_INITIATION_RANGE){
				if (lacI.considerProposalFrom(this)){
					// Attachment formed.
					attachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
					lacIAttachmentPartner = lacI;
					break;
				}
			}
		}
	}
	
	private void checkAttachmentCompleted(){
		assert lacIAttachmentPartner != null;

		// Calculate the current location of our LacI attachment point.
		Point2D lacIAttachmentPtLocation = 
			new Point2D.Double(getPositionRef().getX() + LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
		
		// Check the distance between the attachment points.
		if (lacIAttachmentPtLocation.distance(lacIAttachmentPartner.getAttachmentPointLocation(this)) < ATTACHMENT_FORMING_DISTANCE){
			// Close enough to attach.
			lacIAttachmentPartner.attach(this);
			attachmentState = AttachmentState.ATTACHED;
		}
	}

	private static Shape createShape(){
		
		/*
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(0, (float)HEIGHT/2);
		outline.quadTo((float)WIDTH / 2, (float)HEIGHT / 2, (float)WIDTH/2, -(float)HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)-HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)(HEIGHT * 0.25));
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of a lac inhibitor molecule and shift it to the
		// appropriate position.
		Shape lacInhibitorShape = new LacI(null).getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, HEIGHT/2 );
		lacInhibitorShape = transform.createTransformedShape(lacInhibitorShape);
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lacInhibitorShape));
		return area;
		*/
		
		Rectangle2D baseRect = new Rectangle2D.Double(-WIDTH / 2, -HEIGHT / 2, WIDTH, 
				HEIGHT - ATTACHMENT_REGION_SIZE.getHeight());
		Rectangle2D attachmentRegion = new Rectangle2D.Double(-ATTACHMENT_REGION_SIZE.getWidth() / 2,
				baseRect.getMaxY(), ATTACHMENT_REGION_SIZE.getWidth(), ATTACHMENT_REGION_SIZE.getHeight());
		Area area = new Area(baseRect);
		area.add(new Area(attachmentRegion));
		
		return area;
	}
	
	public static Dimension2D getBindingRegionSize(){
		return ATTACHMENT_REGION_SIZE;
	}
	
	/**
	 * Get the location in absolute space of the attachment point for the
	 * specified type of model element.
	 */
	public Point2D getAttachmentPointLocation(LacI lacI){
		return new Point2D.Double(getPositionRef().getX() + LAC_I_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_I_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public void detach(LacI lacI){
		if (lacI != lacIAttachmentPartner){
			System.err.println(getClass().getName() + " - Warning: Request to disconnect received from non-partner.");
			return;
		}
		
		lacIAttachmentPartner = null;
		attachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}

	public static Dimension2D getAttachementPointOffset(LacI lacI) {
		return LAC_I_ATTACHMENT_POINT_OFFSET;
	}
}
