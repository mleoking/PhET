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
import edu.colorado.phet.fluidpressureandflow.pressure.view.WaterColor;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_LEFT_BACK;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_MIDDLE;
import static java.awt.Color.blue;

/**
 * The back part (in z-ordering) of the pipe graphics
 *
 * @author Sam Reid
 */
public class PipeBackNode extends PNode {

    //Color to use for the water
    private static final Color waterColor = new Color( 122, 197, 213 );

    //Pipe to represent
    private final Pipe pipe;

    //The transform
    private final ModelViewTransform transform;

    //Layout parameters
    public static final int PIPE_LEFT_OFFSET = 72;
    public static final double SX = 0.4;
    public static final double PIPE_OPENING_PIXEL_Y_TOP = 58;
    private static final double PIPE_OPENING_PIXEL_Y_BOTTOM = 375;
    public static final double PIPE_OPENING_HEIGHT = PIPE_OPENING_PIXEL_Y_BOTTOM - PIPE_OPENING_PIXEL_Y_TOP;

    public PipeBackNode( final ModelViewTransform transform, final Pipe pipe, final Property<Double> fluidDensity ) {
        this.pipe = pipe;
        this.transform = transform;

        //Hide the leftmost and rightmost parts as if water is coming from a gray pipe and leaving through a gray pipe
        final BufferedImage pipeImage = PIPE_MIDDLE;
        final PhetPPath leftExtension = new PhetPPath( blue );
        final PhetPPath rightExtension = new PhetPPath( blue );

        final BufferedImage pipeLeftBackImage = PIPE_LEFT_BACK;

        //Synchronize with the pipe shape
        addChild( new PImage( pipeLeftBackImage ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {

                    //Update the transform and offset
                    double syLeft = getPipeLeftViewHeight() / PIPE_OPENING_HEIGHT;
                    double syRight = getPipeRightViewHeight() / PIPE_OPENING_HEIGHT;
                    setTransform( AffineTransform.getScaleInstance( SX, syLeft ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - pipeLeftBackImage.getWidth() + PIPE_LEFT_OFFSET / SX, topLeft.getY() - PIPE_OPENING_PIXEL_Y_TOP * syLeft );

                    //Fill in the left part that joins the exterior to interior pipe
                    double length = 10000;
                    leftExtension.setPathTo( new Rectangle2D.Double( topLeft.getX() - length, 0, length, pipeLeftBackImage.getHeight() ) );
                    leftExtension.setPaint( new TexturePaint( pipeImage, new Rectangle2D.Double( 0, 0, pipeImage.getWidth(), pipeLeftBackImage.getHeight() ) ) );
                    leftExtension.setTransform( AffineTransform.getScaleInstance( 1, syLeft ) );
                    leftExtension.setOffset( 0, topLeft.getY() - PIPE_OPENING_PIXEL_Y_TOP * syLeft );

                    //Fill in the right part that joins the exterior to interior pipe
                    final Point2D topRight = transform.modelToView( pipe.getTopRight() );
                    rightExtension.setPathTo( new Rectangle2D.Double( 0, 0, length, pipeLeftBackImage.getHeight() ) );
                    rightExtension.setPaint( new TexturePaint( pipeImage, new Rectangle2D.Double( 0, 0, pipeImage.getWidth(), pipeLeftBackImage.getHeight() ) ) );
                    rightExtension.setTransform( AffineTransform.getScaleInstance( 1, syRight ) );
                    rightExtension.setOffset( topRight.getX(), topRight.getY() - PIPE_OPENING_PIXEL_Y_TOP * syRight );
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
                    setPaint( WaterColor.getBottomColor( fluidDensity.get() ) );
                }
            } );
        }} );
    }

    public double getPipeLeftViewHeight() {
        return transform.modelToView( pipe.getBottomLeft() ).getY() - transform.modelToView( pipe.getTopLeft() ).getY();
    }

    public double getPipeRightViewHeight() {
        return transform.modelToView( pipe.getBottomRight() ).getY() - transform.modelToView( pipe.getTopRight() ).getY();
    }
}