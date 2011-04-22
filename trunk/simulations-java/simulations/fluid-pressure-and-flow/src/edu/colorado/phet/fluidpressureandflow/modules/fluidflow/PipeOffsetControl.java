// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.fluidpressureandflow.modules.fluidflow.PipeCrossSectionControl.MAX_DRAG_Y;
import static edu.colorado.phet.fluidpressureandflow.modules.fluidflow.PipeCrossSectionControl.MIN_DRAG_Y;

/**
 * Translate a pipe cross section
 *
 * @author Sam Reid
 */
public class PipeOffsetControl extends PNode {

    public PipeOffsetControl( final ModelViewTransform transform, final CrossSection pipePosition, final double offsetX ) {
        final double x = pipePosition.getX() + offsetX;
        final Property<ImmutableVector2D> point = new Property<ImmutableVector2D>( new ImmutableVector2D( x, pipePosition.getCenterY() ) );
        point.addObserver( new SimpleObserver() {
            public void update() {
                double pipeCenter = pipePosition.getCenterY();
                double nodeCenter = point.getValue().getY();
                double delta = nodeCenter - pipeCenter;
                pipePosition.translateTop( 0, delta );
                pipePosition.translateBottom( 0, delta );
            }
        } );
        final SimpleObserver updateCenter = new SimpleObserver() {
            public void update() {
                point.setValue( new ImmutableVector2D( x, pipePosition.getCenterY() ) );
            }
        };
        pipePosition.top.addObserver( updateCenter );
        pipePosition.bottom.addObserver( updateCenter );
        addChild( new PipeBackNode.GrabHandle( transform, new PipeControlPoint(
                point ), new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D proposedDragPoint ) {
                return new Point2D.Double( x, MathUtil.clamp( MIN_DRAG_Y, proposedDragPoint.getY(), MAX_DRAG_Y ) );
            }
        } ) );
    }
}
