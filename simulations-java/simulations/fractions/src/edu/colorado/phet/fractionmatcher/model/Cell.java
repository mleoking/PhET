// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Cells where the fractions start (i.e. where the user drags them from)
 *
 * @author Sam Reid
 */
@Data public class Cell {
    public final ImmutableRectangle2D rectangle;

    //REVIEW you removed field j, now this comment is wrong and we don't know if i is row or column index.
    //Index of the site in the grid, top left is (0,0)
    public final int i;

    public Vector2D getPosition() {return new Vector2D( rectangle.getCenter() );}

    public Shape toRoundedRectangle() { return rectangle.toRoundedRectangle( 20, 20 ); }
}