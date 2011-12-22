// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Primary model class for the Multiple Cells tab.
 *
 * @author John Blanco
 */
public class MultipleCellsModel {

    public static final int MAX_CELLS = 110;
    private static final Random RAND = new Random();

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // List of cells in the model.
    public final ObservableList<Cell> cellList = new ObservableList<Cell>();

    // Locations where cells are placed.  This is initialized at construction
    // so that placements are consistent as cells come and go.
    public final List<Point2D> cellLocations = new ArrayList<Point2D>();

    public MultipleCellsModel() {
        initializeCellLocations();
    }

    public IClock getClock() {
        return clock;
    }

    /**
     * Restore model to initial conditions.  Should be called at least once
     * during initialization of the module.
     */
    public void reset() {
        // Clear out all existing cells.
        cellList.clear();
        // Add a single cell.  Use the cell list size as the seed so that the
        // same cell is always the same shape.
        setNumCells( 1 );
    }

    /**
     * Set the number of cells currently in the model.  Adds or removes cells
     * from the model until the number matches the target.
     *
     * @param numCells - target number of cells.
     */
    public void setNumCells( int numCells ) {

        assert numCells > 0 && numCells <= MAX_CELLS;  // Bounds checking.
        numCells = MathUtil.clamp( 1, numCells, MAX_CELLS ); // Defensive programming.

        if ( cellList.size() > numCells ) {
            // Remove cells from the end of the list.
            while ( cellList.size() > numCells ) {
                cellList.remove( cellList.size() - 1 );
            }
        }
        else if ( cellList.size() < numCells ) {
            while ( cellList.size() < numCells ) {
                Cell newCell = new Cell( cellList.size() ); // Use index as seed so that same cell looks the same.
                newCell.setPosition( cellLocations.get( cellList.size() ) );
                cellList.add( newCell );
            }
        }
    }

    /**
     * Get the number of cells currently in the model.
     *
     * @return - current number of cells.
     */
    public int getNumCells() {
        return cellList.size();
    }

    /**
     * Get a rectangle in model space that is centered at coordinates (0, 0)
     * and that is large enough to contain all of the cells.
     *
     * @return
     */
    public Rectangle2D getCellCollectionBounds() {

        assert cellList.size() > 0;  // Check that the model isn't in a state the makes this operation meaningless.

        double minX = 0;
        double minY = 0;
        double maxX = 0;
        double maxY = 0;
        for ( Cell cell : cellList ) {
            if ( cell.getShape().getBounds2D().getMinX() < minX ) {
                minX = cell.getShape().getBounds2D().getMinX();
                maxX = -minX;
            }
            if ( cell.getShape().getBounds2D().getMinY() < minY ) {
                minY = cell.getShape().getBounds2D().getMinY();
                maxY = -minY;
            }
            if ( cell.getShape().getBounds2D().getMaxX() > maxX ) {
                maxX = cell.getShape().getBounds2D().getMaxX();
                minX = -maxX;
            }
            if ( cell.getShape().getBounds2D().getMaxY() > maxY ) {
                maxY = cell.getShape().getBounds2D().getMaxY();
                minY = -maxY;
            }
        }
        return new Rectangle2D.Double( minX, minY, maxX - minX, maxY - minY );
    }

    private void initializeCellLocations() {
        assert cellLocations.size() == 0; // Should only be called once.

        // Transform for converting from unit-circle based locations to
        // locations that will work for the elliptical cells.
        AffineTransform transform = AffineTransform.getScaleInstance( Cell.CELL_SIZE.getWidth(), Cell.CELL_SIZE.getHeight() );

        // Set the first location to be at the origin.
        cellLocations.add( new Point2D.Double( 0, 0 ) );

        // Create the list of potential locations for cells.  This algorithm
        // is based on a unit circle, and the resulting positions are
        // transformed based on the dimensions of the cells.
        int layer = 1;
        int placedCells = 1;
        while ( placedCells < MAX_CELLS ) {
            List<Point2D> preTransformLocations = new ArrayList<Point2D>();
            int numCellsOnThisLayer = (int) Math.floor( layer * 2 * Math.PI );
            double angleIncrement = 2 * Math.PI / numCellsOnThisLayer;
            Vector2D nextLocation = new Vector2D();
            nextLocation.setMagnitudeAndAngle( layer, RAND.nextDouble() * 2 * Math.PI );
            for ( int i = 0; i < numCellsOnThisLayer && placedCells < MAX_CELLS; i++ ) {
                preTransformLocations.add( nextLocation.toPoint2D() );
                nextLocation.rotate( angleIncrement );
                placedCells++;
            }
            // Shuffle the locations.
            Collections.shuffle( preTransformLocations );
            // Transform the locations and add them to the final list.
            for ( Point2D preTransformLocation : preTransformLocations ) {
                cellLocations.add( transform.transform( preTransformLocation, null ) );
            }
            // Next layer.
            layer++;
        }
    }
}
