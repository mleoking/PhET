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
 * The node's origin is at model coordinate (0,0).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LineGraphNode extends PhetPNode {

    private static final Stroke GRID_LINE_STROKE = new BasicStroke( 0.25f );
    private static final Color GRID_LINE_COLOR = Color.LIGHT_GRAY;
    private static final int GRID_SPACING = 1; // model coordinates
    private final Stroke OUTLINE_STROKE = GRID_LINE_STROKE;
    private final Color OUTLINE_COLOR = GRID_LINE_COLOR;

    public LineGraphNode( LineGraph graph, ModelViewTransform mvt ) {

        // Horizontal grid lines
        PNode horizontalGridLinesNode = new PNode();
        final int numberOfHorizontalGridLines = graph.maxX - graph.minX + 1;
        final double minX = mvt.modelToViewDeltaX( graph.minX );
        final double maxX = mvt.modelToViewDeltaX( graph.maxX );
        for ( int i = 0; i < numberOfHorizontalGridLines; i++ ) {
            final double yOffset = mvt.modelToViewDeltaY( graph.minY + ( i * GRID_SPACING ) );
            PPath gridLineNode = new PPath( new Line2D.Double( minX, yOffset, maxX, yOffset ) );
            gridLineNode.setStroke( GRID_LINE_STROKE );
            gridLineNode.setStrokePaint( GRID_LINE_COLOR );
            horizontalGridLinesNode.addChild( gridLineNode );
        }

        // Vertical grid lines
        PNode verticalGridLinesNode = new PNode();
        final int numberOfVerticalGridLines = graph.maxX - graph.minX + 1;
        final double minY = mvt.modelToViewDeltaX( graph.maxY );
        final double maxY = mvt.modelToViewDeltaX( graph.minY );
        for ( int i = 0; i < numberOfVerticalGridLines; i++ ) {
            final double xOffset = mvt.modelToViewDeltaY( graph.minX + ( i * GRID_SPACING ) );
            PPath gridLineNode = new PPath( new Line2D.Double( xOffset, minY, xOffset, maxY ) );
            gridLineNode.setStroke( GRID_LINE_STROKE );
            gridLineNode.setStrokePaint( GRID_LINE_COLOR );
            verticalGridLinesNode.addChild( gridLineNode );
        }

        // Outline around the graph
        System.out.println( mvt.modelToViewDeltaX( graph.minX ) + "," + mvt.modelToViewDeltaY( graph.maxY ) + "," +
                            mvt.modelToViewDeltaX( graph.getWidth() ) + "," + mvt.modelToViewDeltaY( graph.getHeight() ) );
        PPath outlineNode = new PPath( new Rectangle2D.Double( mvt.modelToViewDeltaX( graph.minX ), mvt.modelToViewDeltaY( graph.maxY ),
                                                               mvt.modelToViewDeltaX( graph.getWidth() ), mvt.modelToViewDeltaY( -graph.getHeight() ) ) );
        outlineNode.setStroke( OUTLINE_STROKE );
        outlineNode.setStrokePaint( OUTLINE_COLOR );

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
