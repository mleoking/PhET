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
	
	private static float WIDTH = CapBindingRegion.WIDTH + LacPromoter.WIDTH;
	private static float HEIGHT = 8;  // In nanometers.
	private static final Paint ELEMENT_PAINT = new GradientPaint(new Point2D.Double(-WIDTH, 0), 
			new Color(17, 149, 210), new Point2D.Double(WIDTH * 5, 0), Color.WHITE);
	
	private LacPromoter lacPromoterBondingPartner = null;
	private boolean bound;
	
	public RnaPolymerase(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		
		// This binding point should is hand tweaked to make it work.
		addAttachmentPoint(new AttachmentPoint(ModelElementType.LAC_PROMOTER, new PDimension(WIDTH * 0.1, -HEIGHT * 0.3)));
		
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds()));
	}
	
	public RnaPolymerase(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public RnaPolymerase(){
		this(null);
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.RNA_POLYMERASE;
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
		if (lacPromoterBondingPartner != null){
			// TODO: This needs refinement.  It needs to recognize when the
			// bond is fully formed so that no motion is required, and it
			// needs to position itself so the binding points align.  This is
			// and initial rough attempt.
			// Also, this should probably only be done in this case when bonds
			// are formed and released, since the partner is known not to move.
			if (!bound){
				// We are moving towards forming a bond with a partner.
				// Calculate the destination and make sure we are moving
				// towards it.
				Dimension2D partnerOffset = lacPromoterBondingPartner.getAttachmentPointForElement(getType()).getOffset();
				Dimension2D myOffset = getAttachmentPointForElement(lacPromoterBondingPartner.getType()).getOffset();
				double xDest = lacPromoterBondingPartner.getPositionRef().getX() + partnerOffset.getWidth() - 
					myOffset.getWidth();
				double yDest = lacPromoterBondingPartner.getPositionRef().getY() + partnerOffset.getHeight() - 
					myOffset.getHeight();
				if (getPositionRef().distance(xDest, yDest) < ATTACHMENT_FORMING_DISTANCE){
					// Close enough to form a bond.  Move to the location and
					// then stop moving.
					setPosition(xDest, yDest);
					setMotionStrategy(new StillnessMotionStrategy(this));
				}
				else{
					getMotionStrategyRef().setDestination(xDest, yDest);
				}
			}
		}
		super.stepInTime(dt);
	}

	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.LAC_PROMOTER && lacPromoterBondingPartner == null){
			available = true;
		}
		return available;
	}

	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof LacPromoter && lacPromoterBondingPartner == null){
			lacPromoterBondingPartner = (LacPromoter)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
	}
}
