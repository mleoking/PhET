// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.CrossSection;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PipeCrossSectionControl extends PNode {
    private double DISTANCE_THRESHOLD = 0.5;

    public PipeCrossSectionControl( final ModelViewTransform transform, final CrossSection pipePosition ) {
        final PipeControlPoint top = new PipeControlPoint( pipePosition.getTopProperty(), true );
        final PipeControlPoint bottom = new PipeControlPoint( pipePosition.getBottomProperty(), false );

        Function1<Point2D, Point2D> bottomConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D bottomLocation ) {
                final double max = top.getPoint().getY() - DISTANCE_THRESHOLD;
                final double min = -5;//make it so that the control point can't be dragged offscreen in the down direction
                return new Point2D.Double( bottom.getPoint().getX(), MathUtil.clamp( min, bottomLocation.getY(), max ) );
            }
        };

        Function1<Point2D, Point2D> topConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D topLocation ) {
                final double min = bottom.getPoint().getY() + DISTANCE_THRESHOLD;
                final double max = 3;//make it so that the control point can't be dragged offscreen in the up direction
                final double clamp = MathUtil.clamp( min, topLocation.getY(), max );
                return new Point2D.Double( top.getPoint().getX(), clamp );
            }
        };

        addChild( new PipeBackNode.GrabHandle( transform, bottom, bottomConstraint ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, topConstraint ) );
    }
}
