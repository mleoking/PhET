/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.Color;

import edu.colorado.phet.buildanatom.model.IDynamicAtom;

/**
 *
 * @author John Blanco
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
        return new PeriodicTableNode.HightlightingElementCell( atomBeingWatched, atomicNumberOfCell, backgroundColor );
    }
}
