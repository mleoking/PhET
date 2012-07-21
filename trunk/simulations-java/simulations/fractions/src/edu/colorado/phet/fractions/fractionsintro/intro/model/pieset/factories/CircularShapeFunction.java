// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Slice;

/**
 * Function that gives shapes for circle pie slices. Factored out to enable equality testing for regression tests, so that we can add lombok annotation for equals.
 *
 * @author Sam Reid
 */
public @Data class CircularShapeFunction extends F<Slice, Shape> {
    public final double extent;
    public final double radius;

    @Override public Shape f( final Slice slice ) { return createShape( slice.position, slice.angle ); }

    public Shape createShape( Vector2D position, double angle ) {
        double epsilon = 1E-6;
        return extent >= Math.PI * 2 - epsilon ?
               new Ellipse2D.Double( position.getX() - radius, position.getY() - radius, radius * 2, radius * 2 ) :
               new Arc2D.Double( position.getX() - radius, position.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }
}