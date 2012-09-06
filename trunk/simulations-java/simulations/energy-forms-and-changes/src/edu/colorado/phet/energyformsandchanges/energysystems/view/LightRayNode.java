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

        // TODO: Could I use a map of points to fade coefficents, then sort the map?

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
        if ( shape.getBounds2D().intersectsLine( startPoint.x, startPoint.y, endPoint.x, endPoint.y ) ) {
            // TODO: This only checks the bounding rect, not the full shape.
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
            // Phase I - Do a binary search to locate the edge of the
            // rectangle that encloses the shape.
            double angle = endpoint.minus( origin ).getAngle();
            double length = origin.distance( endpoint );
            double lengthChange = length / 2;
            for ( int i = 0; i < SEARCH_ITERATIONS; i++ ) {
                Line2D testLine = new Line2D.Double( origin.toPoint2D(), origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) ).toPoint2D() );
                length += lengthChange * ( testLine.intersects( shapeRect ) ? -1 : 1 );
                lengthChange = lengthChange / 2;
            }
            entryPoint = origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) );
        }
        return entryPoint;
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
}
