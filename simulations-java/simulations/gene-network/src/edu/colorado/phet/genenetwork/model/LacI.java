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
	
	private static final Paint ELEMENT_PAINT = new Color(200, 200, 200);
	private static double WIDTH = 7;   // In nanometers.
	private static double HEIGHT = 4;  // In nanometers.
	private static PDimension LAC_OPERATOR_BINDING_POINT_OFFSET = 
		new PDimension(0, -HEIGHT/2 + LacOperator.getBindingRegionSize().getHeight());
	
	private LacOperator lacOperatorBondingPartner = null;
	private Lactose lactoseBondingPartner = null;
	private BondingState lacOperatorBondingState = BondingState.UNBOUND_AND_AVAILABLE;
	private Point2D targetPositionForLacOperatorBond = new Point2D.Double();
	
	public LacI(IObtainGeneModelElements model, Point2D initialPosition) {
		super(model, createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		setMotionStrategy(new DirectedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds()));
		// Add binding point for LacOperator.
		addBindingPoint(new BindingPoint(ModelElementType.LAC_OPERATOR, LAC_OPERATOR_BINDING_POINT_OFFSET));
	}
	
	public LacI(IObtainGeneModelElements model) {
		this(model, new Point2D.Double());
	}
	
	public LacI(){
		this(null);
	}
	
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
		
		if (lacOperatorBondingState == BondingState.UNBOUND_AND_AVAILABLE){
			assert lacOperatorBondingPartner == null;  // For debug - Make sure consistent with bonding state.
			lacOperatorBondingPartner = lacOperator;
			proposalAccepted = true;
			
			// Set ourself up to move toward the bonding location.
			lacOperatorBondingState = BondingState.MOVING_TOWARDS_BOND;
			Dimension2D partnerOffset = lacOperatorBondingPartner.getBindingPointForElement(getType()).getOffset();
			Dimension2D myOffset = getBindingPointForElement(lacOperatorBondingPartner.getType()).getOffset();
			double xDest = lacOperatorBondingPartner.getPositionRef().getX() + partnerOffset.getWidth() - 
				myOffset.getWidth();
			double yDest = lacOperatorBondingPartner.getPositionRef().getY() + partnerOffset.getHeight() - 
				myOffset.getHeight();
			getMotionStrategyRef().setDestination(xDest, yDest);
			targetPositionForLacOperatorBond.setLocation(xDest, yDest);
		}
		
		return proposalAccepted;
	}
	
	public void finalizeBond(LacOperator lacOperator){
		if (lacOperator != lacOperatorBondingPartner){
			System.err.println(getClass().getName() + " - Error: Finalize request from non-partner.");
			assert false;
			return;
		}
		setMotionStrategy(new StillnessMotionStrategy(this));
		setPosition(targetPositionForLacOperatorBond);
		lacOperatorBondingState = BondingState.BONDED;
	}
	
	/**
	 * Get the location in absolute space of the binding point for this type
	 * of model element.
	 */
	public Point2D getBindingPointLocation(LacOperator lacOperator){
		return new Point2D.Double(getPositionRef().getX() + LAC_OPERATOR_BINDING_POINT_OFFSET.getWidth(),
				getPositionRef().getY() + LAC_OPERATOR_BINDING_POINT_OFFSET.getHeight());
	}
}
