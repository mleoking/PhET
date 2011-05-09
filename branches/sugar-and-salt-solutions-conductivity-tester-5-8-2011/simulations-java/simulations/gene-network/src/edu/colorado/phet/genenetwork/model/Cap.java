// Copyright 2002-2011, University of Colorado

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
	private static final float WIDTH = CapBindingRegion.WIDTH;
	private static final float HEIGHT = 4;  // In nanometers.
	private static final Dimension2D CAP_BINDING_REGION_ATTACHMENT_OFFSET = new PDimension(0, -HEIGHT * 0.2);
	
	private CapBindingRegion capBindingRegionPartner = null;
	private AttachmentState capBindingRegionAttachmentState = AttachmentState.UNATTACHED_AND_AVAILABLE;
	Point2D targetPositionForAttachingToBindingRegion = new Point2D.Double();
	
	public Cap(IGeneNetworkModelControl model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		if (model != null){
			setMotionStrategy(new DirectedRandomWalkMotionStrategy(model.getInteriorMotionBounds()));
		}
	}
	
	public Cap(IGeneNetworkModelControl model) {
		this(model, new Point2D.Double());
	}
	
	public Cap(){
		this(null);
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
		/*
		 * TODO: This needs to be reworked for the new approach if we
		 * end up keeping CAP binding region in the sim.  As of Dec
		 * 2009, there is some question about this.
		if (capBindingRegionPartner != null){
			if (capBindingRegionAttachmentState == AttachmentState.MOVING_TOWARDS_ATTACHMENT){
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
					getMotionStrategyRefef().setDestination(xDest, yDest);
				}
			}
		}
		 */
		super.stepInTime(dt);
	}

	public boolean considerProposalFrom(CapBindingRegion capBindingRegion) {
		boolean proposalAccepted = false;

		if (capBindingRegionPartner == null){
			capBindingRegionPartner = capBindingRegion;
			proposalAccepted = true;
			capBindingRegionAttachmentState = AttachmentState.MOVING_TOWARDS_ATTACHMENT;
			
			// Set ourself up to move toward the attaching location.
			double xDest = capBindingRegionPartner.getAttachmentPointLocation(this).getX() - 
				CAP_BINDING_REGION_ATTACHMENT_OFFSET.getWidth();
			double yDest = capBindingRegionPartner.getAttachmentPointLocation(this).getY() - 
				CAP_BINDING_REGION_ATTACHMENT_OFFSET.getHeight();
			setMotionStrategy(new DirectedRandomWalkMotionStrategy(getModel().getInteriorMotionBounds()));
			getMotionStrategyRef().setDestination(xDest, yDest);
			targetPositionForAttachingToBindingRegion.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
	public void attach(CapBindingRegion capBindingRegion){
		if (capBindingRegion != capBindingRegionPartner){
			System.err.println(getClass().getName() + " - Error: Attach request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy());
		setPosition(targetPositionForAttachingToBindingRegion);
		capBindingRegionAttachmentState = AttachmentState.ATTACHED;
	}
	
	public static Dimension2D getCapBindingRegionAttachmentOffset() {
		return CAP_BINDING_REGION_ATTACHMENT_OFFSET;
	}
}
