/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents lac promoter, which is the binding region on the DNA
 * strand for the RNA polymerase.
 * 
 * @author John Blanco
 */
public class LacPromoter extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(0, 137, 225);
	private static float HEIGHT = 2.5f;
	public static float WIDTH = 10;
	
	private RnaPolymerase rnaPolymeraseBondingPartner = null;
	
	public LacPromoter(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		addBindingPoint(new BindingPoint(ModelElementType.RNA_POLYMERASE, new PDimension(0, HEIGHT/2)));
	}
	
	public LacPromoter(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_PROMOTER;
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
	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.RNA_POLYMERASE && rnaPolymeraseBondingPartner == null){
			available = true;
		}
		return available;
	}

	@Override
	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof CapBindingRegion && rnaPolymeraseBondingPartner == null){
			rnaPolymeraseBondingPartner = (RnaPolymerase)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
	}
}
