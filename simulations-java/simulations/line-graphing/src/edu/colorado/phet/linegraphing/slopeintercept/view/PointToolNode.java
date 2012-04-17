// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGResources.Images;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.slopeintercept.model.PointTool;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Tool that displays the (x,y) coordinates of a point on the graph.
 * Origin is at the tip of the tool (bottom center in the image file.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointToolNode extends PhetPNode {

    private static final String COORDINATES_PATTERN = "({0}, {1})"; // space between the coordinates because some locales use ',' as the decimal separator
    private static final NumberFormat COORDINATES_FORMAT = new DefaultDecimalFormat( "0" );
    private static final double COORDINATES_Y_CENTER = 21; // center of the display area, measured from the top of the unscaled image file

    /**
     * Constructor
     * @param pointTool the point tool
     * @param mvt model-view transform
     * @param dragBounds drag bounds, in view coordinate frame
     */
    public PointToolNode( final PointTool pointTool, final ModelViewTransform mvt, final Graph graph, Rectangle2D dragBounds, final Property<Boolean> linesVisible ) {

        // tool body
        final PNode bodyNode = new PImage( Images.POINT_TOOL );
        bodyNode.setOffset( -bodyNode.getFullBoundsReference().getWidth() / 2, -bodyNode.getFullBoundsReference().getHeight() );

        // coordinate display
        final PText coordinatesNode = new PText();
        coordinatesNode.setFont( new PhetFont( Font.BOLD, 15 ) );

        // display background, shows through a transparent hole in the display area portion of the body image
        final int margin = 10;
        final PPath backgroundNode = new PPath( new Rectangle2D.Double( 10, 5,
                                                                        bodyNode.getFullBoundsReference().getWidth() - ( 2 * margin ),
                                                                        0.65 * bodyNode.getFullBoundsReference().getHeight() ) );
        backgroundNode.setStroke( null );
        backgroundNode.setOffset( bodyNode.getOffset() );

        // rendering order
        addChild( backgroundNode );
        addChild( bodyNode );
        addChild( coordinatesNode );

        // location and display
        RichSimpleObserver observer = new RichSimpleObserver() {
            @Override public void update() {

                // move to location
                ImmutableVector2D location = pointTool.location.get();
                setOffset( mvt.modelToView( location ).toPoint2D() );

                // display value and highlighting
                if ( graph.contains( location ) ) {
                    coordinatesNode.setText( MessageFormat.format( COORDINATES_PATTERN, COORDINATES_FORMAT.format( location.getX() ), COORDINATES_FORMAT.format( location.getY() ) ) );
                    if ( linesVisible.get() ) {
                        // use the line's color to highlight
                        SlopeInterceptLine onLine = pointTool.onLine.get();
                        coordinatesNode.setTextPaint( onLine == null ? LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR : LGColors.POINT_TOOL_FOREGROUND_HIGHLIGHT_COLOR );
                        backgroundNode.setPaint( onLine == null ? LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR : onLine.color );
                    }
                    else {
                        coordinatesNode.setTextPaint( LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR );
                        backgroundNode.setPaint( LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR );
                    }
                }
                else {
                    coordinatesNode.setText( "- -" );
                    coordinatesNode.setTextPaint( LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR );
                    backgroundNode.setPaint( LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR );
                }

                // horizontally centered
                coordinatesNode.setOffset( bodyNode.getFullBoundsReference().getCenterX() - ( coordinatesNode.getFullBoundsReference().getWidth() / 2 ),
                                           bodyNode.getFullBoundsReference().getMinY() + COORDINATES_Y_CENTER - ( coordinatesNode.getFullBoundsReference().getHeight() / 2 ) );
            }
        };
        observer.observe( pointTool.location, pointTool.onLine, linesVisible );

        // dragging
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PointToolDragHandler( this, pointTool.location, mvt, graph, dragBounds ) );
    }

    // Drag handler for the point tool, constrained to the range of the graph, snaps to grid.
    private static class PointToolDragHandler extends SimSharingDragHandler {

        private final PNode dragNode;
        private final Property<ImmutableVector2D> point;
        private final ModelViewTransform mvt;
        private final Graph graph;
        private final Rectangle2D dragBounds; // drag bounds, in view coordinate frame
        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public PointToolDragHandler( PNode dragNode, Property<ImmutableVector2D> point, ModelViewTransform mvt, Graph graph, Rectangle2D dragBounds ) {
            super( UserComponents.pointTool, UserComponentTypes.sprite, true );
            this.dragNode = dragNode;
            this.point = point;
            this.mvt = mvt;
            this.graph = graph;
            this.dragBounds = dragBounds;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            clickXOffset = pMouse.getX() - mvt.modelToViewX( point.get().getX() );
            clickYOffset = pMouse.getY() - mvt.modelToViewY( point.get().getY() );
        }

        @Override protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            final double viewX = pMouse.getX() - clickXOffset;
            final double viewY = pMouse.getY() - clickYOffset;
            ImmutableVector2D pView = constrainToBounds( viewX, viewY );
            point.set( mvt.viewToModel( pView ) );
        }

        @Override protected void endDrag( PInputEvent event ) {
            super.endDrag( event );
            if ( graph.contains( point.get() ) ) {
                // snap to the grid
                point.set( new ImmutableVector2D( MathUtil.round( point.get().getX() ), MathUtil.round( point.get().getY() ) ) );
            }
        }

        @Override public ParameterSet getParametersForAllEvents( PInputEvent event ) {
            return new ParameterSet().
                    with( ParameterKeys.x, COORDINATES_FORMAT.format( point.get().getX() ) ).
                    with( ParameterKeys.y, COORDINATES_FORMAT.format( point.get().getY() ) ).
                    with( super.getParametersForAllEvents( event ) );
        }

        /*
         * Constrains xy view coordinates to be within some view bounds.
         * Assumes the origin is at the bottom center of the drag node.
         */
        private ImmutableVector2D constrainToBounds( double x, double y ) {
            return new ImmutableVector2D( Math.max( dragBounds.getMinX() + ( dragNode.getFullBoundsReference().getWidth() / 2 ), Math.min( dragBounds.getMaxX() - ( dragNode.getFullBoundsReference().getWidth() / 2 ), x ) ),
                                          Math.max( dragBounds.getMinY() + dragNode.getFullBoundsReference().getHeight(), Math.min( dragBounds.getMaxY(), y ) ) );
        }
    }
}
