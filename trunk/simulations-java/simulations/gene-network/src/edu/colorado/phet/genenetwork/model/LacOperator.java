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
	
	private static final Paint ELEMENT_PAINT = new Color(200, 200, 200);
	private static final double WIDTH = 7;   // In nanometers.
	private static final double HEIGHT = 3;  // In nanometers.
	private static final Dimension2D LAC_I_BINDING_POINT_OFFSET = new PDimension(0, HEIGHT/2); 
	
	private LacI lacIBondingPartner = null;
	private BondingState bondingState = BondingState.UNBOUND_AND_AVAILABLE; 
	
	public LacOperator(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createShape(), initialPosition, ELEMENT_PAINT);
		// Add binding point for LacI.
		addBindingPoint(new BindingPoint(ModelElementType.LAC_I, LAC_I_BINDING_POINT_OFFSET));
	}
	
	public LacOperator(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	@Override
	public ModelElementType getType() {
		return ModelElementType.LAC_OPERATOR;
	}
	
	@Override
	public void stepInTime(double dt) {
		switch (bondingState){
		case UNBOUND_AND_AVAILABLE:
			attemptToStartBond();
			break;
		case MOVING_TOWARDS_BOND:
			checkBondCompleted();
			break;
		case BONDED:
			// TODO
			break;
		case UNBONDED_BUT_UNAVALABLE:
			// TODO
			break;
		default:
			// Should never get here, should be debugged if it does.
			assert false;
			break;
		}
		super.stepInTime(dt);
	}
	
	private void attemptToStartBond(){
		assert lacIBondingPartner == null;
		// Search for a partner to bond with.
		ArrayList<LacI> potentialPartnerList = getModel().getLacIList();
		
		for (LacI lacI : potentialPartnerList){
			if (getPositionRef().distance(lacI.getPositionRef()) < BOND_INITIATION_RANGE){
				if (lacI.considerProposalFrom(this)){
					// Bond formed.
					bondingState = BondingState.MOVING_TOWARDS_BOND;
					lacIBondingPartner = lacI;
				}
			}
		}
	}
	
	private void checkBondCompleted(){
		assert lacIBondingPartner != null;

		// Calculate the current location of our LacI attachment point.
		Point2D lacIAttachmentPtLocation = 
			new Point2D.Double(getPositionRef().getX() + LAC_I_BINDING_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_I_BINDING_POINT_OFFSET.getHeight());
		
		// Check the distance between the attachment points.
		if (lacIAttachmentPtLocation.distance(lacIBondingPartner.getBindingPointLocation(this)) < BOND_FORMING_DISTANCE){
			// Close enough to form a bond.
			lacIBondingPartner.finalizeBond(this);
			bondingState = BondingState.BONDED;
		}
	}

	private static Shape createShape(){
		
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
	}
	
	public static Dimension2D getBindingRegionSize(){
		return new PDimension(WIDTH * 0.5, HEIGHT / 2);
	}
}
