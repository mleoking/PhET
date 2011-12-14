// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.multiplecells.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.ObservableList;

/**
 * Primary model class for the Multiple Cells tab.
 *
 * @author John Blanco
 */
public class MultipleCellsModel {

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
}
