// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents rays of light in the view.
 *
 * @author John Blanco
 */
public class LightRays extends PNode {

    private static Stroke RAY_STROKE = new BasicStroke( 2 );
    private static int SEARCH_ITERATIONS = 10;

    private static boolean SHOW_RAY_BLOCKING_SHAPES = true;

    private final ObservableList<Shape> rayBlockingShapes = new ObservableList<Shape>();
    private final PNode rayLayer = new PNode();

    public LightRays( final Vector2D center, final double innerRadius, final double outerRadius, final int numRays, final Color color ) {

        assert numRays > 0 && outerRadius > innerRadius; // Parameter checking.
        addChild( rayLayer );

        // Function that creates the rays, accounting for any blocking shapes.
        VoidFunction1<Shape> rayUpdater = new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                rayLayer.removeAllChildren();
                for ( int i = 0; i < numRays; i++ ) {
                    double angle = ( Math.PI * 2 / numRays ) * i;
                    final Vector2D rayStartPoint = center.plus( new Vector2D( innerRadius, 0 ).getRotatedInstance( angle ) );
                    Vector2D rayEndPoint = center.plus( new Vector2D( outerRadius, 0 ).getRotatedInstance( angle ) );
                    for ( Shape rayBlockingShape : rayBlockingShapes ) {
                        System.out.println( "==================" );
                        System.out.println( "rayEndPoint before blocking = " + rayEndPoint );
                        rayEndPoint = blockRay( rayStartPoint, rayEndPoint, rayBlockingShape );
                        System.out.println( "rayEndPoint after blocking = " + rayEndPoint );
                    }
                    rayLayer.addChild( new PhetPPath( new Line2D.Double( rayStartPoint.toPoint2D(), rayEndPoint.toPoint2D() ), RAY_STROKE, color ) );
                }
                if ( SHOW_RAY_BLOCKING_SHAPES ) {
                    for ( Shape rayBlockingShape : rayBlockingShapes ) {
                        rayLayer.addChild( new PhetPPath( rayBlockingShape ) );
                    }
                }
            }
        };
        rayUpdater.apply( null ); // Initial update.

        // Update the rays whenever shapes are added or removed.
        rayBlockingShapes.addElementAddedObserver( rayUpdater );
        rayBlockingShapes.addElementRemovedObserver( rayUpdater );
    }

    public void addRayBlockingShape( Shape rayBlockingShape ) {
        rayBlockingShapes.add( rayBlockingShape );
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
}
