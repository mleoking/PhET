package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.*;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.BasicElementCell;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.ElementCell;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode.HighlightedElementCell;

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

    //Cell factory that highlights the specified elements
    public static class HighlightElements implements CellFactory {
        private final Function1<Integer, Boolean> highlightRule;

        public HighlightElements( final Integer... highlighted ) {
            this( new Function1<Integer, Boolean>() {
                public Boolean apply( Integer integer ) {
                    return Arrays.asList( highlighted ).contains( integer );
                }
            } );
        }

        public HighlightElements( final Function1<Integer, Boolean> highlightRule ) {
            this.highlightRule = highlightRule;
        }

        //If the cell should be highlighted, return a highlighted node instead of a regular one
        public ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
            return highlightRule.apply( atomicNumberOfCell ) ?
                   new HighlightedElementCell( atomicNumberOfCell, backgroundColor ) :
                   new BasicElementCell( atomicNumberOfCell, backgroundColor );
        }
    }
}
