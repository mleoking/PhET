//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Creates a shape that is the intersection of two shapes.
 * <p/>
 * CSG intro: https://secure.wikimedia.org/wikipedia/en/wiki/Constructive_solid_geometry
 * Rationale for intersection: http://groups.csail.mit.edu/graphics/classes/6.838/F01/lectures/SmoothSurfaces/0the_s040.html
 */
public class ShapeIntersection implements IShape {
    public final IShape a;
    public final IShape b;

    public ShapeIntersection( IShape a, IShape b ) {
        this.a = a;
        this.b = b;
    }

    public Shape toShape() {
        return new Area( a.toShape() ) {{
            intersect( new Area( b.toShape() ) );
        }};
    }

    public IShape getTranslatedInstance( double dx, double dy ) {
        return new ShapeIntersection( a.getTranslatedInstance( dx, dy ), b.getTranslatedInstance( dx, dy ) );
    }

    public ArrayList<Intersection> getIntersections( Ray ray ) {
        // for CSG intersection, intersection points need to be at the boundary of one surface, and INSIDE the other. If it was outside one of the
        // shapes, it would not be in the intersection

        ArrayList<Intersection> result = new ArrayList<Intersection>();

        // find all intersections with A that are in B
        for ( Intersection intersection : a.getIntersections( ray ) ) {
            if ( b.containsPoint( intersection.getPoint() ) ) {
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
        // Area's getBounds() was failing, so we need to use our own version
        return a.getBounds().createUnion( b.getBounds() );
    }

    public IShape getRotatedInstance( double angle, ImmutableVector2D rotationPoint ) {
        // rotate the children around the same rotation point
        return new ShapeIntersection( a.getRotatedInstance( angle, rotationPoint ), b.getRotatedInstance( angle, rotationPoint ) );
    }

    public ImmutableVector2D getCentroid() {
        // average child centroids. NOTE: this is NOT the true centroid!!!
        return a.getCentroid().getAddedInstance( b.getCentroid() ).times( 0.5 );
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
        return a.containsPoint( point ) && b.containsPoint( point );
    }
}
