package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.BasicElementCell;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.ElementCell;

/**
 * Determines how to create PNodes for cells in the periodic table.
 *
 * @author Sam Reid
 */
public interface CellFactory {
    ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor );

    //Default cell just shows an empty square with the specified background color and the element symbol
    public static class Default implements CellFactory {
        public ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
            return new BasicElementCell( atomicNumberOfCell, backgroundColor );
        }
    }
}
