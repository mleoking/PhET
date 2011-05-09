//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Creates a shape that is the difference of two shapes. It contains a point x iff: A contains x && B does not contain x
 * <p/>
 * CSG intro: https://secure.wikimedia.org/wikipedia/en/wiki/Constructive_solid_geometry
 * Rationale for intersection: http://groups.csail.mit.edu/graphics/classes/6.838/F01/lectures/SmoothSurfaces/0the_s040.html
 */
public class ShapeDifference implements IShape {
    public final IShape a;
    public final IShape b;

    public ShapeDifference( IShape a, IShape b ) {
        this.a = a;
        this.b = b;
    }

    public Shape toShape() {
        return new Area( a.toShape() ) {{
            subtract( new Area( b.toShape() ) );
        }};
    }

    public IShape getTranslatedInstance( double dx, double dy ) {
        return new ShapeDifference( a.getTranslatedInstance( dx, dy ), b.getTranslatedInstance( dx, dy ) );
    }

    public ArrayList<Intersection> getIntersections( Ray ray ) {
        // for CSG difference, intersection points need to be at the boundary of one surface, and either be INSIDE A, or OUTSIDE B
        ArrayList<Intersection> result = new ArrayList<Intersection>();

        // find all intersections with A that are outside of B
        for ( Intersection intersection : a.getIntersections( ray ) ) {
            if ( !b.containsPoint( intersection.getPoint() ) ) {
                result.add( intersection );
            }
        }

        // find all intersections with B that are in A
        for ( Intersection intersection : b.getIntersections( ray ) ) {
            if ( a.containsPoint( intersection.getPoint() ) ) {
                result.add( intersection );
            }
        }

        return result;
    }

    public Rectangle2D getBounds() {
        // as a simplification, we just need to return A's bounds
        return a.getBounds();
    }

    public IShape getRotatedInstance( double angle, ImmutableVector2D rotationPoint ) {
        // rotate the children around the same rotation point
        return new ShapeDifference( a.getRotatedInstance( angle, rotationPoint ), b.getRotatedInstance( angle, rotationPoint ) );
    }

    public ImmutableVector2D getRotationCenter() {
        // just grab A's centroid, since it is simplier. NOTE: this is NOT the true centroid!!!
        return a.getRotationCenter();
    }

    public Option<ImmutableVector2D> getReferencePoint() {
        // return the first viable centroid
        if ( a.getReferencePoint().isSome() ) {
            return a.getReferencePoint();
        }
        else {
            return b.getReferencePoint();
        }
    }

    public boolean containsPoint( ImmutableVector2D point ) {
        return a.containsPoint( point ) && !b.containsPoint( point );
    }
}
