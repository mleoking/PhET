// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.model;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


/**
 * This class adds functionality to the basic lac operon model.  The additions
 * primarily involve the inclusion of LacY, which is an enzyme (or protein or
 * some such thing) that is involved in transporting lactose across the cell
 * membrane.  At the time of this writing (March 2010), the tab that is most
 * closely associated with this model is the Lactose Transport tab.
 * 
 * @author John Blanco
 */
public class LacOperonModelWithLacY extends LacOperonModel {
	
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------
	
	// Constants that define the size and position of the cell membrane.
	private static final double CELL_MEMBRANE_THICKNESS = 4; // In nanometers.
	private static final Point2D CELL_MEMBRANE_CENTER_POS = new Point2D.Double(0, 40);
	
	// The rectangle that describes the position of the cell membrane.
	private static final Rectangle2D CELL_MEMBRANE_RECT = new Rectangle2D.Double(
			-MODEL_AREA_WIDTH,
			CELL_MEMBRANE_CENTER_POS.getY() - CELL_MEMBRANE_THICKNESS / 2,
			MODEL_AREA_WIDTH * 2,
			CELL_MEMBRANE_THICKNESS);
	
	private static final double MIN_DISTANCE_BETWEEN_LAC_Y = new LacY().getShape().getBounds2D().getWidth() * 1.1;

	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------
	
	public LacOperonModelWithLacY(GeneNetworkClock clock, boolean simulateLacY) {
		super(clock, simulateLacY);
		
		setDnaStrand(new DnaStrandWithLacY(this, DNA_STRAND_SIZE, DNA_STRAND_POSITION));
	}

	//----------------------------------------------------------------------------
	// Methods
	//----------------------------------------------------------------------------
	
	/**
	 * Get the cell membrane.  This variation of the lac operon model has one,
	 * hence the override.
	 */
	@Override
	public Rectangle2D getCellMembraneRect() {
		return new Rectangle2D.Double(CELL_MEMBRANE_RECT.getX(), CELL_MEMBRANE_RECT.getY(), CELL_MEMBRANE_RECT.getWidth(),
				CELL_MEMBRANE_RECT.getHeight());
	}

	@Override
	public Rectangle2D getInteriorMotionBounds() {
		// Subtract off the area above the cell membrane.
		Rectangle2D origRect = super.getInteriorMotionBounds();
		return new Rectangle2D.Double(origRect.getX(), origRect.getY(), origRect.getWidth(),
				CELL_MEMBRANE_RECT.getMinY() - origRect.getY());
	}

	@Override
	public Rectangle2D getInteriorMotionBoundsAboveDna() {
		// Subtract off the area above the cell membrane.
		Rectangle2D origRect = super.getInteriorMotionBoundsAboveDna();
		return new Rectangle2D.Double(origRect.getX(), origRect.getY(), origRect.getWidth(),
				CELL_MEMBRANE_RECT.getMinY() - origRect.getY());
	}
	
	@Override
	public Rectangle2D getExteriorMotionBounds() {
		// Determine the area above the cell membrane.
		Rectangle2D origRect = super.getInteriorMotionBounds();
		return new Rectangle2D.Double(origRect.getX(), CELL_MEMBRANE_RECT.getMaxY(), origRect.getWidth(),
				origRect.getMaxY() - CELL_MEMBRANE_RECT.getMaxY());
	}

	@Override
	public PositionWrtCell classifyPosWrtCell(Point2D pt) {
		if (pt.getY() < CELL_MEMBRANE_RECT.getMinY()){
			return PositionWrtCell.INSIDE_CELL;
		}
		else if (pt.getY() > CELL_MEMBRANE_RECT.getMaxY()){
			return PositionWrtCell.OUTSIDE_CELL;
		}
		else{
			return PositionWrtCell.WITHIN_CELL_MEMBRANE;
		}
	}
	
	/**
	 * Find an open spot on the membrane where a new LacY can be placed.
	 */
	@Override
	public Point2D getOpenSpotForLacY() {
		double yPos = CELL_MEMBRANE_RECT.getCenterY();
		double xPos = 0;
		double xMin = CELL_MEMBRANE_RECT.getCenterX() - 30;
		double xRange = getInteriorMotionBounds().getMaxX() - 10 - xMin;
		boolean openSpotFound = false;
		double minDistanceBetweenLacYs = MIN_DISTANCE_BETWEEN_LAC_Y;
		for (int attempts = 0; attempts < 10; attempts++){
			for (int i = 0; i < 100 && !openSpotFound; i++){
				xPos = xMin + ( xRange / 2 ) * (createBoundedGaussian() + 1); 
				openSpotFound = true;
				for (LacY lacY : getLacYList()){
					if (Math.abs(lacY.getMembraneDestinationRef().getX() - xPos) < minDistanceBetweenLacYs){
						openSpotFound = false;
						break;
					}
				}
			}
		}
		
		if (!openSpotFound){
			// No free spots were found using the random method, so now locate
			// the largest free segment and put the LacY in the center of it.
			double bestSegmentLeftXPos = xMin;
			double bestSegmentRightXPos = xMin;
			// Find the length of the segment that starts at offset = 0.
			double distanceToNearestLacY = Double.POSITIVE_INFINITY;
			for (LacY lacY : getLacYList()){
				double distanceToThisLacY = Math.abs(lacY.getMembraneDestinationRef().getX() - bestSegmentLeftXPos);
				if (distanceToThisLacY < distanceToNearestLacY){
					// Found a new nearest neighbor.
					distanceToNearestLacY = distanceToThisLacY;
					bestSegmentRightXPos = bestSegmentLeftXPos + distanceToNearestLacY;
				}
			}
			// Now, for each LacY, find its nearest neighbor to the right and
			// see if the distance between them is greater than the best
			// segment found so far.
			for (LacY lacY : getLacYList()){
				LacY closestNeighborToRight = null;
				for (LacY lacY2 : getLacYList()){
					if (lacY2 == lacY){
						// Pointless to compare to ourself.
						continue;
					}
					if (lacY2.getMembraneDestinationRef().getX() > lacY.getMembraneDestinationRef().getX()){
						// This neighbor is to the right.  Is it the closest one so far?
						if (closestNeighborToRight == null || lacY2.getMembraneDestinationRef().getX() < closestNeighborToRight.getMembraneDestinationRef().getX()){
							// Yes it is.
							closestNeighborToRight = lacY2;
						}
					}
				}
				
				// Figure out the end points of the segment between these two
				// LacYs.
				double segmentLeftXPos = lacY.getPositionRef().getX();
				double segmentRightXPos;
				if (closestNeighborToRight != null){
					segmentRightXPos = closestNeighborToRight.getMembraneDestinationRef().getX();
				}
				else{
					// If no neighbors were found to the right, this is
					// the rightmost LacY.
					segmentRightXPos = xMin + xRange;
				}
				
				// See if this segment is the largest one so far.
				if (segmentRightXPos - segmentLeftXPos > bestSegmentRightXPos - bestSegmentLeftXPos){
					// Indeed it is, so keep track of it.
					bestSegmentLeftXPos = segmentLeftXPos;
					bestSegmentRightXPos = segmentRightXPos;
				}
			}
			
			if (bestSegmentLeftXPos == 0 && bestSegmentRightXPos == 0){
				// Something went terribly wrong, so log an error and choose a spot.
				System.err.println(getClass().getName() + " - Error: Algorithm for finding largest free segment failed.");
				assert false;
				bestSegmentRightXPos = xMin + xRange;
			}

			// Make the chosen position be in the center of the largest segment.
			xPos = bestSegmentLeftXPos + ((bestSegmentRightXPos - bestSegmentLeftXPos) / 2);
		}
		
		return new Point2D.Double(xPos, yPos);
	}
	
	/**
	 * Create a bounded value between -1 and 1 that is essentially a Gaussian
	 * distribution.  This was created in order to have a preference for the
	 * LacY to be in the center of its position range so that it will be able
	 * to move the most lactose.
	 * @return
	 */
	private double createBoundedGaussian(){
		double boundedGaussian = Double.POSITIVE_INFINITY;
		int count = 0;
		while (boundedGaussian < -1 || boundedGaussian > 1){
			boundedGaussian = RAND.nextGaussian() / 2;
			count++;
		}
		return boundedGaussian;
	}
}
