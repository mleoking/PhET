// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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

    public static final int MAX_CELLS = 20;
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

    public void setNumCells( int numCells ) {
        if ( cellList.size() > numCells ) {
            // Remove cells from the end of the list.
            while ( cellList.size() > numCells ) {
                cellList.remove( cellList.size() - 1 );
            }
        }
        else if ( cellList.size() < numCells ) {
            Cell newCell = new Cell( cellList.size() ); // Use index as seed so that same cell looks the same.
            newCell.setPosition( cellLocations.get( cellList.size() ) );
            cellList.add( newCell );
        }
    }

    private void placeCellInOpenLocation( Cell cell ) {
        if ( cellList.size() == 0 ) {
            cell.setPosition( 0, 0 );
        }
        else {
            // TODO: Just random for now, needs to be a search.
            cell.setPosition( ( RAND.nextDouble() - 0.5 ) * 2 * cell.getShape().getBounds2D().getWidth(),
                              ( RAND.nextDouble() - 0.5 ) * 2 * cell.getShape().getBounds2D().getHeight() );
        }
    }

    private void initializeCellLocations() {
        assert cellLocations.size() == 0; // Should only be called once.

        // Transform for converting from unit-circle based locations to
        // locations that will work for the elliptical cells.
        AffineTransform transform = AffineTransform.getScaleInstance( Cell.CELL_SIZE.getWidth(), Cell.CELL_SIZE.getHeight() );

        // Set the first location to be at the origin.
        cellLocations.add( new Point2D.Double( 0, 0 ) );

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
