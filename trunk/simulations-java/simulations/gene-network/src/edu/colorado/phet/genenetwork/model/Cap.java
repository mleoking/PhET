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

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents the CAP, which is an acronym for something, but I
 * don't know what.
 * 
 * @author John Blanco
 */
public class Cap extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(237, 179, 122);
	private static float WIDTH = CapBindingRegion.WIDTH;
	private static float HEIGHT = 4;  // In nanometers.
	
	private CapBindingRegion capBindingRegionPartner = null;
	private boolean bound;
	
	public Cap(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		addAttachmentPoint(new AttachmentPoint(ModelElementType.CAP_BINDING_REGION, new PDimension(0, 0)));
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds()));
	}
	
	public Cap(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public Cap(){
		this(null);
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.CAP;
	}
	
	private static Shape createActiveConformationShape(){
		
		// Create the overall outline.
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(-WIDTH / 2, 0);
		outline.curveTo(-WIDTH/2, HEIGHT, 0, HEIGHT / 2, 0, HEIGHT/4);
		outline.lineTo(WIDTH/2, HEIGHT/4);
		outline.lineTo(WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, -HEIGHT/2);
		outline.closePath();
		Area area = new Area(outline);
		
		// Get the shape of the binding region and shift it to the appropriate
		// position.
		Shape bindingRegionShape = new CapBindingRegion(null).getShape();
		AffineTransform transform = new AffineTransform();
		transform.setToTranslation(	0, -HEIGHT/2 );
		bindingRegionShape = transform.createTransformedShape(bindingRegionShape);
		
		// Subtract off the shape of the binding region.
		area.subtract(new Area(bindingRegionShape));
		
		// Get the shape of the cAMP and shift it to the appropriate location.
		Shape campShape = new Camp(null).getShape();
		transform = new AffineTransform();
		transform.setToTranslation(	-WIDTH/2, 0 );
		campShape = transform.createTransformedShape(campShape);
		
		// Subtract off the shape of the camp.
		area.subtract(new Area(campShape));
		
		return area;
	}
	
	@Override
	public void stepInTime(double dt) {
		if (capBindingRegionPartner != null){
			if (!bound){
				// We are moving towards attaching to a partner.
				// Calculate the destination and make sure we are moving
				// towards it.
				Dimension2D partnerOffset = capBindingRegionPartner.getAttachmentPointForElement(getType()).getOffset();
				Dimension2D myOffset = getAttachmentPointForElement(capBindingRegionPartner.getType()).getOffset();
				double xDest = capBindingRegionPartner.getPositionRef().getX() + partnerOffset.getWidth() - 
					myOffset.getWidth();
				double yDest = capBindingRegionPartner.getPositionRef().getY() + partnerOffset.getHeight() - 
					myOffset.getHeight();
				if (getPositionRef().distance(xDest, yDest) < ATTACHMENT_FORMING_DISTANCE){
					// Close enough to attach.  Move to the location and
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

	public boolean availableForAttaching(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.CAP_BINDING_REGION && capBindingRegionPartner == null){
			available = true;
		}
		return available;
	}

	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof CapBindingRegion && capBindingRegionPartner == null){
			capBindingRegionPartner = (CapBindingRegion)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
	}
}
