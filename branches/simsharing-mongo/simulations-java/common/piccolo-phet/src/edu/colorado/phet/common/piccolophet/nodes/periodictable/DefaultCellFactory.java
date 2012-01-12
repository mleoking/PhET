// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.periodictable;

import java.awt.Color;

/**
 * Default cell factory just shows an empty square with the specified background color and the element symbol
 *
 * @author Sam Reid
 */
public class DefaultCellFactory implements CellFactory {
    public ElementCell createCellForElement( int atomicNumberOfCell, Color backgroundColor ) {
        return new BasicElementCell( atomicNumberOfCell, backgroundColor );
    }
}