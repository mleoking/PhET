package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.Color;

/**
 * CellFactory creates ElementCell nodes to be added to and displayed in the periodic table node
 *
 * @author Sam Reid
 * @author John Blanco
 */
public interface CellFactory {
    ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor );
}