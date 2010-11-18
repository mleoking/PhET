package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources;
import edu.colorado.phet.fluidpressureandflow.model.Pipe;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The front part (in z-ordering) of the pipe graphics, so it looks like particles go inside the pipe.
 *
 * @author Sam Reid
 */
public class PipeFrontNode extends PNode {
    private final ModelViewTransform2D transform;
    private final Pipe pipe;

    public PipeFrontNode( final ModelViewTransform2D transform, final Pipe pipe ) {
        this.transform = transform;
        this.pipe = pipe;
        final BufferedImage pipeRightImage = FluidPressureAndFlowResources.RESOURCES.getImage( "pipe-right.png" );
        addChild( new PImage( pipeRightImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeRightViewHeight() / PipeBackNode.pipeOpeningHeight;
                    setTransform( AffineTransform.getScaleInstance( PipeBackNode.sx, sy ) );
                    final Point2D topLeft = transform.modelToViewDouble( pipe.getTopRight() );
                    setOffset( topLeft.getX() - pipeRightImage.getWidth() + PipeBackNode.PIPE_LEFT_OFFSET / PipeBackNode.sx, topLeft.getY() - PipeBackNode.pipeOpeningPixelYTop * sy );
                }
            } );
        }} );
    }

    public double getPipeRightViewHeight() {
        final Point2D topRight = transform.modelToViewDouble( pipe.getTopRight() );
        final Point2D bottomRight = transform.modelToViewDouble( pipe.getBottomRight() );
        return bottomRight.getY() - topRight.getY();
    }
}
