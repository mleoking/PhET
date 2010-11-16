package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PipeNode extends PNode {
    public PipeNode( final ModelViewTransform2D transform, final Pipe pipe ) {
        addChild( new PhetPPath( Color.blue, new BasicStroke( 1 ), Color.black ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.createTransformedShape( pipe.getShape() ) );
                }
            } );
        }} );
        for ( PipePosition pipePosition : pipe.getPipePositions() ) {
            addChild( new PipePositionControl( transform, pipePosition ) );
        }
    }

    public static class GrabHandle extends PNode {
        public GrabHandle( final ModelViewTransform2D transform, final ControlPoint controlPoint ) {
//            double grabHandleRadius = 10;
//            addChild( new PhetPPath( new Ellipse2D.Double( -grabHandleRadius, -grabHandleRadius, grabHandleRadius * 2, grabHandleRadius * 2 ), Color.green ) {{
            double arrowLength = 20;
            addChild( new DoubleArrowNode( new Point2D.Double( 0, -arrowLength ), new Point2D.Double( 0, arrowLength ), 16, 16, 8) {{
                setPaint( Color.green );
                setStroke( new BasicStroke( 1 ) );
                setStrokePaint( Color.black );
                controlPoint.addObserver( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( controlPoint.getPoint() ) );
                    }
                } );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        PDimension delta = event.getDeltaRelativeTo( getParent() );
                        controlPoint.translate( 0, transform.viewToModelDifferential( delta ).getY() );
                    }
                } );
            }} );
        }
    }

    public static interface ControlPoint {
        Point2D getPoint();

        void translate( double x, double y );

        void addObserver( SimpleObserver observer );
    }

    public static class PipePositionControl extends PNode {
        public PipePositionControl( final ModelViewTransform2D transform, final PipePosition pipePosition ) {
            addChild( new GrabHandle( transform, new ControlPoint() {
                public Point2D getPoint() {
                    return pipePosition.getTop();
                }

                public void translate( double x, double y ) {
                    pipePosition.translateTop( x, y );
                }

                public void addObserver( SimpleObserver observer ) {
                    pipePosition.addObserver( observer );
                }
            } ) );
            addChild( new GrabHandle( transform, new ControlPoint() {
                public Point2D getPoint() {
                    return pipePosition.getBottom();
                }

                public void translate( double x, double y ) {
                    pipePosition.translateBottom( x, y );
                }

                public void addObserver( SimpleObserver observer ) {
                    pipePosition.addObserver( observer );
                }
            } ) );
        }
    }
}
