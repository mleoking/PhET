// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model.pieset.factories;

import fj.F;
import lombok.Data;

import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.fractions.util.immutable.Vector2D;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;

/**
 * Function that gives shapes for cake slices. Factored out to enable equality testing for regression tests, so that we can add lombok annotation for equals.
 *
 * @author Sam Reid
 */
public @Data class CakeSliceFunction extends F<Slice, Shape> {
    private final double extent;
    private final double radius;
    private final double spacing;

    @Override public Shape f( final Slice slice ) {
        double epsilon = 1E-6;
        Vector2D tip = slice.position;
        double angle = slice.angle;
        final Ellipse2D.Double boundingEllipse = new Ellipse2D.Double( tip.x - radius, tip.y - radius, radius * 2, radius * 2 - radius * 0.65 );

        //Special case for half-pies since they should be vertical instead of horizontal (like pies)
        if ( Math.abs( extent - Math.PI ) < epsilon ) {
            angle = angle + Math.PI / 2;
        }
        return extent >= Math.PI * 2 - epsilon ?
               boundingEllipse :
               new Arc2D.Double( boundingEllipse.x, boundingEllipse.y, boundingEllipse.width, boundingEllipse.height, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }
}