// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fluidpressureandflow.FPAFSimSharing.UserComponents;
import edu.colorado.phet.fluidpressureandflow.flow.model.Pipe;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeCrossSection;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_LEFT_FRONT;
import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_RIGHT;
import static edu.colorado.phet.fluidpressureandflow.flow.view.PipeBackNode.PIPE_OPENING_HEIGHT;

/**
 * The front part (in z-ordering) of the pipe graphics, so it looks like particles go inside the pipe.
 *
 * @author Sam Reid
 */
public class PipeFrontNode extends PNode {
    private final ModelViewTransform transform;
    private final Pipe pipe;
    public static final BasicStroke EDGE_STROKE = new BasicStroke( 8 );

    public PipeFrontNode( final ModelViewTransform transform, final Pipe pipe ) {
        this.transform = transform;
        this.pipe = pipe;

        //Show the edges and color match with the images
        final int edgeOffset = 4;
        addChild( new PhetPPath( EDGE_STROKE, new Color( 165, 91, 0 ) ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( AffineTransform.getTranslateInstance( 0, -edgeOffset ).createTransformedShape( transform.modelToView( pipe.getTopPath() ) ) );
                }
            } );
        }} );
        addChild( new PhetPPath( EDGE_STROKE, new Color( 0, 51, 91 ) ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    setPathTo( AffineTransform.getTranslateInstance( 0, +edgeOffset ).createTransformedShape( transform.modelToView( pipe.getBottomPath() ) ) );
                }
            } );
        }} );

        //image for the left of the pipe
        addChild( new PImage( PIPE_LEFT_FRONT ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeLeftViewHeight() / PIPE_OPENING_HEIGHT;
                    setTransform( AffineTransform.getScaleInstance( PipeBackNode.SX, sy ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
                    setOffset( topLeft.getX() - getImage().getWidth( null ) + PipeBackNode.PIPE_LEFT_OFFSET / PipeBackNode.SX, topLeft.getY() - PipeBackNode.PIPE_OPENING_PIXEL_Y_TOP * sy );
                }
            } );
        }} );

        //image for the right of the pipe
        addChild( new PImage( PIPE_RIGHT ) {{
            pipe.addShapeChangeListener( new SimpleObserver() {
                public void update() {
                    double sy = getPipeRightViewHeight() / PIPE_OPENING_HEIGHT;
                    setTransform( AffineTransform.getScaleInstance( PipeBackNode.SX, sy ) );
                    final Point2D topLeft = transform.modelToView( pipe.getTopRight() );
                    setOffset( topLeft.getX() - getImage().getWidth( null ) + PipeBackNode.PIPE_LEFT_OFFSET / PipeBackNode.SX, topLeft.getY() - PipeBackNode.PIPE_OPENING_PIXEL_Y_TOP * sy );
                }
            } );
        }} );

        //Show user-draggable controls for each cross section
        for ( PipeCrossSection crossSection : pipe.getControlCrossSections() ) {
            addChild( new PipeCrossSectionControl( transform, crossSection ) );
        }

        //Also allow the user to translate the left and right sides up and down
        double offsetX = 0.6;
        addChild( new PipeOffsetControl( UserComponents.leftPipeHandle, transform, pipe.getControlCrossSections().get( 0 ), -offsetX ) );
        addChild( new PipeOffsetControl( UserComponents.rightPipeHandle, transform, pipe.getControlCrossSections().get( pipe.getControlCrossSections().size() - 1 ), offsetX ) );
    }

    public double getPipeRightViewHeight() {
        final Point2D bottomRight = transform.modelToView( pipe.getBottomRight() );
        return bottomRight.getY() - transform.modelToView( pipe.getTopRight() ).getY();
    }

    public double getPipeLeftViewHeight() {
        final Point2D topLeft = transform.modelToView( pipe.getTopLeft() );
        final Point2D bottomLeft = transform.modelToView( pipe.getBottomLeft() );
        return bottomLeft.getY() - topLeft.getY();
    }
}
