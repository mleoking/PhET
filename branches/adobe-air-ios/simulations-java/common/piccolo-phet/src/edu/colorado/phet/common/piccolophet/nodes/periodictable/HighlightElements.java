// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static java.util.Arrays.asList;

/**
 * Cell factory that highlights the specified elements
 *
 * @author Sam Reid
 */
public class HighlightElements implements CellFactory {
    private final Function1<Integer, Boolean> highlightRule;

    public HighlightElements( final Integer... highlighted ) {
        this( new Function1<Integer, Boolean>() {
            public Boolean apply( Integer integer ) {
                return asList( highlighted ).contains( integer );
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
