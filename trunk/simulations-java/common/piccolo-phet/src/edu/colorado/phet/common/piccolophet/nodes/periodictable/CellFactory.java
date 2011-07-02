package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.BasicElementCell;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.ElementCell;

/**
 * @author Sam Reid
 */
public interface CellFactory {
    ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor );

    public static class Default implements CellFactory {
        public ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
            return new BasicElementCell( atomicNumberOfCell, backgroundColor );
        }
    }
}
