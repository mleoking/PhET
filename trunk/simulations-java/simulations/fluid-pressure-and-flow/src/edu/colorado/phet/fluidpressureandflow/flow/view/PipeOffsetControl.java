// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeControlPoint;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeCrossSection;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.flow.view.PipeCrossSectionControl.*;

/**
 * Translate a pipe cross section up and down, used on the leftmost and rightmost parts of the pipe to change the height.
 *
 * @author Sam Reid
 */
public class PipeOffsetControl extends PNode {
    public PipeOffsetControl( final IUserComponent component, final ModelViewTransform transform, final PipeCrossSection pipePosition, final double offsetX ) {
        final double x = pipePosition.getX() + offsetX;
        final Property<ImmutableVector2D> point = new Property<ImmutableVector2D>( new ImmutableVector2D( x, pipePosition.getCenterY() ) );
        point.addObserver( new SimpleObserver() {
            public void update() {
                double pipeCenter = pipePosition.getCenterY();
                double nodeCenter = point.get().getY();
                double delta = nodeCenter - pipeCenter;
                pipePosition.translate( 0, delta );
            }
        } );
        final SimpleObserver updateCenter = new SimpleObserver() {
            public void update() {
                point.set( new ImmutableVector2D( x, pipePosition.getCenterY() ) );
            }
        };
        pipePosition.top.addObserver( updateCenter );
        pipePosition.bottom.addObserver( updateCenter );

        //Add the handle that the user can use to drag the pipe up and down
        addChild( new GrabHandle( transform, new PipeControlPoint( component, point ),
                                  new Function1<Point2D, Point2D>() {
                                      public Point2D apply( Point2D proposedDragPoint ) {
                                          return new Point2D.Double( x, MathUtil.clamp( MIN_DRAG_Y + DISTANCE_THRESHOLD, proposedDragPoint.getY(), MAX_DRAG_Y - DISTANCE_THRESHOLD ) );
                                      }
                                  }, true ) );
    }
}
