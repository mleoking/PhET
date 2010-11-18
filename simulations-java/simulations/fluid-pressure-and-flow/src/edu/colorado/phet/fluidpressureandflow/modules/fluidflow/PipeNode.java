package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.model.PipePosition;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PipeNode extends PNode {
    private Color waterColor = new Color( 122, 197, 213 );
    private Pipe pipe;
    private ModelViewTransform2D transform;
    private int PIPE_LEFT_OFFSET = 72;
    double sx = 0.4;

    private static final double pipeOpeningPixelYTop = 58;
    private static final double pipeOpeningPixelYBottom = 375;
    private static final double pipeOpeningHeight = pipeOpeningPixelYBottom - pipeOpeningPixelYTop;

    public PipeNode( final ModelViewTransform2D transform, final Pipe pipe ) {
        this.pipe = pipe;
        this.transform = transform;
        //Hide the leftmost and rightmost parts as if water is coming from a gray pipe and leaving through a gray pipe

        final BufferedImage pipeImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-middle.png" );
        final PhetPPath leftExtension = new PhetPPath( Color.blue );
        final PhetPPath rightExtension = new PhetPPath( Color.blue );

        final BufferedImage pipeLeftBackImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-left-back.png" );
        final BufferedImage pipeLeftFrontImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-left-front.png" );
        final BufferedImage pipeRightImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-right.png" );
        addChild( new PImage( pipeLeftBackImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double syLeft = getPipeLeftViewHeight() / pipeOpeningHeight;
                    double syRight = getPipeRightViewHeight() / pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( sx, syLeft ) );
                    final Point2D topLeft = transform.modelToViewDouble( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - pipeLeftBackImage.getWidth() + PIPE_LEFT_OFFSET / sx, topLeft.getY() - pipeOpeningPixelYTop * syLeft );

                    double length = 10000;
                    leftExtension.setPathTo( new Rectangle2D.Double( topLeft.getX() - length, 0, length, pipeLeftBackImage.getHeight() ) );
                    leftExtension.setPaint( new TexturePaint( pipeImage, new Rectangle2D.Double( 0, 0, pipeImage.getWidth(), pipeLeftBackImage.getHeight() ) ) );
                    leftExtension.setTransform( AffineTransform.getScaleInstance( 1, syLeft ) );
                    leftExtension.setOffset( 0, topLeft.getY() - pipeOpeningPixelYTop * syLeft );

                    final Point2D topRight = transform.modelToViewDouble( pipe.getTopRight() );
                    rightExtension.setPathTo( new Rectangle2D.Double( 0, 0, length, pipeLeftBackImage.getHeight() ) );
                    rightExtension.setPaint( new TexturePaint( pipeImage, new Rectangle2D.Double( 0, 0, pipeImage.getWidth(), pipeLeftBackImage.getHeight() ) ) );
                    rightExtension.setTransform( AffineTransform.getScaleInstance( 1, syRight ) );
                    rightExtension.setOffset( topRight.getX(), topRight.getY() - pipeOpeningPixelYTop * syRight);
                }
            } );
        }} );

        //extensions
        addChild( leftExtension );
        addChild( rightExtension );

        addChild( new PhetPPath( waterColor, new BasicStroke( 1 ), Color.black ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.createTransformedShape( pipe.getShape() ) );
                }
            } );
        }} );

        addChild( new PImage( pipeLeftFrontImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeLeftViewHeight() / pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( sx, sy ) );
                    final Point2D topLeft = transform.modelToViewDouble( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - pipeLeftBackImage.getWidth() + PIPE_LEFT_OFFSET / sx, topLeft.getY() - pipeOpeningPixelYTop * sy );
                }
            } );
        }} );

        addChild( new PImage( pipeRightImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeRightViewHeight() / pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( sx, sy ) );
                    final Point2D topLeft = transform.modelToViewDouble( pipe.getTopRight() );
                    setOffset( topLeft.getX() - pipeLeftBackImage.getWidth() + PIPE_LEFT_OFFSET / sx, topLeft.getY() - pipeOpeningPixelYTop * sy );
                }
            } );
        }} );

        for ( PipePosition pipePosition : pipe.getPipePositions() ) {
            addChild( new PipePositionControl( transform, pipePosition ) );
        }
    }

    public double getPipeLeftViewHeight() {
        final Point2D topLeft = transform.modelToViewDouble( pipe.getTopLeft() );
        final Point2D bottomLeft = transform.modelToViewDouble( pipe.getBottomLeft() );
        return bottomLeft.getY() - topLeft.getY();
    }

    public double getPipeRightViewHeight() {
        final Point2D topRight = transform.modelToViewDouble( pipe.getTopRight() );
        final Point2D bottomRight = transform.modelToViewDouble( pipe.getBottomRight() );
        return bottomRight.getY() - topRight.getY();
    }

    public static class GrabHandle extends PNode {
        private ControlPoint controlPoint;
        private ControlPoint oppositeControlPoint;

        public GrabHandle( final ModelViewTransform2D transform, final ControlPoint controlPoint, final ControlPoint oppositeControlPoint ) {
            this.controlPoint = controlPoint;
            this.oppositeControlPoint = oppositeControlPoint;
//            double grabHandleRadius = 10;
//            addChild( new PhetPPath( new Ellipse2D.Double( -grabHandleRadius, -grabHandleRadius, grabHandleRadius * 2, grabHandleRadius * 2 ), Color.green ) {{
            double arrowLength = 20;
            addChild( new DoubleArrowNode( new Point2D.Double( 0, -arrowLength ), new Point2D.Double( 0, arrowLength ), 16, 16, 8 ) {{
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
                        final double dy = transform.viewToModelDifferential( delta ).getY();
                        if ( controlPoint.distance( oppositeControlPoint ) > 0.5 || movingAway( dy ) ) {
                            controlPoint.translate( 0, dy );
                        }
                    }
                } );
            }} );
        }

        private boolean movingAway( double dy ) {
            return MathUtil.getSign( controlPoint.getPoint().getY() - oppositeControlPoint.getPoint().getY() ) == MathUtil.getSign( dy );
        }
    }

    public static interface ControlPoint {
        Point2D getPoint();

        void translate( double x, double y );

        void addObserver( SimpleObserver observer );

        double distance( ControlPoint controlPoint );
    }

    public static class PipePositionControl extends PNode {
        public PipePositionControl( final ModelViewTransform2D transform, final PipePosition pipePosition ) {
            final ControlPoint topControlPoint = new ControlPoint() {
                public Point2D getPoint() {
                    return pipePosition.getTop();
                }

                public void translate( double x, double y ) {
                    pipePosition.translateTop( x, y );
                }

                public void addObserver( SimpleObserver observer ) {
                    pipePosition.addObserver( observer );
                }

                public double distance( ControlPoint controlPoint ) {
                    return getPoint().distance( controlPoint.getPoint() );
                }
            };
            final ControlPoint bottomControlPoint = new ControlPoint() {
                public Point2D getPoint() {
                    return pipePosition.getBottom();
                }

                public void translate( double x, double y ) {
                    pipePosition.translateBottom( x, y );
                }

                public void addObserver( SimpleObserver observer ) {
                    pipePosition.addObserver( observer );
                }

                public double distance( ControlPoint controlPoint ) {
                    return getPoint().distance( controlPoint.getPoint() );
                }
            };
            addChild( new GrabHandle( transform, bottomControlPoint, topControlPoint ) );
            addChild( new GrabHandle( transform, topControlPoint, bottomControlPoint ) );
        }
    }
}
