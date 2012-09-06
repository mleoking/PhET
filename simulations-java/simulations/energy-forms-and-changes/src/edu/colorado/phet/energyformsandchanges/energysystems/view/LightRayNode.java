// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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
    private static final double STROKE_THICKNESS = 3;
    private static final int SEARCH_ITERATIONS = 10;

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
        pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( origin, 0 ) );
        pointAndFadeCoefficientList.add( new PointAndFadeCoefficient( endpoint, 0 ) );
        for ( int i = 0; i < pointAndFadeCoefficientList.size() - 1; i++ ) {
            addChild( new FadingLineNode( pointAndFadeCoefficientList.get( i ).point,
                                          pointAndFadeCoefficientList.get( i + 1 ).point,
                                          color,
//                                          pointAndFadeCoefficientList.get( i ).fadeCoefficient,
                                          0.5,
                                          STROKE_THICKNESS ) );
        }
    }

    private static Vector2D blockRay( Vector2D origin, Vector2D endPoint, Shape shape ) {
        Rectangle2D shapeRect = shape.getBounds2D();
        System.out.println( "shapeRect = " + shapeRect );
        Vector2D adjustedEndPoint = endPoint;
        if ( shapeRect.intersectsLine( new Line2D.Double( origin.toPoint2D(), endPoint.toPoint2D() ) ) ) {
            // Phase I - Do a binary search to locate the edge of the
            // rectangle that encloses the shape.
            double angle = endPoint.minus( origin ).getAngle();
            double length = origin.distance( endPoint );
            double lengthChange = length / 2;
            for ( int i = 0; i < SEARCH_ITERATIONS; i++ ) {
                Line2D testLine = new Line2D.Double( origin.toPoint2D(), origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) ).toPoint2D() );
                length += lengthChange * ( testLine.intersects( shapeRect ) ? -1 : 1 );
                lengthChange = lengthChange / 2;
            }
            adjustedEndPoint = origin.plus( new Vector2D( length, 0 ).getRotatedInstance( angle ) );
        }
        return adjustedEndPoint;
    }

    private boolean shapeIntersects( Shape shape ) {
        if ( shape.getBounds2D().intersectsLine( origin.x, origin.y, endpoint.x, endpoint.y ) ) {
            // TODO: This only checks the bounding rect, not the full shape.
            return true;
        }
        else {
            return false;
        }
    }

    private static Vector2D getShapeEntryPoint( Vector2D origin, Vector2D endPoint, Shape shape ) {
        Rectangle2D shapeRect = shape.getBounds2D();
        System.out.println( "shapeRect = " + shapeRect );
        Vector2D entryPoint = null;
        if ( shapeRect.intersectsLine( new Line2D.Double( origin.toPoint2D(), endPoint.toPoint2D() ) ) ) {
            // Phase I - Do a binary search to locate the edge of the
            // rectangle that encloses the shape.
            double angle = endPoint.minus( origin ).getAngle();
            double length = origin.distance( endPoint );
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

    private static class PointAndFadeCoefficient {
        public final Vector2D point;
        public final double fadeCoefficient;

        private PointAndFadeCoefficient( Vector2D point, double fadeCoefficient ) {
            this.point = point;
            this.fadeCoefficient = fadeCoefficient;
        }
    }
}
