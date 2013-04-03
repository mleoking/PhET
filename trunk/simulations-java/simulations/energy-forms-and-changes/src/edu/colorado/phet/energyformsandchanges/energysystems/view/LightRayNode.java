// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents a ray of light in the view.  Rays of light can have
 * shapes that reduce or block the amount of light passing through.
 *
 * @author John Blanco
 */
public class LightRayNode extends PNode {
    private static final double STROKE_THICKNESS = 2;
    private static final int SEARCH_ITERATIONS = 10;
    private static final double FADE_COEFFICIENT_IN_AIR = 0.005;

    private final List<LightAbsorbingShape> lightAbsorbingShapes = new ArrayList<LightAbsorbingShape>();
    private final List<PointAndFadeCoefficient> pointAndFadeCoefficientList = new ArrayList<PointAndFadeCoefficient>();
    private final Vector2D origin;
    private final Vector2D endpoint;
    private final Color color;

    public LightRayNode( Vector2D origin, Vector2D endpoint, Color color ) {
        this.origin = origin;
        this.endpoint = endpoint;
        this.color = color;
        updateLineSegments();
    }

    public void addLightAbsorbingShape( LightAbsorbingShape lightAbsorbingShape ) {
        lightAbsorbingShapes.add( lightAbsorbingShape );
        lightAbsorbingShape.lightAbsorptionCoefficient.addObserver( new SimpleObserver() {
            public void update() {
                updateLineSegments();
            }
        } );
    }

    public void removeLightAbsorbingShape( LightAbsorbingShape lightAbsorbingShape ) {
        lightAbsorbingShape.lightAbsorptionCoefficient.removeAllObservers();
        lightAbsorbingShapes.remove( lightAbsorbingShape );
        updateLineSegments();
    }

    private void updateLineSegments() {
        removeAllChildren();
        pointAndFadeCoefficientList.clear();

        // Add the initial start and end points.
        pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( origin, FADE_COEFFICIENT_IN_AIR ) );
        pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( endpoint, 0 ) );

        // Add the entry and exit points for each shape.
        for ( LightAbsorbingShape lightAbsorbingShape : lightAbsorbingShapes ) {
            if ( lineIntersectsShapeIntersects( origin, endpoint, lightAbsorbingShape.shape ) ) {
                Vector2D entryPoint = getShapeEntryPoint( origin, endpoint, lightAbsorbingShape.shape );
                assert entryPoint != null; // It's conceivable that we could handle a case where the line originates in a shape, but we don't for now.
                pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( entryPoint, lightAbsorbingShape.lightAbsorptionCoefficient.get() ) );
                Vector2D exitPoint = getShapeExitPoint( origin, endpoint, lightAbsorbingShape.shape );
                if ( exitPoint != null ) {
                    pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( exitPoint, FADE_COEFFICIENT_IN_AIR ) );
                }
            }
        }

        // Sort the list by distance from the origin.
        Collections.sort( pointAndFadeCoefficientList, new Comparator<PointAndFadeCoefficient>() {
            public int compare( PointAndFadeCoefficient p1, PointAndFadeCoefficient p2 ) {
                return Double.compare( p1.point.distance( origin ), p2.point.distance( origin ) );
            }
        } );

        // Add the segments that comprise the line.
        int opacity = 255;
        for ( int i = 0; i < pointAndFadeCoefficientList.size() - 1; i++ ) {
            final FadingLineNode fadingLineNode = new FadingLineNode( pointAndFadeCoefficientList.get( i ).point,
                                                                      pointAndFadeCoefficientList.get( i + 1 ).point,
                                                                      new Color( color.getRed(), color.getGreen(), color.getBlue(), opacity ),
                                                                      pointAndFadeCoefficientList.get( i ).fadeCoefficient,
                                                                      STROKE_THICKNESS );
            addChild( fadingLineNode );
            opacity = fadingLineNode.getOpacityAtEndpoint();
        }
    }

    private static boolean lineIntersectsShapeIntersects( Vector2D startPoint, Vector2D endPoint, Shape shape ) {
        // Warning: This only checks the bounding rect, not the full shape.
        // This was adequate in this case, but take care if reusing.
        if ( shape.getBounds2D().intersectsLine( startPoint.x, startPoint.y, endPoint.x, endPoint.y ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    private static Vector2D getShapeEntryPoint( Vector2D origin, Vector2D endpoint, Shape shape ) {
        Rectangle2D shapeRect = shape.getBounds2D();
        Vector2D entryPoint = null;
        if ( shapeRect.intersectsLine( new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ) ) ) {
            Vector2D boundsEntryPoint = getRectangleEntryPoint( origin, endpoint, shapeRect );
            if ( boundsEntryPoint == null ) {
                return null;
            }
            Vector2D boundsExitPoint = getRectangleExitPoint( origin, endpoint, shapeRect );
            Vector2D searchEndPoint = boundsExitPoint == null ? endpoint : boundsExitPoint;

            // Search linearly for edge of the shape.  BIG HAIRY NOTE - This
            // will not work in all cases.  It worked for the coarse shapes
            // and rough bounds needed for this simulation.  Don't reuse if you
            // need good general edge finding.
            double angle = endpoint.minus( origin ).getAngle();
            double incrementalDistance = boundsEntryPoint.distance( searchEndPoint ) / SEARCH_ITERATIONS;
            for ( int i = 0; i < SEARCH_ITERATIONS; i++ ) {
                Vector2D testPoint = boundsEntryPoint.plus( new Vector2D( incrementalDistance * i, 0 ).getRotatedInstance( angle ) );
                if ( shape.contains( testPoint.toPoint2D() ) ) {
                    entryPoint = testPoint;
                    break;
                }
            }
        }
        return entryPoint;
    }

    public static Vector2D getLineIntersection( Line2D line1, Line2D line2 ) {

        double denominator = ( ( line1.getP2().getX() - line1.getP1().getX() ) * ( line2.getP2().getY() - line2.getP1().getY() ) ) -
                             ( ( line1.getP2().getY() - line1.getP1().getY() ) * ( line2.getP2().getX() - line2.getP1().getX() ) );

        // Check if the lines are parallel, and thus don't intersect.
        if ( denominator == 0 ) {
            return null;
        }

        double numerator = ( ( line1.getP1().getY() - line2.getP1().getY() ) * ( line2.getP2().getX() - line2.getP1().getX() ) ) - ( ( line1.getP1().getX() - line2.getP1().getX() ) * ( line2.getP2().getY() - line2.getP1().getY() ) );
        double r = numerator / denominator;
        double numerator2 = ( ( line1.getP1().getY() - line2.getP1().getY() ) * ( line1.getP2().getX() - line1.getP1().getX() ) ) - ( ( line1.getP1().getX() - line2.getP1().getX() ) * ( line1.getP2().getY() - line1.getP1().getY() ) );
        double s = numerator2 / denominator;

        if ( ( r < 0 || r > 1 ) || ( s < 0 || s > 1 ) ) { return null; }

        // Find intersection point
        return new Vector2D( line1.getP1().getX() + ( r * ( line1.getP2().getX() - line1.getP1().getX() ) ), line1.getP1().getY() + ( r * ( line1.getP2().getY() - line1.getP1().getY() ) ) );
    }

    private static Vector2D getRectangleEntryPoint( Vector2D origin, Vector2D endpoint, Rectangle2D rect ) {

        List<Vector2D> intersectingPoints = getRectangleLineIntersectionPoints( rect, new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ) );

        // Determine which point is closest to the origin.
        Vector2D closestIntersectionPoint = null;
        for ( Vector2D intersectingPoint : intersectingPoints ) {
            if ( closestIntersectionPoint == null || closestIntersectionPoint.distance( origin ) > intersectingPoint.distance( origin ) ) {
                closestIntersectionPoint = intersectingPoint;
            }
        }

        return closestIntersectionPoint;
    }

    private static Vector2D getRectangleExitPoint( Vector2D origin, Vector2D endpoint, Rectangle2D rect ) {

        List<Vector2D> intersectingPoints = getRectangleLineIntersectionPoints( rect, new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ) );

        if ( intersectingPoints.size() < 2 ) {
            // Line either doesn't intersect or ends inside the rectangle.
            return null;
        }

        // Determine which point is furthest from the origin.
        Vector2D furthestIntersectionPoint = null;
        for ( Vector2D intersectingPoint : intersectingPoints ) {
            if ( furthestIntersectionPoint == null || furthestIntersectionPoint.distance( origin ) < intersectingPoint.distance( origin ) ) {
                furthestIntersectionPoint = intersectingPoint;
            }
        }

        return furthestIntersectionPoint;
    }

    private static List<Vector2D> getRectangleLineIntersectionPoints( Rectangle2D rect, Line2D line ) {
        List<Line2D> linesThatMakeUpRectangle = new ArrayList<Line2D>( 4 );
        linesThatMakeUpRectangle.add( new Line2D.Double( rect.getMinX(), rect.getMinY(), rect.getMinX(), rect.getMaxY() ) );
        linesThatMakeUpRectangle.add( new Line2D.Double( rect.getMinX(), rect.getMaxY(), rect.getMaxX(), rect.getMaxY() ) );
        linesThatMakeUpRectangle.add( new Line2D.Double( rect.getMaxX(), rect.getMaxY(), rect.getMaxX(), rect.getMinY() ) );
        linesThatMakeUpRectangle.add( new Line2D.Double( rect.getMaxX(), rect.getMinY(), rect.getMinX(), rect.getMinY() ) );

        List<Vector2D> intersectingPoints = new ArrayList<Vector2D>();
        for ( Line2D rectLine : linesThatMakeUpRectangle ) {
            Vector2D intersectingPoint = getLineIntersection( rectLine, line );
            if ( intersectingPoint != null ) {
                intersectingPoints.add( intersectingPoint );
            }
        }
        return intersectingPoints;
    }

    private Vector2D getShapeExitPoint( Vector2D origin, Vector2D endpoint, Shape shape ) {
        Rectangle2D shapeRect = shape.getBounds2D();
        Vector2D exitPoint = null;
        if ( shape.contains( endpoint.toPoint2D() ) ) {
            // Line ends inside shape, return null.
            return null;
        }
        if ( !shape.contains( endpoint.toPoint2D() ) && shapeRect.intersectsLine( new Line2D.Double( origin.toPoint2D(), endpoint.toPoint2D() ) ) ) {
            // Phase I - Do a binary search to locate the edge of the
            // rectangle that encloses the shape.
            double angle = endpoint.minus( origin ).getAngle();
            double length = origin.distance( endpoint );
            double lengthChange = length / 2;
            for ( int i = 0; i < SEARCH_ITERATIONS; i++ ) {
                Line2D testLine = new Line2D.Double( origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) ).toPoint2D(), endpoint.toPoint2D() );
                length += lengthChange * ( testLine.intersects( shapeRect ) ? 1 : -1 );
                lengthChange = lengthChange / 2;
            }
            exitPoint = origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) );
        }
        return exitPoint;
    }

    private static class PointAndFadeCoefficient {
        public final Vector2D point;
        public final double fadeCoefficient;

        private PointAndFadeCoefficient( Vector2D point, double fadeCoefficient ) {
            this.point = point;
            this.fadeCoefficient = fadeCoefficient;
        }
    }

    public static void main( String[] args ) {
        Line2D testLine1 = new Line2D.Double( 0, 0, 100, 100 );
        Line2D testLine2 = new Line2D.Double( 0, 100, 100, 0 );
        System.out.println( "Intersection = " + getLineIntersection( testLine1, testLine2 ) );

        Rectangle2D testRect = new Rectangle2D.Double( 1, -1, 2, 2 );
        Vector2D testLineOrigin = new Vector2D( 0, 0 );
        Vector2D testLineEndpoint = new Vector2D( 100, 5 );
        System.out.println( "Entry point with rect = " + getRectangleEntryPoint( testLineOrigin,
                                                                                 testLineEndpoint,
                                                                                 testRect ) );
        System.out.println( "Exit point with rect = " + getRectangleExitPoint( testLineOrigin,
                                                                               testLineEndpoint,
                                                                               testRect ) );

    }
}
