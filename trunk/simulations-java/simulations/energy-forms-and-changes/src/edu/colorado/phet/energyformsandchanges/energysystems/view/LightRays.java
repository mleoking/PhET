// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Class that represents rays of light emitting from a light source.
 *
 * @author John Blanco
 */
public class LightRays extends PNode {

    private static int SEARCH_ITERATIONS = 10;

    private static boolean SHOW_RAY_BLOCKING_SHAPES = true;

    private final List<LightRayNode> lightRayNodes = new ArrayList<LightRayNode>();
    private final ObservableList<LightAbsorbingShape> rayAbsorbingShapes = new ObservableList<LightAbsorbingShape>();

    public LightRays( final Vector2D center, final double innerRadius, final double outerRadius, final int numRays, final Color color ) {
        this( center, innerRadius, outerRadius, numRays, 0, color );
    }

    public LightRays( final Vector2D center, final double innerRadius, final double outerRadius, final int numRaysForFullCircle, final double darkConeSpanAngle, final Color color ) {

        assert numRaysForFullCircle > 0 && outerRadius > innerRadius && darkConeSpanAngle < Math.PI * 2; // Parameter checking.

        // Create and add the rays.
        for ( int i = 0; i < numRaysForFullCircle; i++ ) {
            double angle = ( Math.PI * 2 / numRaysForFullCircle ) * i;
            final Vector2D rayStartPoint = center.plus( new Vector2D( innerRadius, 0 ).getRotatedInstance( angle ) );
            Vector2D rayEndPoint = center.plus( new Vector2D( outerRadius, 0 ).getRotatedInstance( angle ) );
            if ( angle <= Math.PI / 2 - darkConeSpanAngle / 2 || angle >= Math.PI / 2 + darkConeSpanAngle / 2 ) {
                // Ray is not in the "dark cone", so add it.
                final LightRayNode lightRayNode = new LightRayNode( rayStartPoint, rayEndPoint, color );
                lightRayNodes.add( lightRayNode );
                addChild( lightRayNode );
            }
        }

        // For debug: Show the ray-blocking shapes.
        if ( SHOW_RAY_BLOCKING_SHAPES ) {
            for ( LightAbsorbingShape rayAbsorbingShape : rayAbsorbingShapes ) {
                addChild( new PhetPPath( rayAbsorbingShape.shape ) );
            }
        }


        /*
        // Function that creates the rays.
        VoidFunction1<LightAbsorbingShape> rayUpdater = new VoidFunction1<LightAbsorbingShape>() {
            public void apply( LightAbsorbingShape lightAbsorbingShape ) {
                for ( int i = 0; i < numRaysForFullCircle; i++ ) {
                    double angle = ( Math.PI * 2 / numRaysForFullCircle ) * i;
                    final Vector2D rayStartPoint = center.plus( new Vector2D( innerRadius, 0 ).getRotatedInstance( angle ) );
                    Vector2D rayEndPoint = center.plus( new Vector2D( outerRadius, 0 ).getRotatedInstance( angle ) );
                    if ( angle <= Math.PI / 2 - darkConeSpanAngle / 2 || angle >= Math.PI / 2 + darkConeSpanAngle / 2 ) {
                        // Ray is not in the "dark cone", so add it.
                        final LightRayNode lightRayNode = new LightRayNode( rayStartPoint, rayEndPoint, color );
                        lightRayNodes.add( lightRayNode );
                        addChild( lightRayNode );
                    }
//                    for ( Shape rayBlockingShape : rayBlockingShapes ) {
//                        System.out.println( "==================" );
//                        System.out.println( "rayEndPoint before blocking = " + rayEndPoint );
//                        rayEndPoint = blockRay( rayStartPoint, rayEndPoint, rayBlockingShape );
//                        System.out.println( "rayEndPoint after blocking = " + rayEndPoint );
//                    }
//                    rayLayer.addChild( new PhetPPath( new Line2D.Double( rayStartPoint.toPoint2D(), rayEndPoint.toPoint2D() ), RAY_STROKE, color ) );
                }
                if ( SHOW_RAY_BLOCKING_SHAPES ) {
                    for ( LightAbsorbingShape rayAbsorbingShape : rayAbsorbingShapes ) {
                        addChild( new PhetPPath( rayAbsorbingShape.shape ) );
                    }
                }
            }
        };
        rayUpdater.apply( null ); // Initial update.
        */

        // Update the rays whenever shapes are added or removed.
//        rayAbsorbingShapes.addElementAddedObserver( rayUpdater );
//        rayAbsorbingShapes.addElementRemovedObserver( rayUpdater );
    }

    public void addLightAbsorbingShape( LightAbsorbingShape lightAbsorbingShape ) {
        rayAbsorbingShapes.add( lightAbsorbingShape );
        for ( LightRayNode lightRayNode : lightRayNodes ) {
            lightRayNode.addLightAbsorbingShape( lightAbsorbingShape );
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
}
