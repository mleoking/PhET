// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

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
        final ControlPoint top = new ControlPoint( pipePosition.getTopProperty(), true );
        final ControlPoint bottom = new ControlPoint( pipePosition.getBottomProperty(), false );

        Function1<Point2D, Point2D> bottomConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D bottomLocation ) {
                if ( bottomLocation.getY() > top.getPoint().getY() - DISTANCE_THRESHOLD ) {
                    bottomLocation.setLocation( bottomLocation.getX(), top.getPoint().getY() - DISTANCE_THRESHOLD );
                }
                return new Point2D.Double( bottom.getPoint().getX(), bottomLocation.getY() );
            }
        };

        Function1<Point2D, Point2D> topConstraint = new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D topLocation ) {
                if ( topLocation.getY() < bottom.getPoint().getY() + DISTANCE_THRESHOLD ) {
                    topLocation.setLocation( topLocation.getX(), bottom.getPoint().getY() + DISTANCE_THRESHOLD );
                }
                return new Point2D.Double( top.getPoint().getX(), topLocation.getY() );
            }
        };

        addChild( new PipeBackNode.GrabHandle( transform, bottom, bottomConstraint ) );
        addChild( new PipeBackNode.GrabHandle( transform, top, topConstraint ) );
    }
}
