// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.fluidflow.model.CrossSection;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * @author Sam Reid
 */
public class PipeCrossSectionControl extends PNode {
    private double DISTANCE_THRESHOLD = 0.5;
    public static final double MIN_DRAG_Y = -5;//make it so that the control point can't be dragged offscreen in the down direction
    public static final double MAX_DRAG_Y = 3;//make it so that the control point can't be dragged offscreen in the up direction

    public PipeCrossSectionControl( final ModelViewTransform transform, final CrossSection pipePosition ) {
        final PipeControlPoint top = new PipeControlPoint( pipePosition.top );
        final PipeControlPoint bottom = new PipeControlPoint( pipePosition.bottom );

        Function1<Point2D, Point2D> bottomConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D bottomLocation ) {
                return new Point2D.Double( bottom.getPoint().getX(),
                                           clamp( MIN_DRAG_Y, bottomLocation.getY(), top.getPoint().getY() - DISTANCE_THRESHOLD ) );
            }
        };

        Function1<Point2D, Point2D> topConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D topLocation ) {
                return new Point2D.Double( top.getPoint().getX(),
                                           clamp( bottom.getPoint().getY() + DISTANCE_THRESHOLD, topLocation.getY(), MAX_DRAG_Y ) );
            }
        };

        addChild( new PipeBackNode.GrabHandle( transform, bottom, bottomConstraint ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, topConstraint ) );
    }
}
