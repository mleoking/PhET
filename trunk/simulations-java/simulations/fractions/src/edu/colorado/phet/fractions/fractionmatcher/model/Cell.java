// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.model;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * Cells where the fractions start (i.e. where the user drags them from)
 *
 * @author Sam Reid
 */
@Data public class Cell {
    public final ImmutableRectangle2D rectangle;

    public Vector2D getPosition() {return new Vector2D( rectangle.getCenter() );}

    public Shape toRoundedRectangle() { return rectangle.toRoundedRectangle( 20, 20 ); }
}