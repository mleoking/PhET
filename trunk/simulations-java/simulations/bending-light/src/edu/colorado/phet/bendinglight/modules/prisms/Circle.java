// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Circle implementation for use in prisms
 *
 * @author Sam Reid
 */
public class Circle implements IShape {
    private Vector2D center;
    private double radius;

    public Circle( Vector2D center, double radius ) {
        this.center = center;
        this.radius = radius;
    }

    public Shape toShape() {
        return toEllipse();
    }

    private Ellipse2D.Double toEllipse() {
        return new Ellipse2D.Double( center.getX() - radius, center.getY() - radius, radius * 2, radius * 2 );
    }

    public IShape getTranslatedInstance( double dx, double dy ) {
        return new Circle( center.plus( dx, dy ), radius );
    }

    //Finds the intersections between the edges of the circle and the specified ray
    public ArrayList<Intersection> getIntersections( Ray ray ) {
        //Find the line segment corresponding to the specified ray
        final Line2D.Double line = new Line2D.Double( ray.tail.toPoint2D(), ray.tail.plus( ray.directionUnitVector ).toPoint2D() );

        //Find the intersections between the infinite line (not a segment) and the circle
        final Point2D[] intersectionArray = MathUtil.getLineCircleIntersection( toEllipse(), line );

        //Convert Point2D => Intersection instances
        final ArrayList<Intersection> intersectionList = new ArrayList<Intersection>();
        for ( Point2D intersectionPoint : intersectionArray ) {
            //Filter out getLineCircleIntersection nulls, which are returned if there is no intersection
            if ( intersectionPoint != null ) {
                Vector2D vector = new Vector2D( intersectionPoint ).minus( ray.tail );

                //Only consider intersections that are in front of the ray
                if ( vector.dot( ray.directionUnitVector ) > 0 ) {
                    Vector2D normalVector = new Vector2D( intersectionPoint ).minus( center ).getNormalizedInstance();
                    if ( normalVector.dot( ray.directionUnitVector ) > 0 ) {
                        normalVector = normalVector.negate();
                    }
                    intersectionList.add( new Intersection( normalVector, new Vector2D( intersectionPoint ) ) );
                }
            }
        }
        return intersectionList;
    }

    public Rectangle2D getBounds() {
        return new Rectangle2D.Double( center.getX() - radius, center.getY() - radius, radius * 2, radius * 2 );
    }

    public IShape getRotatedInstance( double angle, Vector2D rotationPoint ) {
        // we create a new circle with a rotated center point
        Vector2D vectorAboutCentroid = getRotationCenter().minus( rotationPoint );
        final Vector2D rotated = vectorAboutCentroid.getRotatedInstance( angle );
        return new Circle( rotated.plus( rotationPoint ), radius );
    }

    public Vector2D getRotationCenter() {
        return center;
    }

    //Signify that the circle can't be rotated
    public Option<Vector2D> getReferencePoint() {
        return new Option.None<Vector2D>();
    }

    public boolean containsPoint( Vector2D point ) {
        return point.distance( center ) <= radius;
    }
}
