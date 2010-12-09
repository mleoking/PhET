package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.model.CrossSection;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The front part (in z-ordering) of the pipe graphics, so it looks like particles go inside the pipe.
 *
 * @author Sam Reid
 */
public class PipeFrontNode extends PNode {
    private final ModelViewTransform transform;
    private final Pipe pipe;

    public PipeFrontNode( final ModelViewTransform transform, final Pipe pipe ) {
        this.transform = transform;
        this.pipe = pipe;

        final int edgeOffset = 4;
        BasicStroke edgeStroke = new BasicStroke( 8 );
        addChild( new PhetPPath( edgeStroke, new Color( 165, 91, 0 ) ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( AffineTransform.getTranslateInstance( 0, -edgeOffset ).createTransformedShape( transform.modelToView( pipe.getTopPath() ) ) );
                }
            } );
        }} );

        addChild( new PhetPPath( edgeStroke, new Color( 0, 51, 91 ) ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( AffineTransform.getTranslateInstance( 0, +edgeOffset ).createTransformedShape( transform.modelToView( pipe.getBottomPath() ) ) );
                }
            } );
        }} );

        final BufferedImage pipeLeftFrontImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-left-front.png" );
        addChild( new PImage( pipeLeftFrontImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeLeftViewHeight() / PipeBackNode.pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( PipeBackNode.sx, sy ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - pipeLeftFrontImage.getWidth() + PipeBackNode.PIPE_LEFT_OFFSET / PipeBackNode.sx, topLeft.getY() - PipeBackNode.pipeOpeningPixelYTop * sy );
                }
            } );
        }} );

        final BufferedImage pipeRightImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-right.png" );
        addChild( new PImage( pipeRightImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeRightViewHeight() / PipeBackNode.pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( PipeBackNode.sx, sy ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopRight() );
                    setOffset( topLeft.getX() - pipeRightImage.getWidth() + PipeBackNode.PIPE_LEFT_OFFSET / PipeBackNode.sx, topLeft.getY() - PipeBackNode.pipeOpeningPixelYTop * sy );
                }
            } );
        }} );

        for ( CrossSection pipePosition : pipe.getControlPoints() ) {
            addChild( new PipePositionControl( transform, pipePosition ) );
        }
    }

    public double getPipeRightViewHeight() {
        final Point2D topRight = transform.modelToView( pipe.getTopRight() );
        final Point2D bottomRight = transform.modelToView( pipe.getBottomRight() );
        return bottomRight.getY() - topRight.getY();
    }

    public double getPipeLeftViewHeight() {
        final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
        final Point2D bottomLeft = transform.modelToView( pipe.getBottomLeft() );
        return bottomLeft.getY() - topLeft.getY();
    }
}
