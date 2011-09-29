// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.Color;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.flow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.pressure.view.PoolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_LEFT_BACK;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_MIDDLE;

/**
 * The back part (in z-ordering) of the pipe graphics
 *
 * @author Sam Reid
 */
public class PipeBackNode extends PNode {
    private final Color waterColor = new Color( 122, 197, 213 );
    private final Pipe pipe;
    private final ModelViewTransform transform;
    public static final int PIPE_LEFT_OFFSET = 72;
    static final double sx = 0.4;

    public static final double pipeOpeningPixelYTop = 58;
    private static final double pipeOpeningPixelYBottom = 375;
    public static final double pipeOpeningHeight = pipeOpeningPixelYBottom - pipeOpeningPixelYTop;

    public PipeBackNode( final ModelViewTransform transform, final Pipe pipe, final Property<Double> fluidDensity ) {
        this.pipe = pipe;
        this.transform = transform;
        //Hide the leftmost and rightmost parts as if water is coming from a gray pipe and leaving through a gray pipe

        final BufferedImage pipeImage = PIPE_MIDDLE;
        final PhetPPath leftExtension = new PhetPPath( Color.blue );
        final PhetPPath rightExtension = new PhetPPath( Color.blue );

        final BufferedImage pipeLeftBackImage = PIPE_LEFT_BACK;

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
                    setPaint( PoolNode.getBottomColor( fluidDensity.get() ) );
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

}