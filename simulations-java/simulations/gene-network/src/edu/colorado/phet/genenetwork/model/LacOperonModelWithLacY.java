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
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl#getOpenSpotForLacY()
	 */
	@Override
	public Point2D getOpenSpotForLacY() {
		double yPos = CELL_MEMBRANE_RECT.getCenterY();
		double xPos = 0;
		double xMin = CELL_MEMBRANE_RECT.getCenterX() - 20;
		double xRange = getInteriorMotionBounds().getMaxX() - 20 - xMin;
		boolean openSpotFound = false;
		for (int i = 0; i < 100 && !openSpotFound; i++){
			xPos = xMin + xRange * RAND.nextDouble();
			openSpotFound = true;
			for (LacY lacY : getLacYList()){
				if (Math.abs(lacY.getMembraneDestinationRef().getX() - xPos) < MIN_DISTANCE_BETWEEN_LAC_Y){
					openSpotFound = false;
					break;
				}
			}
		}
		
		if (!openSpotFound){
			System.err.println(getClass().getName() + " - Warning: No open spots found, choosing arbitrarily.");
			// Use the last value chosen in the loop.
		}
		
		return new Point2D.Double(xPos, yPos);
	}
}
