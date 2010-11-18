package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.view.PoolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The back part (in z-ordering) of the pipe graphics
 *
 * @author Sam Reid
 */
public class PipeBackNode extends PNode {
    private Color waterColor = new Color( 122, 197, 213 );
    private Pipe pipe;
    private ModelViewTransform2D transform;
    public static int PIPE_LEFT_OFFSET = 72;
    static double sx = 0.4;

    public static final double pipeOpeningPixelYTop = 58;
    private static final double pipeOpeningPixelYBottom = 375;
    public static final double pipeOpeningHeight = pipeOpeningPixelYBottom - pipeOpeningPixelYTop;

    public PipeBackNode( final ModelViewTransform2D transform, final Pipe pipe, final Property<Double> fluidDensity ) {
        this.pipe = pipe;
        this.transform = transform;
        //Hide the leftmost and rightmost parts as if water is coming from a gray pipe and leaving through a gray pipe

        final BufferedImage pipeImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-middle.png" );
        final PhetPPath leftExtension = new PhetPPath( Color.blue );
        final PhetPPath rightExtension = new PhetPPath( Color.blue );

        final BufferedImage pipeLeftBackImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-left-back.png" );

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
                    rightExtension.setOffset( topRight.getX(), topRight.getY() - pipeOpeningPixelYTop * syRight );
                }
            } );
        }} );

        //extensions
        addChild( leftExtension );
        addChild( rightExtension );


        //Background so the semi-transparent water color looks correct
        addChild( new PhetPPath( Color.white ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.createTransformedShape( pipe.getShape() ) );
                }
            } );
        }} );
        addChild( new PhetPPath( waterColor ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.createTransformedShape( pipe.getShape() ) );
                }
            } );
            fluidDensity.addObserver( new SimpleObserver() {
                public void update() {
                    setPaint( PoolNode.getBottomColor( fluidDensity.getValue() ) );
                }
            } );
        }} );
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
        public GrabHandle( final ModelViewTransform2D transform, final ControlPoint controlPoint, final ControlPoint oppositeControlPoint ) {
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
                    private Point2D.Double relativeGrabPoint;

                    public void mousePressed( PInputEvent event ) {
                        updateGrabPoint( event );
                    }

                    private void updateGrabPoint( PInputEvent event ) {
                        Point2D viewStartingPoint = event.getPositionRelativeTo( getParent() );
                        Point2D viewCoordinateOfObject = transform.modelToView( controlPoint.getPoint().getX(), controlPoint.getPoint().getY() );
                        relativeGrabPoint = new Point2D.Double( viewStartingPoint.getX() - viewCoordinateOfObject.getX(), viewStartingPoint.getY() - viewCoordinateOfObject.getY() );
                    }

                    public void mouseDragged( PInputEvent event ) {
                        if ( relativeGrabPoint == null ) {
                            updateGrabPoint( event );
                        }
                        final Point2D newDragPosition = event.getPositionRelativeTo( getParent() );
                        Point2D modelLocation = transform.viewToModel( newDragPosition.getX() - relativeGrabPoint.getX(),
                                                                       newDragPosition.getY() - relativeGrabPoint.getY() );

                        //Todo: this could be factored out better
                        if ( controlPoint.isTop() ) {
                            if ( modelLocation.getY() - oppositeControlPoint.getPoint().getY() < 0.5 ) {
                                modelLocation.setLocation( modelLocation.getX(), oppositeControlPoint.getPoint().getY() + 0.5 );
                            }
                        }
                        else {
                            if ( modelLocation.getY() - oppositeControlPoint.getPoint().getY() > -0.5 ) {
                                modelLocation.setLocation( modelLocation.getX(), oppositeControlPoint.getPoint().getY() - 0.5 );
                            }
                        }
                        controlPoint.setPosition( controlPoint.getPoint().getX(), modelLocation.getY() );//not allowed to go to negative Potential Energy
                    }

                    public void mouseReleased( PInputEvent event ) {
                        relativeGrabPoint = null;
                    }
                } );
            }} );
        }
    }

    public static interface ControlPoint {
        Point2D getPoint();

        void translate( double x, double y );

        void addObserver( SimpleObserver observer );

        double distance( ControlPoint controlPoint );

        void setPosition( double x, double y );

        boolean isTop();
    }

}
