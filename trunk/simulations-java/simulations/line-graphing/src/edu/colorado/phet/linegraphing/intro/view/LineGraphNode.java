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
import edu.colorado.phet.linegraphing.LGResources;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Graph that can display zero or more lines.
 * One of the lines can be directly manipulated.
 * The node's origin is at model coordinate (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends PhetPNode {

    // grid
    private static final Stroke GRID_LINE_STROKE = new BasicStroke( 0.25f );
    private static final Color GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private static final int GRID_SPACING = 1; // model coordinates

    // outline
    private final Stroke OUTLINE_STROKE = GRID_LINE_STROKE;
    private final Color OUTLINE_COLOR = GRID_LINE_COLOR;

    // axes
    private final PDimension AXIS_ARROW_SIZE = new PDimension( 10, 10 );
    private final double AXIS_THICKNESS = 1;
    private final Color AXIS_COLOR = Color.BLACK;
    private final double AXIS_EXTENT = 0.5; // how far the arrow extends past the min/max ticks, in model coordinates
    private final PhetFont AXIS_LABEL_FONT = new PhetFont( Font.BOLD, 16 );
    private final double AXIS_LABEL_SPACING = 2; // space between end of axis and label

    public LineGraphNode( LineGraph graph, ModelViewTransform mvt ) {

        // Horizontal grid lines
        PNode horizontalGridLinesNode = new PNode();
        {
            final int numberOfHorizontalGridLines = graph.maxX - graph.minX + 1;
            final double minX = mvt.modelToViewDeltaX( graph.minX );
            final double maxX = mvt.modelToViewDeltaX( graph.maxX );
            // add one line for each unit of grid spacing
            for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
                final double yOffset = mvt.modelToViewDeltaY( graph.minY + ( i * GRID_SPACING ) );
                PPath gridLineNode = new PPath( new Line2D.Double( minX, yOffset, maxX, yOffset ) );
                gridLineNode.setStroke( GRID_LINE_STROKE );
                gridLineNode.setStrokePaint( GRID_LINE_COLOR );
                horizontalGridLinesNode.addChild( gridLineNode );
            }
        }

        // Vertical grid lines
        PNode verticalGridLinesNode = new PNode();
        {
            final int numberOfVerticalGridLines = graph.maxX - graph.minX + 1;
            final double minY = mvt.modelToViewDeltaX( graph.maxY );
            final double maxY = mvt.modelToViewDeltaX( graph.minY );
            // add one line for each unit of grid spacing
            for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
                final double xOffset = mvt.modelToViewDeltaY( graph.minX + ( i * GRID_SPACING ) );
                PPath gridLineNode = new PPath( new Line2D.Double( xOffset, minY, xOffset, maxY ) );
                gridLineNode.setStroke( GRID_LINE_STROKE );
                gridLineNode.setStrokePaint( GRID_LINE_COLOR );
                verticalGridLinesNode.addChild( gridLineNode );
            }
        }

        // Outline around the graph
        PPath outlineNode = new PPath( new Rectangle2D.Double( mvt.modelToViewDeltaX( graph.minX ), mvt.modelToViewDeltaY( graph.maxY ),
                                                               mvt.modelToViewDeltaX( graph.getWidth() ), mvt.modelToViewDeltaY( -graph.getHeight() ) ) );
        outlineNode.setStroke( OUTLINE_STROKE );
        outlineNode.setStrokePaint( OUTLINE_COLOR );

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
            PText labelNode = new PText( Strings.SYMBOL_HORIZONTAL_AXIS );
            labelNode.setFont( AXIS_LABEL_FONT );
            xAxisNode.addChild( labelNode );
            labelNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + AXIS_LABEL_SPACING,
                                 lineNode.getFullBoundsReference().getCenterY() - ( labelNode.getFullBoundsReference().getHeight() / 2 ) );
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
            PText labelNode = new PText( Strings.SYMBOL_VERTICAL_AXIS );
            labelNode.setFont( AXIS_LABEL_FONT );
            yAxisNode.addChild( labelNode );
            labelNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( labelNode.getFullBoundsReference().getWidth() / 2 ),
                                 lineNode.getFullBoundsReference().getMinY() - labelNode.getFullBoundsReference().getHeight() - AXIS_LABEL_SPACING );
        }

        // rendering order
        {
            addChild( horizontalGridLinesNode );
            addChild( verticalGridLinesNode );
            addChild( xAxisNode );
            addChild( yAxisNode );
            addChild( outlineNode );
        }
    }
}
