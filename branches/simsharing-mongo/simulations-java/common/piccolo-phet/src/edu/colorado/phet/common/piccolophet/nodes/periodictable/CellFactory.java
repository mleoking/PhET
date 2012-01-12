package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.Color;

/**
 * CellFactory creates ElementCell nodes to be added to and displayed in the periodic table node
 *
 * @author Sam Reid
 * @author John Blanco
 */
public interface CellFactory {

    /**
     * Creates an ElementCell with the specified atomic number and background color (which may be ignored depending on the highlighting rule)
     *
     * @param atomicNumberOfCell the atomic number of the cell to create, for purposes of showing the element name, etc.
     * @param backgroundColor    background color for non-highlighted cells
     * @return the new ElementCell
     */
    ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor );
}