/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Dimension2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents CAP binding region on the DNA strand.
 * 
 * @author John Blanco
 */
public class CapBindingRegion extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(247, 143, 36);
	private static final float HEIGHT = 2.5f;
	public static final float WIDTH = 5;
	private static final Dimension2D CAP_ATTACHMENT_POINT_OFFSET = new PDimension(0, HEIGHT / 2);
	
	private Cap capAttachmentPartner = null;
	
	public CapBindingRegion(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT, false, Double.POSITIVE_INFINITY);
		addAttachmentPoint(new AttachmentPoint(ModelElementType.CAP, CAP_ATTACHMENT_POINT_OFFSET));
	}
	
	public CapBindingRegion(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.CAP_BINDING_REGION;
	}
	
	private static Shape createShape(){
		
		GeneralPath outline = new GeneralPath();
		
		outline.moveTo(WIDTH/2, HEIGHT/2);
		outline.lineTo(WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, -HEIGHT/2);
		outline.lineTo(-WIDTH/2, HEIGHT/2);
		outline.lineTo(-WIDTH * 0.3f, 0);
		outline.lineTo(0, HEIGHT/2);
		outline.lineTo(WIDTH * 0.3f, 0);
		outline.closePath();
		
		return outline;
	}
	
	@Override
	public void stepInTime(double dt) {
		super.stepInTime(dt);
	}

	public boolean availableForAttaching(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.CAP && capAttachmentPartner == null){
			available = true;
		}
		return available;
	}

	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof Cap && capAttachmentPartner == null){
			capAttachmentPartner = (Cap)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
	}
	
	public Point2D getAttachmentPointLocation(Cap cap){
		return new Point2D.Double(getPositionRef().getX() + CAP_ATTACHMENT_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + CAP_ATTACHMENT_POINT_OFFSET.getHeight());
	}
	
	@Override
	public boolean isPartOfDnaStrand() {
		return true;
	}
	
	public static Dimension2D getCapAttachmentPointOffset() {
		return CAP_ATTACHMENT_POINT_OFFSET;
	}
}
