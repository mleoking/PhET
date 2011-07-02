package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.*;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.piccolophet.nodes.periodictable.PeriodicTableNode;

/**
 * A version of the periodic table that highlights the cell that matches the
 * supplied atom's configuration.
 */
public class MultiHighlightPeriodicTableNode extends PeriodicTableNode {

    private final Function1<Integer, Boolean> highlightRule;

    public MultiHighlightPeriodicTableNode( PeriodicTableAtom atom, Color backgroundColor, final Integer... values ) {
        this( atom, backgroundColor, new Function1<Integer, Boolean>() {
            public Boolean apply( Integer integer ) {
                return Arrays.asList( values ).contains( integer );
            }
        } );
    }

    public MultiHighlightPeriodicTableNode( PeriodicTableAtom atom, Color backgroundColor, Function1<Integer, Boolean> highlightRule ) {
        super( atom, backgroundColor );
        this.highlightRule = highlightRule;
    }

    @Override protected ElementCell createCellForElement( PeriodicTableAtom atomBeingWatched, int atomicNumberOfCell, Color backgroundColor ) {
        // Create a cell that highlights when its configuration matches that
        // of the atom being watched.
        if ( highlightRule.apply( atomicNumberOfCell ) ) {
            return new HighlightedElementCell( atomicNumberOfCell, backgroundColor );
        }
        else {
            return new BasicElementCell( atomBeingWatched, atomicNumberOfCell, backgroundColor );
        }
    }
}
