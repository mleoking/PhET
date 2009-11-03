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
import java.util.ArrayList;


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
	
	private LacOperator lacOperatorBondingPartner = null;
	private Lactose lactoseBondingPartner = null;
	
	public LacI(Point2D initialPosition) {
		super(createActiveConformationShape(), initialPosition, ELEMENT_PAINT);
		setMotionStrategy(new WeightedRandomWalkMotionStrategy(this, LacOperonModel.getModelBounds()));
	}
	
	public LacI() {
		this(new Point2D.Double());
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

	@Override
	public boolean availableForBonding(ModelElementType elementType) {
		boolean available = false;
		switch (elementType){
		case LAC_OPERATOR:
			if (lacOperatorBondingPartner == null){
				available = true;
			}
			break;
			
		}
		return available;
	}

	@Override
	public boolean considerProposalFrom(IModelElement modelElement) {
		// TODO Auto-generated method stub
		return super.considerProposalFrom(modelElement);
	}

	@Override
	public void updatePotentialBondingPartners(
			ArrayList<IModelElement> modelElements) {
		// TODO Auto-generated method stub
		super.updatePotentialBondingPartners(modelElements);
	}
	
	
	
}
