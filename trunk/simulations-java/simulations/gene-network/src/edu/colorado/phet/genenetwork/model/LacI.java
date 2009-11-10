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

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents LacI, which in real life is a protein that inhibits
 * (hence the 'I' in the name) the expression of genes coding for proteins
 * involved in lactose metabolism in bacteria.
 * 
 * @author John Blanco
 */
public class LacI extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// Constants that control size and appearance.
	private static final Paint ELEMENT_PAINT = new Color(200, 200, 200);
	private static double WIDTH = 7;   // In nanometers.
	private static double HEIGHT = 4;  // In nanometers.
	
	// Attachment point offset.
	private static PDimension LAC_OPERATOR_ATTACHMENT_POINT_OFFSET = 
		new PDimension(0, -HEIGHT/2 + LacOperator.getBindingRegionSize().getHeight());
	
	// Time definitions for the amount of time to attach and then to be
	// "unavailable".
	private static double ATTACHMENT_TIME = 5000; // In ms.
	private static double UNAVAILABLE_TIME = 5000; // In ms.
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private LacOperator lacOperatorAttachmentPartner = null;
	private Lactose lactoseAttachmentPartner = null;
	private AttachmentState lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private Point2D targetPositionForLacOperatorAttachment = new Point2D.Double();
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
	
	public LacI(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds()));
		// Add binding point for LacOperator.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_OPERATOR, LAC_OPERATOR_ATTACHMENT_POINT_OFFSET));
	}
	
	public LacI(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public LacI(){
		this(null);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(0, (float)HEIGHT/2);
		outline.quadTo((float)WIDTH / 2, (float)HEIGHT / 2, (float)WIDTH/2, -(float)HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)-HEIGHT/2);
		outline.lineTo((float)-WIDTH/2, (float)(HEIGHT * 0.25));
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of a lactose molecule and shift it to the appropriate
		// position.
		Shape lactoseShape = new Lactose().getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, HEIGHT/2 );
		lactoseShape = transform.createTransformedShape(lactoseShape);
		
		// Get the size of the binding region where this protein will bind to
		// the lac operator and create a shape for it.
		Dimension2D bindingRegionSize = LacOperator.getBindingRegionSize();
		Rectangle2D bindingRegionRect = new Rectangle2D.Double(-bindingRegionSize.getWidth() / 2,
				-HEIGHT/2, bindingRegionSize.getWidth(), bindingRegionSize.getHeight());
		
		// Subtract off the shape of the lactose molecule.
		area.subtract(new Area(lactoseShape));
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(bindingRegionRect));
		
		return area;
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_I;
	}

	public boolean considerProposalFrom(LacOperator lacOperator) {
		boolean proposalAccepted = false;
		
		if (lacOperatorAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
			assert lacOperatorAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacOperatorAttachmentPartner = lacOperator;
			proposalAccepted = true;
			
			// Set ourself up to move toward the attaching location.
			lacOperatorAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			Dimension2D partnerOffset = lacOperatorAttachmentPartner.getAttachmentPointForElement(getType()).getOffset();
			Dimension2D myOffset = getAttachmentPointForElement(lacOperatorAttachmentPartner.getType()).getOffset();
			double xDest = lacOperatorAttachmentPartner.getPositionRef().getX() + partnerOffset.getWidth() - 
				myOffset.getWidth();
			double yDest = lacOperatorAttachmentPartner.getPositionRef().getY() + partnerOffset.getHeight() - 
				myOffset.getHeight();
			getMotionStrategyRef().setDestination(xDest, yDest);
			targetPositionForLacOperatorAttachment.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
	public void attach(LacOperator lacOperator){
		if (lacOperator != lacOperatorAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Finalize request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacOperatorAttachment);
		lacOperatorAttachmentState = AttachmentState.ATTACHED;
	}
	
	/**
	 * Get the location in absolute space of the attachment point for this
	 * type of model element.
	 */
	public Point2D getAttachmentPointLocation(LacOperator lacOperator){
		return new Point2D.Double(getPositionRef().getX() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_OPERATOR_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public void detach(LacOperator lacOperator){
		if (lacOperator != lacOperatorAttachmentPartner){
			System.err.println(getClass().getName() + " - Warning: Request to disconnect received from non-partner.");
			return;
		}
		
		lacOperatorAttachmentPartner = null;
		lacOperatorAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	}
}
