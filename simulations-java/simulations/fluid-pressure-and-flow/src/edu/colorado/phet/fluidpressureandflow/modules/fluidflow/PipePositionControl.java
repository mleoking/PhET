package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class PipePositionControl extends PNode {
    public PipePositionControl( final ModelViewTransform2D transform, final PipePosition pipePosition ) {
        final PipeBackNode.ControlPoint topControlPoint = new PipeBackNode.ControlPoint() {
            public Point2D getPoint() {
                return pipePosition.getTop();
            }

            public void translate( double x, double y ) {
                pipePosition.translateTop( x, y );
            }

            public void addObserver( SimpleObserver observer ) {
                pipePosition.addObserver( observer );
            }

            public double distance( PipeBackNode.ControlPoint controlPoint ) {
                return getPoint().distance( controlPoint.getPoint() );
            }

            public void setPosition( double x, double y ) {
                pipePosition.setTop( x, y );
            }

            public boolean isTop() {
                return true;
            }
        };
        final PipeBackNode.ControlPoint bottomControlPoint = new PipeBackNode.ControlPoint() {
            public Point2D getPoint() {
                return pipePosition.getBottom();
            }

            public void translate( double x, double y ) {
                pipePosition.translateBottom( x, y );
            }

            public void addObserver( SimpleObserver observer ) {
                pipePosition.addObserver( observer );
            }

            public double distance( PipeBackNode.ControlPoint controlPoint ) {
                return getPoint().distance( controlPoint.getPoint() );
            }

            public void setPosition( double x, double y ) {
                pipePosition.setBottom(x,y);
            }

            public boolean isTop() {
                return false;
            }
        };
        addChild( new PipeBackNode.GrabHandle( transform, bottomControlPoint, topControlPoint ) );
        addChild( new PipeBackNode.GrabHandle( transform, topControlPoint, bottomControlPoint ) );
    }
}
