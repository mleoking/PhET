// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class DnaStrandWithLacY extends DnaStrand {

	protected DnaSegmentSpace lacYGeneSpace;
	
	public DnaStrandWithLacY(IGeneNetworkModelControl model, Dimension2D size, Point2D initialPosition) {
		super(model, size, initialPosition);
		
		double xPos = getLacZGeneLocation().getX() + new LacZGene().getShape().getBounds2D().getMaxX() +
			new LacYGene().getShape().getBounds2D().getWidth() / 2 + 1;
		lacYGeneSpace = new DnaSegmentSpace(this, new LacYGene().getShape(), new Point2D.Double(xPos, 0));
		shapeList.add(lacYGeneSpace);
	}

	
	@Override
	protected void handleModelElementAdded(SimpleModelElement modelElement) {
		super.handleModelElementAdded(modelElement);
		// Add highlighting for the LacY gene.
		if (modelElement instanceof LacYGene){
			lacYGeneSpace.setEyeCatching(true);
			lacYGeneSpace.setModelElement(modelElement);
		}
	}


	@Override
	public Point2D getLacYGeneLocation() {
		return new Point2D.Double(getPositionRef().getX() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getX(),
				getPositionRef().getY() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getY());
	}


	@Override
	public boolean isOnLacYGeneSpace(Point2D pt) {
		Rectangle2D uncompensatedBounds = lacYGeneSpace.getBounds2D();
		Rectangle2D compensatedBounds = new Rectangle2D.Double(
			uncompensatedBounds.getX() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX(),
			uncompensatedBounds.getY() + lacYGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY(),
			uncompensatedBounds.getWidth(),
			uncompensatedBounds.getHeight() + RANGE_FOR_PROXIMITRY_TEST);
		return compensatedBounds.contains(pt);
	}
	
	/**
	 * Returns true if the LacZ gene is in place on the DNA strand and false
	 * if not.
	 * 
	 * @return
	 */
	@Override
	public boolean isLacYGeneInPlace(){
		LacYGene lacYGene = model.getLacYGene();
		boolean lacYGeneInPlace = false;
		if (lacYGene != null){
			double xPos = lacYGeneSpace.getOffsetFromDnaStrandPosRef().getX() + getPositionRef().getX();
			double yPos = lacYGeneSpace.getOffsetFromDnaStrandPosRef().getY() + getPositionRef().getY();
			// Use a range for this test to account for floating point errors.
			if ( lacYGene.getPositionRef().getX() + ERROR_RANGE > xPos &&
				 lacYGene.getPositionRef().getX() - ERROR_RANGE <  xPos &&
				 lacYGene.getPositionRef().getY() + ERROR_RANGE > yPos &&
				 lacYGene.getPositionRef().getY() - ERROR_RANGE <  yPos ){
				
				lacYGeneInPlace = true;
			}
		}
		
		return lacYGeneInPlace;
	}
}
