// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Cells where the fractions start
 *
 * @author Sam Reid
 */
@Data public class Cell {
    public final ImmutableRectangle2D rectangle;

    //Index of the site in the grid, top left is (0,0)
    public final int i;
    public final int j;

    public ImmutableVector2D position() {return rectangle.getCenter();}
}