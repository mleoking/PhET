// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Function that gives shapes for stacked horizontal slices. Factored out to enable equality testing for regression tests, so that we can add lombok annotation for equals.
 *
 * @author Sam Reid
 */
@Data public class StackedHorizontalSliceToShape extends F<Slice, Shape> {
    public final double width;
    public final double barHeight;

    @Override public Shape f( final Slice slice ) {
        Vector2D tip = slice.position;
        return new Rectangle2D.Double( tip.getX() - width / 2, tip.getY() - barHeight / 2, width, barHeight );
    }
}