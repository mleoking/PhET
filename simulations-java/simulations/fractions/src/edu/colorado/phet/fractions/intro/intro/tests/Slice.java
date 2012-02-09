// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.tests;

import java.awt.Shape;
import java.awt.geom.Arc2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * @author Sam Reid
 */
public class Slice {
    public final ImmutableVector2D tip;
    public final double angle;//in radians
    public final double extent;//in radians
    public final double radius;
    public final boolean dragging;
    public final ImmutableVector2D center;

    public Slice( ImmutableVector2D tip, double _angle, double extent, double radius, boolean dragging ) {

        //TODO: More elegant way to do this, perhaps with atan2?
        while ( _angle < 0 ) {
            _angle += 2 * Math.PI;
        }
        while ( _angle > 2 * Math.PI ) {
            _angle -= 2 * Math.PI;
        }
        this.tip = tip;
        this.angle = _angle;
        this.extent = extent;
        this.radius = radius;
        this.dragging = dragging;

        this.center = new ImmutableVector2D( toShape().getBounds2D().getCenterX(), toShape().getBounds2D().getCenterY() );
    }

    public Shape toShape() {
        return new Arc2D.Double( tip.getX() - radius, tip.getY() - radius, radius * 2, radius * 2, angle * 180.0 / Math.PI, extent * 180.0 / Math.PI, Arc2D.PIE );
    }

    public Slice translate( ImmutableVector2D delta ) {
        return translate( delta.getX(), delta.getY() );
    }

    public Slice translate( double dx, double dy ) {
        return new Slice( tip.plus( dx, dy ), angle, extent, radius, dragging );
    }

    public Slice dragging( boolean dragging ) {
        return new Slice( tip, angle, extent, radius, dragging );
    }

    public Slice angle( double angle ) {
        return new Slice( tip, angle, extent, radius, dragging );
    }

    public Slice tip( ImmutableVector2D tip ) {
        return new Slice( tip, angle, extent, radius, dragging );
    }
}