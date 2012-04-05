// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Basic graph, displays grid and axes.
 * The node's origin is at model coordinate (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class GraphNode extends PhetPNode {

    // grid
    private static final Color GRID_BACKGROUND = Color.WHITE;
    private static final Stroke MINOR_GRID_LINE_STROKE = new BasicStroke( 0.25f );
    private static final Color MINOR_GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private static final Stroke MAJOR_GRID_LINE_STROKE = new BasicStroke( 0.5f );
    private static final Color MAJOR_GRID_LINE_COLOR = Color.LIGHT_GRAY;

    // axes
    private final PDimension AXIS_ARROW_SIZE = new PDimension( 10, 10 );
    private final double AXIS_THICKNESS = 1;
    private final Color AXIS_COLOR = Color.BLACK;
    private final double AXIS_EXTENT = 1.0; // how far the arrow extends past the min/max ticks, in model coordinates
    private final PhetFont AXIS_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private final double AXIS_LABEL_SPACING = 2; // space between end of axis and label

    // ticks
    private final int MAJOR_TICK_SPACING = 5; // model units
    private final double MINOR_TICK_LENGTH = 3; // how far a minor tick extends from the axis
    private final Stroke MINOR_TICK_STROKE = new BasicStroke( 0.5f );
    private final Color MINOR_TICK_COLOR = Color.BLACK;
    private final double MAJOR_TICK_LENGTH = 6; // how far a major tick extends from the axis
    private final Stroke MAJOR_TICK_STROKE = new BasicStroke( 1f );
    private final Color MAJOR_TICK_COLOR = Color.BLACK;
    private final PhetFont MAJOR_TICK_FONT = new PhetFont( Font.PLAIN, 16 );
    private final double TICK_LABEL_SPACING = 2;
    private final double MINUS_SIGN_WIDTH = new PhetPText( "-", MAJOR_TICK_FONT ).getFullBoundsReference().getWidth();

    public GraphNode( LineGraph graph, ModelViewTransform mvt ) {

        //TODO review duplication for horizontal vs vertical grid, axis, etc.

        // Background
        PPath backgroundNode = new PPath( new Rectangle2D.Double( mvt.modelToViewDeltaX( graph.minX ), mvt.modelToViewDeltaY( graph.maxY ), mvt.modelToViewDeltaX( graph.getWidth() ), mvt.modelToViewDeltaX( graph.getHeight() ) ) );
        backgroundNode.setPaint( GRID_BACKGROUND );
        backgroundNode.setStroke( null );
        addChild( backgroundNode );

        // Horizontal grid lines
        PNode horizontalGridLinesNode = new PNode();
        final int numberOfHorizontalGridLines = graph.maxX - graph.minX + 1;
        {
            final double minX = mvt.modelToViewDeltaX( graph.minX );
            final double maxX = mvt.modelToViewDeltaX( graph.maxX );
            // add one line for each unit of grid spacing
            for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
                final int modelY = graph.minY + i;
                if ( modelY != 0 ) { // skip origin
                    final double yOffset = mvt.modelToViewDeltaY( modelY );
                    PPath gridLineNode = new PPath( new Line2D.Double( minX, yOffset, maxX, yOffset ) );
                    horizontalGridLinesNode.addChild( gridLineNode );
                    if ( Math.abs( modelY ) % MAJOR_TICK_SPACING == 0 ) {
                        gridLineNode.setStroke( MAJOR_GRID_LINE_STROKE );
                        gridLineNode.setStrokePaint( MAJOR_GRID_LINE_COLOR );
                    }
                    else {
                        gridLineNode.setStroke( MINOR_GRID_LINE_STROKE );
                        gridLineNode.setStrokePaint( MINOR_GRID_LINE_COLOR );
                    }
                }
            }
        }

        // Vertical grid lines
        PNode verticalGridLinesNode = new PNode();
        final int numberOfVerticalGridLines = graph.maxX - graph.minX + 1;
        {
            final double minY = mvt.modelToViewDeltaY( graph.maxY );
            final double maxY = mvt.modelToViewDeltaY( graph.minY );
            // add one line for each unit of grid spacing
            for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
                final double modelX = graph.minX + i;
                if ( modelX != 0 ) { // skip origin
                    final double xOffset = mvt.modelToViewDeltaX( modelX );
                    PPath gridLineNode = new PPath( new Line2D.Double( xOffset, minY, xOffset, maxY ) );
                    verticalGridLinesNode.addChild( gridLineNode );
                    if ( Math.abs( modelX ) % MAJOR_TICK_SPACING == 0 ) {
                        gridLineNode.setStroke( MAJOR_GRID_LINE_STROKE );
                        gridLineNode.setStrokePaint( MAJOR_GRID_LINE_COLOR );
                    }
                    else {
                        gridLineNode.setStroke( MINOR_GRID_LINE_STROKE );
                        gridLineNode.setStrokePaint( MINOR_GRID_LINE_COLOR );
                    }
                }
            }
        }

        // X axis
        PNode xAxisNode = new PNode();
        {
            // horizontal line with arrows at both ends
            Point2D tailLocation = new Point2D.Double( mvt.modelToViewDeltaX( graph.minX - AXIS_EXTENT ), 0 );
            Point2D tipLocation = new Point2D.Double( mvt.modelToViewDeltaX( graph.maxX + AXIS_EXTENT ), 0 );
            DoubleArrowNode lineNode = new DoubleArrowNode( tailLocation, tipLocation, AXIS_ARROW_SIZE.getHeight(), AXIS_ARROW_SIZE.getWidth(), AXIS_THICKNESS );
            lineNode.setPaint( AXIS_COLOR );
            lineNode.setStroke( null ); // DoubleArrowNode is a shape that we're filling, no need to stroke
            xAxisNode.addChild( lineNode );

            // label at positive end
            PText labelNode = new PText( Strings.SYMBOL_X );
            labelNode.setFont( AXIS_LABEL_FONT );
            xAxisNode.addChild( labelNode );
            labelNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + AXIS_LABEL_SPACING,
                                 lineNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );

            // ticks
            for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
                final int modelX = graph.minX + i;
                if ( modelX != 0 ) { // skip the origin
                    final double x = mvt.modelToViewDeltaX( modelX );
                    if ( Math.abs( modelX ) % MAJOR_TICK_SPACING == 0 ) {
                        // major tick line
                        PPath tickLineNode = new PPath( new Line2D.Double( x, -MAJOR_TICK_LENGTH, x, MAJOR_TICK_LENGTH ) );
                        tickLineNode.setStroke( MAJOR_TICK_STROKE );
                        tickLineNode.setPaint( MAJOR_TICK_COLOR );
                        xAxisNode.addChild( tickLineNode );
                        // major tick label
                        PText tickLabelNode = new PText( String.valueOf( modelX ) );
                        tickLabelNode.setFont( MAJOR_TICK_FONT );
                        tickLabelNode.setTextPaint( MAJOR_TICK_COLOR );
                        xAxisNode.addChild( tickLabelNode );
                        // center label under line, compensate for minus sign
                        final double signXOffset = ( modelX < 0 ) ? -( MINUS_SIGN_WIDTH / 2 ) : 0;
                        tickLabelNode.setOffset( tickLineNode.getFullBoundsReference().getCenterX() - ( tickLabelNode.getFullBoundsReference().getWidth() / 2 ) + signXOffset,
                                                 tickLineNode.getFullBoundsReference().getMaxY() + TICK_LABEL_SPACING );
                    }
                    else {
                        // minor tick with no label
                        PPath tickLineNode = new PPath( new Line2D.Double( x, -MINOR_TICK_LENGTH, x, MINOR_TICK_LENGTH ) );
                        tickLineNode.setStroke( MINOR_TICK_STROKE );
                        tickLineNode.setPaint( MINOR_TICK_COLOR );
                        xAxisNode.addChild( tickLineNode );
                    }
                }
            }
        }

        // Y axis
        PNode yAxisNode = new PNode();
        {
            // horizontal line with arrows at both ends
            Point2D tailLocation = new Point2D.Double( 0, mvt.modelToViewDeltaY( graph.minY - AXIS_EXTENT ) );
            Point2D tipLocation = new Point2D.Double( 0, mvt.modelToViewDeltaY( graph.maxY + AXIS_EXTENT ) );
            DoubleArrowNode lineNode = new DoubleArrowNode( tailLocation, tipLocation, AXIS_ARROW_SIZE.getHeight(), AXIS_ARROW_SIZE.getWidth(), AXIS_THICKNESS );
            lineNode.setPaint( AXIS_COLOR );
            lineNode.setStroke( null ); // DoubleArrowNode is a shape that we're filling, no need to stroke
            yAxisNode.addChild( lineNode );

            // label at positive end
            PText labelNode = new PText( Strings.SYMBOL_Y );
            labelNode.setFont( AXIS_LABEL_FONT );
            yAxisNode.addChild( labelNode );
            labelNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                 lineNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - AXIS_LABEL_SPACING );

            // ticks
            for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
                final int modelY = graph.minY + i;
                if ( modelY != 0 ) { // skip the origin
                    final double y = mvt.modelToViewDeltaY( modelY );
                    if ( Math.abs( modelY ) % MAJOR_TICK_SPACING == 0 ) {
                        // major tick line
                        PPath tickLineNode = new PPath( new Line2D.Double( -MAJOR_TICK_LENGTH, y, MAJOR_TICK_LENGTH, y ) );
                        tickLineNode.setStroke( MAJOR_TICK_STROKE );
                        tickLineNode.setPaint( MAJOR_TICK_COLOR );
                        yAxisNode.addChild( tickLineNode );
                        // major tick label
                        PText tickLabelNode = new PText( String.valueOf( modelY ) );
                        tickLabelNode.setFont( MAJOR_TICK_FONT );
                        tickLabelNode.setTextPaint( MAJOR_TICK_COLOR );
                        yAxisNode.addChild( tickLabelNode );
                        // center label to left of line
                        tickLabelNode.setOffset( tickLineNode.getFullBoundsReference().getMinX() - tickLabelNode.getFullBoundsReference().getWidth() - TICK_LABEL_SPACING,
                                                 tickLineNode.getFullBoundsReference().getCenterY() - ( tickLabelNode.getFullBoundsReference().getHeight() / 2 ) );
                    }
                    else {
                        // minor tick with no label
                        PPath tickLineNode = new PPath( new Line2D.Double( -MINOR_TICK_LENGTH, y, MINOR_TICK_LENGTH, y ) );
                        tickLineNode.setStroke( MINOR_TICK_STROKE );
                        tickLineNode.setPaint( MINOR_TICK_COLOR );
                        yAxisNode.addChild( tickLineNode );
                    }
                }
            }
        }

        // rendering order
        {
            addChild( horizontalGridLinesNode );
            addChild( verticalGridLinesNode );
            addChild( xAxisNode );
            addChild( yAxisNode );
        }
    }
}
