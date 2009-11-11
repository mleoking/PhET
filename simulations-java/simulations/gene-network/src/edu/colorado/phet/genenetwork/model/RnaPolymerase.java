/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents the CAP, which is an acronym for something, but I
 * don't know what.
 * 
 * @author John Blanco
 */
public class RnaPolymerase extends SimpleModelElement {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static float WIDTH = CapBindingRegion.WIDTH + LacPromoter.WIDTH;
	private static float HEIGHT = 8;  // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-WIDTH, 0), 
			new Color(17, 149, 210), new Point2D.Double(WIDTH * 5, 0), Color.WHITE);
	private static Dimension2D LAC_PROMOTER_ATTACHMENT_POINT_OFFSET = new PDimension(WIDTH * 0.1, -HEIGHT * 0.3);
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
	
	private LacPromoter lacPromoterAttachmentPartner = null;
	private AttachmentState lacPromoterAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	private boolean bound;
	private Point2D targetPositionForLacPromoterAttachment = new Point2D.Double();
	
    //------------------------------------------------------------------------
    // Constructor(s)
    //------------------------------------------------------------------------
	
	public RnaPolymerase(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		
		// This binding point should is hand tweaked to make it work.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_PROMOTER, LAC_PROMOTER_ATTACHMENT_POINT_OFFSET));
		
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getMotionBounds()));
	}
	
	public RnaPolymerase(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public RnaPolymerase(){
		this(null);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.RNA_POLYMERASE;
	}
	
	public Point2D getAttachmentPointLocation(LacPromoter lacPromoter){
		return new Point2D.Double(getPositionRef().getX() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	public void attach(LacPromoter lacPromoter){
		if (lacPromoter != lacPromoterAttachmentPartner){
			System.err.println(getClass().getName() + " - Error: Attachment request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacPromoterAttachment);
		lacPromoterAttachmentState = AttachmentState.ATTACHED;
	}
	
	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath basicShape = new GeneralPath();
		
		basicShape.moveTo(WIDTH / 2, -HEIGHT/2);
		basicShape.lineTo(0, -HEIGHT/2);
		basicShape.lineTo(-WIDTH * 0.4f, 0);
		basicShape.curveTo(0, HEIGHT * 0.6f, WIDTH/4, HEIGHT * 0.4f, WIDTH / 2, HEIGHT/4);
		basicShape.closePath();
		Area area = new Area(basicShape);
		
		// Get the shape of the promoter and shift it to the appropriate
		// position.
		Shape promoterShape = new LacPromoter(null).getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	basicShape.getBounds2D().getMaxX() - promoterShape.getBounds().getMaxX(), -HEIGHT/2 );
		promoterShape = transform.createTransformedShape(promoterShape);
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(promoterShape));
		
		// Get the shape of the CAP and shift it to the appropriate location.
		Shape capShape = new Cap(null).getShape();
		transform = new AffineTransform();
		transform.setToTranslation(	promoterShape.getBounds2D().getMinX() - capShape.getBounds2D().getWidth()/2, -2 );
		capShape = transform.createTransformedShape(capShape);
		
		// Subtract off the shape of the camp.
		area.subtract(new Area(capShape));
		
		return area;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
	}

	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.LAC_PROMOTER && lacPromoterAttachmentPartner == null){
			available = true;
		}
		return available;
	}

	public boolean considerProposalFrom(LacPromoter lacPromoter) {
		boolean proposalAccepted = false;
		
		if (lacPromoterAttachmentState == AttachmentState.UNATTACHED_AND_AVAILABLE){
			assert lacPromoterAttachmentPartner == null;  // For debug - Make sure consistent with attachment state.
			lacPromoterAttachmentPartner = lacPromoter;
			proposalAccepted = true;
			lacPromoterAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			
			// Set ourself up to move toward the attaching location.
			double xDest = lacPromoterAttachmentPartner.getAttachmentPointLocation(this).getX() - 
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getWidth();
			double yDest = lacPromoterAttachmentPartner.getAttachmentPointLocation(this).getY() -
				LAC_PROMOTER_ATTACHMENT_POINT_OFFSET.getHeight(); 
			getMotionStrategyRef().setDestination(xDest, yDest);
			targetPositionForLacPromoterAttachment.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
}
