// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeControlPoint;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeCrossSection;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;

/**
 * Control for the user to deform the pipe vertically.
 *
 * @author Sam Reid
 */
public class PipeCrossSectionControl extends PNode {

    //Distance that must separate the points so the pipe cannot become too constricted (makes velocity too high)
    //Note that when the velocity becomes too high, Bernoulli's equation gives a negative pressure.
    //The pressure doesn't really go negative then, it just means Bernoulli's equation is inapplicable in that situation
    //So we have to make sure the distance threshold is high enough that Bernoulli's equation never gives a negative pressure
    public static final double DISTANCE_THRESHOLD = 1;

    //make it so that the control point can't be dragged off-screen in the down direction
    public static final double MIN_DRAG_Y = -4;

    //make it so that the control point can't be dragged off-screen in the up direction
    public static final double MAX_DRAG_Y = 0;

    public PipeCrossSectionControl( final ModelViewTransform transform, final PipeCrossSection pipePosition ) {
        final PipeControlPoint top = new PipeControlPoint( chain( pipePosition.component, UserComponents.top ), pipePosition.top );
        final PipeControlPoint bottom = new PipeControlPoint( chain( pipePosition.component, UserComponents.bottom ), pipePosition.bottom );

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

        //Add handles that allow the user to deform this cross section of the pipe
        addChild( new GrabHandle( transform, bottom, bottomConstraint, false ) );
        addChild( new GrabHandle( transform, top, topConstraint, true ) );
    }
}
