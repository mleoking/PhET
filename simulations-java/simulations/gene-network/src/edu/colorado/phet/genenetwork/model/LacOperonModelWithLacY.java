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
	
	//----------------------------------------------------------------------------
	// Instance Data
	//----------------------------------------------------------------------------
	
    // The rectangle that describes the position of the cell membrane.
    private Rectangle2D cellMembraneRect;
    
	//----------------------------------------------------------------------------
	// Constructor(s)
	//----------------------------------------------------------------------------
	
	public LacOperonModelWithLacY(GeneNetworkClock clock, boolean simulateLacY) {
		super(clock, simulateLacY);
		
		setDnaStrand(new DnaStrandWithLacY(this, DNA_STRAND_SIZE, DNA_STRAND_POSITION));
		
		cellMembraneRect = new Rectangle2D.Double(
				-MODEL_AREA_WIDTH,
				CELL_MEMBRANE_CENTER_POS.getY() - CELL_MEMBRANE_THICKNESS / 2,
				MODEL_AREA_WIDTH * 2,
				CELL_MEMBRANE_THICKNESS);
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
		return new Rectangle2D.Double(cellMembraneRect.getX(), cellMembraneRect.getY(), cellMembraneRect.getWidth(),
				cellMembraneRect.getHeight());
	}
}
