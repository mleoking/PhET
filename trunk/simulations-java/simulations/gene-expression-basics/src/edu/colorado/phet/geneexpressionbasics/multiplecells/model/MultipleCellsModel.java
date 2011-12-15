// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.geom.Point2D;
import java.util.Random;

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

    public MultipleCellsModel() {
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
        // Add a single cell.
        cellList.add( new Cell( new Point2D.Double( 0, 0 ) ) );
    }

    public void setNumCells( int numCells ) {
        if ( cellList.size() > numCells ) {
            // Remove cells from the end of the list.
            while ( cellList.size() > numCells ) {
                cellList.remove( cellList.size() - 1 );
            }
        }
        else if ( cellList.size() < numCells ) {
            Cell newCell = new Cell();
            placeCellInOpenLocation( newCell );
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
}
