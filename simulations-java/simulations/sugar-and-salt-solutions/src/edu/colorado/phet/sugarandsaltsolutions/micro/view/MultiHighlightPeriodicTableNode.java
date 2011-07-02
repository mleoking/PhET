package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.CellFactory;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;

/**
 * A version of the periodic table that highlights the cell that matches the
 * supplied atom's configuration.
 */
public class MultiHighlightPeriodicTableNode extends PeriodicTableNode {

    public MultiHighlightPeriodicTableNode( PeriodicTableAtom atom, Color backgroundColor, final Integer... values ) {
        this( backgroundColor, new Function1<Integer, Boolean>() {
            public Boolean apply( Integer integer ) {
                return Arrays.asList( values ).contains( integer );
            }
        } );
    }

    public MultiHighlightPeriodicTableNode( Color backgroundColor, final Function1<Integer, Boolean> highlightRule ) {
        super( backgroundColor, new CellFactory() {
            public ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
                // Create a cell that highlights when its configuration matches that
                // of the atom being watched.
                if ( highlightRule.apply( atomicNumberOfCell ) ) {
                    return new HighlightedElementCell( atomicNumberOfCell, backgroundColor );
                }
                else {
                    return new BasicElementCell( atomicNumberOfCell, backgroundColor );
                }
            }
        } );
    }
}