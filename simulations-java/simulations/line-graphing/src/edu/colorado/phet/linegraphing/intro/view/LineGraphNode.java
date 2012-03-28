// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Graph that can display zero or more lines.
 * One of the lines can be directly manipulated.
 * The node's origin is at the upper-left corner of the grid, at model coordinate (maxY,minX).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends PhetPNode {

    private static final Stroke GRID_LINE_STROKE = new BasicStroke( 0.25f );
    private static final Color GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private final Stroke OUTLINE_STROKE = GRID_LINE_STROKE;
    private final Color OUTLINE_COLOR = GRID_LINE_COLOR;

    public LineGraphNode( LineGraph graph, ModelViewTransform mvt ) {

        // Outline around the graph
        final double outlineWidth = mvt.modelToViewDeltaX( graph.maxX - graph.minX );
        final double outlineHeight = Math.abs( mvt.modelToViewDeltaX( graph.maxY - graph.minY ) );
        PPath outlineNode = new PPath( new Rectangle2D.Double( 0, 0, outlineWidth, outlineHeight ) );
        outlineNode.setStroke( OUTLINE_STROKE );
        outlineNode.setStrokePaint( OUTLINE_COLOR );

        // Horizontal grid lines
        PNode horizontalGridLinesNode = new PNode();
        final int numberOfHorizontalGridLines = graph.maxX - graph.minX + 1;
        final double deltaY = mvt.modelToViewDeltaX( 1 );
        for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
            final double yOffset = i * deltaY;
            PPath gridLineNode = new PPath( new Line2D.Double( 0, yOffset, outlineWidth, yOffset ) );
            gridLineNode.setStroke( GRID_LINE_STROKE );
            gridLineNode.setStrokePaint( GRID_LINE_COLOR );
            horizontalGridLinesNode.addChild( gridLineNode );
        }

        // Vertical grid lines
        PNode verticalGridLinesNode = new PNode();
        final int numberOfVerticalGridLines = graph.maxX - graph.minX + 1;
        final double deltaX = mvt.modelToViewDeltaX( 1 );
        for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
            final double xOffset = i * deltaX;
            PPath gridLineNode = new PPath( new Line2D.Double( xOffset, 0, xOffset, outlineHeight ) );
            gridLineNode.setStroke( GRID_LINE_STROKE );
            gridLineNode.setStrokePaint( GRID_LINE_COLOR );
            verticalGridLinesNode.addChild( gridLineNode );
        }

        // X axis

        // Y axis

        // rendering order
        {
            addChild( horizontalGridLinesNode );
            addChild( verticalGridLinesNode );
            addChild( outlineNode );
        }
    }
}
