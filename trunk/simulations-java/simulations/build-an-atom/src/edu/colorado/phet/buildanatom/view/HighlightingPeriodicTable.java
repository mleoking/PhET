/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.*;

import edu.colorado.phet.buildanatom.model.IDynamicAtom;

/**
 * A version of the periodic table that highlights the cell that matches the
 * supplied atom's configuration.
 */
public class HighlightingPeriodicTable extends PeriodicTableNode {

    /**
     * Constructor.
     */
    public HighlightingPeriodicTable( IDynamicAtom atom, Color backgroundColor ) {
        super( atom, backgroundColor );
    }

    @Override
    protected ElementCell createCellForElement( IDynamicAtom atomBeingWatched, int atomicNumberOfCell, Color backgroundColor ) {
        // Create a cell that highlights when its configuration matches that
        // of the atom being watched.
        return new HighlightingElementCell( atomBeingWatched, atomicNumberOfCell, backgroundColor );
    }
}
