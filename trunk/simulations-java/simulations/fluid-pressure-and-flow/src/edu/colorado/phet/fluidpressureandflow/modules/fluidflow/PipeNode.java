package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
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
        public GrabHandle( final ModelViewTransform2D transform, final Function1<SimpleObserver, Void> addObserver, final Function0<Point2D> getPosition, final Function1<Point2D, Void> translate ) {
            double grabHandleRadius = 10;
            addChild( new PhetPPath( new Ellipse2D.Double( -grabHandleRadius, -grabHandleRadius, grabHandleRadius * 2, grabHandleRadius * 2 ), Color.green ) {{
                addObserver.apply( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( getPosition.apply() ) );
                    }
                } );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        PDimension delta = event.getDeltaRelativeTo( getParent() );
                        translate.apply( new Point2D.Double( 0, transform.viewToModelDifferential( delta ).getY() ) );
                    }
                } );
            }} );
        }
    }

    public static class PipePositionControl extends PNode {
        public PipePositionControl( final ModelViewTransform2D transform, final PipePosition pipePosition ) {
            final Function1<SimpleObserver, Void> addObserver = new Function1<SimpleObserver, Void>() {
                public Void apply( SimpleObserver simpleObserver ) {
                    pipePosition.addObserver( simpleObserver );
                    return null;//TODO: ugly way to return void value
                }
            };
            addChild( new GrabHandle( transform, addObserver, new Function0<Point2D>() {
                public Point2D apply() {
                    return pipePosition.getTop();
                }
            }, new Function1<Point2D, Void>() {
                public Void apply( Point2D point2D ) {
                    pipePosition.translateTop( point2D.getX(), point2D.getY() );
                    return null;
                }
            } ) );
            addChild( new GrabHandle( transform, addObserver, new Function0<Point2D>() {
                public Point2D apply() {
                    return pipePosition.getBottom();
                }
            }, new Function1<Point2D, Void>() {
                public Void apply( Point2D point2D ) {
                    pipePosition.translateBottom( point2D.getX(), point2D.getY() );
                    return null;
                }
            } ) );

        }
    }
}
