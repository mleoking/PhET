// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.model.PointTool;
import edu.colorado.phet.linegraphing.common.model.PointTool.Orientation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Tool that displays the (x,y) coordinates of a grid-point on the graph.
 * Origin is at the tip of the tool (bottom center in the image file.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointToolNode extends PhetPNode {

    private static final NumberFormat COORDINATES_FORMAT = new DefaultDecimalFormat( "0" );
    private static final double COORDINATES_Y_CENTER = 21; // center of the display area, measured from the top of the unscaled image file

    private final PNode bodyNode; // body (rectangular part) of the tool
    private final PPath backgroundNode; // the background behind the displayed value
    private final PText valueNode; // the displayed value

    /**
     * Constructor
     *
     * @param pointTool    the point tool
     * @param mvt          transform between model and view coordinate frames
     * @param graph        the graph that the point tool "sees"
     * @param dragBounds   drag bounds, in view coordinate frame
     * @param linesVisible whether lines are currently visible
     */
    public PointToolNode( final PointTool pointTool, final ModelViewTransform mvt, final Graph graph, Rectangle2D dragBounds, final Property<Boolean> linesVisible ) {
        this( pointTool.location.get(), pointTool.orientation, LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR );

        // location and display
        RichSimpleObserver observer = new RichSimpleObserver() {
            @Override public void update() {

                // move to location
                Vector2D location = pointTool.location.get();
                setOffset( mvt.modelToView( location ).toPoint2D() );

                // display value and highlighting
                if ( graph.contains( location ) ) {
                    setCoordinates( location );
                    if ( linesVisible.get() ) {
                        // use the line's color to highlight
                        Line onLine = pointTool.onLine.get();
                        setForeground( onLine == null ? LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR : LGColors.POINT_TOOL_FOREGROUND_HIGHLIGHT_COLOR );
                        setBackground( onLine == null ? LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR : onLine.color );
                    }
                    else {
                        setForeground( LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR );
                        setBackground( LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR );
                    }
                }
                else {
                    setCoordinates( Strings.POINT_UNKNOWN );
                    setForeground( LGColors.POINT_TOOL_FOREGROUND_NORMAL_COLOR );
                    setBackground( LGColors.POINT_TOOL_BACKGROUND_NORMAL_COLOR );
                }
            }
        };
        observer.observe( pointTool.location, pointTool.onLine, linesVisible );

        // dragging
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PointToolDragHandler( this, pointTool.location, mvt, graph, dragBounds ) );
    }

    /**
     * This constructor creates a node that is independent of the model.
     * This was needed so that we could easily generate images of the point tool, for inclusion in the game reward.
     */
    public PointToolNode( Vector2D point, Orientation orientation, Color background ) {

        // tool body
        bodyNode = new PImage( Images.POINT_TOOL_BODY );

        /*
         * Pointy tip, separate from the body and not pickable.
         * Because Piccolo's picking bounds are rectangular, making the tip pickable made it difficult
         * to pick a line manipulator when the tip and manipulator were on the same grid point.
         * Making the tip non-pickable was determined to be an acceptable and "natural feeling" solution.
         */
        PNode tipNode = new PImage( Images.POINT_TOOL_TIP );
        tipNode.setPickable( false );

        // background behind the displayed value, shows through a transparent hole in the display area portion of the body image
        final int backgroundMargin = 5;
        backgroundNode = new PPath( new Rectangle2D.Double( 0, 0,
                                                            bodyNode.getFullBoundsReference().getWidth() - ( 2 * backgroundMargin ),
                                                            bodyNode.getFullBoundsReference().getHeight() - ( 2 * backgroundMargin ) ) );
        backgroundNode.setStroke( null );
        backgroundNode.setOffset( bodyNode.getOffset() );
        backgroundNode.setPickable( false );

        // displayed value
        valueNode = new PText( "?" );
        valueNode.setFont( new PhetFont( Font.BOLD, 15 ) );
        valueNode.setPickable( false );

        // rendering order
        addChild( tipNode );
        addChild( backgroundNode );
        addChild( bodyNode );
        addChild( valueNode );

        // orientation
        if ( orientation == Orientation.DOWN ) {
            tipNode.setOffset( -tipNode.getFullBoundsReference().getWidth() / 2, -tipNode.getFullBoundsReference().getHeight() );
            bodyNode.setOffset( -bodyNode.getFullBoundsReference().getWidth() / 2, tipNode.getFullBoundsReference().getMinY() - bodyNode.getFullBoundsReference().getHeight() );
            backgroundNode.setOffset( bodyNode.getXOffset() + backgroundMargin, bodyNode.getYOffset() + backgroundMargin );
            valueNode.setOffset( 0, bodyNode.getFullBoundsReference().getMinY() + COORDINATES_Y_CENTER - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }
        else {
            tipNode.rotate( Math.PI );
            tipNode.setOffset( tipNode.getFullBoundsReference().getWidth() / 2, tipNode.getFullBoundsReference().getHeight() );
            bodyNode.setOffset( -bodyNode.getFullBoundsReference().getWidth() / 2, tipNode.getFullBoundsReference().getMaxY() );
            backgroundNode.setOffset( bodyNode.getFullBoundsReference().getMinX() + backgroundMargin, bodyNode.getFullBoundsReference().getMinY() + backgroundMargin );
            valueNode.setOffset( 0, bodyNode.getFullBoundsReference().getMaxY() - COORDINATES_Y_CENTER - ( valueNode.getFullBoundsReference().getHeight() / 2 ) );
        }

        // default state
        setCoordinates( point );
        setBackground( background );
    }

    // Sets the displayed value to a point
    private void setCoordinates( Vector2D point ) {
        setCoordinates( MessageFormat.format( Strings.POINT_XY, COORDINATES_FORMAT.format( point.getX() ), COORDINATES_FORMAT.format( point.getY() ) ) );
    }

    // Sets the displayed value to an arbitrary string
    private void setCoordinates( String s ) {
        valueNode.setText( s );
        // horizontally centered
        valueNode.setOffset( bodyNode.getFullBoundsReference().getCenterX() - ( valueNode.getFullBoundsReference().getWidth() / 2 ), valueNode.getYOffset() );
    }

    // Sets the foreground, the color of the displayed value
    private void setForeground( Color color ) {
        valueNode.setTextPaint( color );
    }

    // Sets the background, the color of the display area behind the value
    private void setBackground( Color color ) {
        backgroundNode.setPaint( color );
    }

    // Drag handler for the point tool, constrained to the range of the graph, snaps to integer grid.
    private static class PointToolDragHandler extends SimSharingDragHandler {

        private final PNode dragNode;
        private final Property<Vector2D> point;
        private final ModelViewTransform mvt;
        private final Graph graph;
        private final Rectangle2D dragBounds; // drag bounds, in view coordinate frame
        private double clickXOffset, clickYOffset; // offset of mouse click from dragNode's origin, in parent's coordinate frame

        public PointToolDragHandler( PNode dragNode, Property<Vector2D> point, ModelViewTransform mvt, Graph graph, Rectangle2D dragBounds ) {
            super( UserComponents.pointTool, UserComponentTypes.sprite, true );
            this.dragNode = dragNode;
            this.point = point;
            this.mvt = mvt;
            this.graph = graph;
            this.dragBounds = dragBounds;
        }

        @Override protected void startDrag( PInputEvent event ) {
            super.startDrag( event );
            // Note the mouse-click offset when dragging starts.
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            clickXOffset = pMouse.getX() - mvt.modelToViewX( point.get().getX() );
            clickYOffset = pMouse.getY() - mvt.modelToViewY( point.get().getY() );
            // Move the tool that we're dragging to the foreground.
            dragNode.moveToFront();
        }

        // Translate the model's location. Snap to integer grid if the location is inside the bounds of the graph.
        @Override protected void drag( final PInputEvent event ) {
            super.drag( event );
            Point2D pMouse = event.getPositionRelativeTo( dragNode.getParent() );
            final double viewX = pMouse.getX() - clickXOffset;
            final double viewY = pMouse.getY() - clickYOffset;
            Vector2D pView = constrainToBounds( viewX, viewY );
            point.set( mvt.viewToModel( pView ) );
            Vector2D pModel = mvt.viewToModel( pView );
            if ( graph.contains( point.get() ) ) {
                // snap to the grid
                point.set( new Vector2D( MathUtil.roundHalfUp( pModel.getX() ), MathUtil.roundHalfUp( pModel.getY() ) ) );
            }
            else {
                point.set( pModel );
            }
        }

        // Sim-sharing parameters
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
        private Vector2D constrainToBounds( double x, double y ) {
            return new Vector2D( MathUtil.clamp( dragBounds.getMinX(), x, dragBounds.getMaxX() ),
                                 MathUtil.clamp( dragBounds.getMinY(), y, dragBounds.getMaxY() ) );
        }
    }
}
