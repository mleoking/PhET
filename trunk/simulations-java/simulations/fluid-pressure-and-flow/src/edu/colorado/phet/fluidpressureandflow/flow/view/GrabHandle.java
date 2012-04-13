// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.flow.view;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.fluidpressureandflow.flow.model.PipeControlPoint;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.fluidpressureandflow.FluidPressureAndFlowResources.Images.PIPE_HANDLE_1;
import static edu.colorado.phet.fluidpressureandflow.flow.view.PipeFrontNode.EDGE_STROKE;

/**
 * Grab handle that lets the user translate the top or bottom of the pipe to deform the pipe.
 *
 * @author Sam Reid
 */
public class GrabHandle extends PNode {
    public GrabHandle( final ModelViewTransform transform, final PipeControlPoint controlPoint, final Function1<Point2D, Point2D> constraint, final boolean top ) {

        //Showing a handle image.
        //The original image is pointing up, flip it if this is a drag handle on the bottom of the pipe
        final BufferedImage sourceImage = BufferedImageUtils.multiScaleToWidth( PIPE_HANDLE_1, 34 );
        BufferedImage image = top ? sourceImage : BufferedImageUtils.flipY( sourceImage );

        //Add the image
        addChild( new PImage( image ) {{
            controlPoint.point.addObserver( new SimpleObserver() {
                public void update() {

                    //Update the location on initialization and when the model changes
                    //Move by one edge stroke width away so it will sit on the very edge of the pipe
                    final ImmutableVector2D modelPoint = transform.modelToView( controlPoint.point.get() );
                    double dy = top ?
                                -getFullBounds().getHeight() - EDGE_STROKE.getLineWidth() :
                                EDGE_STROKE.getLineWidth();
                    setOffset( modelPoint.plus( -getFullBounds().getWidth() / 2, dy ).toPoint2D() );
                }
            } );

            //Make it draggable, but only up and down
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new RelativeDragHandler( this, transform, controlPoint.point, constraint ) {
                @Override protected void sendMessage( final Point2D modelPoint ) {
                    super.sendMessage( modelPoint );
                    SimSharingManager.sendUserMessage( controlPoint.component, UserComponentTypes.sprite, UserActions.drag, ParameterSet.parameterSet( ParameterKeys.x, modelPoint.getX() ).with( ParameterKeys.y, modelPoint.getY() ) );
                }
            } );
        }} );
    }
}