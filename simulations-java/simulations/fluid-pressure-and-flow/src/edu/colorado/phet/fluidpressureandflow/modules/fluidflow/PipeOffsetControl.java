// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.fluidpressureandflow.model.CrossSection;
import edu.umd.cs.piccolo.PNode;

/**
 * Translate a pipe cross section
 *
 * @author Sam Reid
 */
public class PipeOffsetControl extends PNode {

    public PipeOffsetControl( final ModelViewTransform transform, final CrossSection pipePosition, final double offsetX ) {
        final Property<ImmutableVector2D> point = new Property<ImmutableVector2D>( new ImmutableVector2D( pipePosition.getX() + offsetX, pipePosition.getCenterY() ) );
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
                point.setValue( new ImmutableVector2D( pipePosition.getX() + offsetX, pipePosition.getCenterY() ) );
            }
        };
        pipePosition.getTopProperty().addObserver( updateCenter );
        pipePosition.getBottomProperty().addObserver( updateCenter );
        addChild( new PipeBackNode.GrabHandle( transform, new ControlPoint(
                point, true ), new Function1.Identity<Point2D>() ) );
    }
}
