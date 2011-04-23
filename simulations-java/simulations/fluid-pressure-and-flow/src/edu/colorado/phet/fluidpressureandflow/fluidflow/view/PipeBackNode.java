// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.fluidflow.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowApplication;
import edu.colorado.phet.fluidpressureandflow.common.view.PoolNode;
import edu.colorado.phet.fluidpressureandflow.fluidflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The back part (in z-ordering) of the pipe graphics
 *
 * @author Sam Reid
 */
public class PipeBackNode extends PNode {
    private Color waterColor = new Color( 122, 197, 213 );
    private Pipe pipe;
    private ModelViewTransform transform;
    public static int PIPE_LEFT_OFFSET = 72;
    static double sx = 0.4;

    public static final double pipeOpeningPixelYTop = 58;
    private static final double pipeOpeningPixelYBottom = 375;
    public static final double pipeOpeningHeight = pipeOpeningPixelYBottom - pipeOpeningPixelYTop;

    public PipeBackNode( final ModelViewTransform transform, final Pipe pipe, final Property<Double> fluidDensity ) {
        this.pipe = pipe;
        this.transform = transform;
        //Hide the leftmost and rightmost parts as if water is coming from a gray pipe and leaving through a gray pipe

        final BufferedImage pipeImage = FluidPressureAndFlowApplication.RESOURCES.getImage( "pipe-middle.png" );
        final PhetPPath leftExtension = new PhetPPath( Color.blue );
        final PhetPPath rightExtension = new PhetPPath( Color.blue );

        final BufferedImage pipeLeftBackImage = FluidPressureAndFlowApplication.RESOURCES.getImage( "pipe-left-back.png" );

        addChild( new PImage( pipeLeftBackImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double syLeft = getPipeLeftViewHeight() / pipeOpeningHeight;
                    double syRight = getPipeRightViewHeight() / pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( sx, syLeft ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - pipeLeftBackImage.getWidth() + PIPE_LEFT_OFFSET / sx, topLeft.getY() - pipeOpeningPixelYTop * syLeft );

                    double length = 10000;
                    leftExtension.setPathTo( new Rectangle2D.Double( topLeft.getX() - length, 0, length, pipeLeftBackImage.getHeight() ) );
                    leftExtension.setPaint( new TexturePaint( pipeImage, new Rectangle2D.Double( 0, 0, pipeImage.getWidth(), pipeLeftBackImage.getHeight() ) ) );
                    leftExtension.setTransform( AffineTransform.getScaleInstance( 1, syLeft ) );
                    leftExtension.setOffset( 0, topLeft.getY() - pipeOpeningPixelYTop * syLeft );

                    final Point2D topRight = transform.modelToView( pipe.getTopRight() );
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
                    setPathTo( transform.modelToView( pipe.getShape() ) );
                }
            } );
        }} );
        addChild( new PhetPPath( waterColor ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( pipe.getShape() ) );
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
        final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
        final Point2D bottomLeft = transform.modelToView( pipe.getBottomLeft() );
        return bottomLeft.getY() - topLeft.getY();
    }

    public double getPipeRightViewHeight() {
        final Point2D topRight = transform.modelToView( pipe.getTopRight() );
        final Point2D bottomRight = transform.modelToView( pipe.getBottomRight() );
        return bottomRight.getY() - topRight.getY();
    }

    public static class GrabHandle extends PNode {
        public GrabHandle( final ModelViewTransform transform, final PipeControlPoint controlPoint, final Function1<Point2D, Point2D> constraint ) {
            double arrowLength = 20;
            addChild( new DoubleArrowNode( new Point2D.Double( 0, -arrowLength ), new Point2D.Double( 0, arrowLength ), 16, 16, 8 ) {{
                setPaint( Color.green );
                setStroke( new BasicStroke( 1 ) );
                setStrokePaint( Color.black );
                controlPoint.point.addObserver( new SimpleObserver() {
                    public void update() {
                        setOffset( transform.modelToView( controlPoint.point.getValue().toPoint2D() ) );
                    }
                } );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new RelativeDragHandler( this, transform, controlPoint.point, constraint ) );
            }} );
        }
    }
}