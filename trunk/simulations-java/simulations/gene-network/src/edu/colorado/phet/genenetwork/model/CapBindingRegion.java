/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.umd.cs.piccolo.util.PDimension;


/**
 * Class that represents CAP binding region on the DNA strand.
 * 
 * @author John Blanco
 */
public class CapBindingRegion extends SimpleModelElement {
	
	private static final Paint ELEMENT_PAINT = new Color(247, 143, 36);
	private static float HEIGHT = 2.5f;
	public static float WIDTH = 5;
	
	private Cap capBondingPartner = null;
	
	public CapBindingRegion(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		addBindingPoint(new BindingPoint(ModelElementType.CAP, new PDimension(0, HEIGHT)));
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
	public void updatePositionAndMotion(double dt) {
		super.updatePositionAndMotion(dt);
	}

	@Override
	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		if (elementType == ModelElementType.CAP && capBondingPartner == null){
			available = true;
		}
		return available;
	}

	@Override
	public boolean considerProposalFrom(IModelElement modelElement) {
		boolean proposalAccepted = false;

		if (modelElement instanceof Cap && capBondingPartner == null){
			capBondingPartner = (Cap)modelElement;
			proposalAccepted = true;
		}
		
		return proposalAccepted;
	}

	private void updatePotentialBondingPartners( ArrayList<Cap> capList ) {
		// Seek to bond with free elements that are within range and that
		// match our needs.
		if (capBondingPartner == null){
			for (Cap cap : capList){
				
				// Look for a bond with Cap.
				if (cap.getType() == ModelElementType.CAP &&
					getPositionRef().distance(cap.getPositionRef()) <= BONDING_RANGE &&
					cap.availableForBonding(getType())){
					
					// Propose a bond with this element
					if (cap.considerProposalFrom(this)){
						// Proposal accepted.  Note that the bond is only
						// started at this point, and not really finalized
						// until the binding points are in the same location.
						capBondingPartner = (Cap)cap;
						break;
					}
				}
			}
		}
	}
}
