// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.linegraphing.intro.model.LineGraph;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Visual representation of a line in slope-intercept form, with arrows on both ends.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLineNode extends PComposite {

    private static final PDimension ARROW_HEAD_SIZE = new PDimension( 8, 8 );
    private static final double LINE_THICKNESS = 1;

    public SlopeInterceptLineNode( SlopeInterceptLine line, LineGraph graph, ModelViewTransform mvt, Color color ) {

        // constrain the line to the range of the graph
        double tipX = graph.minX;
        double tipY = line.solveY( tipX );
        if ( tipY < graph.minY ) {
            tipX = line.solveX( graph.minY );
        }
        else if ( tipY > graph.maxY ) {
            tipX = line.solveX( graph.maxY );
        }

        double tailX = graph.maxX;
        double tailY = line.solveY( tailX );
        if ( tailY < graph.minY ) {
            tailX = line.solveX( graph.minY );
        }
        else if ( tailY > graph.maxY ) {
            tailX = line.solveX( graph.maxY );
        }

        Point2D tipLocation = new Point2D.Double( mvt.modelToViewDeltaX( tipX ),
                                                  mvt.modelToViewDeltaY( line.solveY( tipX ) ) );
        Point2D tailLocation = new Point2D.Double( mvt.modelToViewDeltaX( tailX ),
                                                   mvt.modelToViewDeltaY( line.solveY( tailX ) ) );
        DoubleArrowNode arrowNode = new DoubleArrowNode( tailLocation, tipLocation, ARROW_HEAD_SIZE.getHeight(), ARROW_HEAD_SIZE.getWidth(), LINE_THICKNESS );
        arrowNode.setPaint( color );
        arrowNode.setStroke( null ); // DoubleArrowNode is a shape that we fill, no need to stroke
        addChild( arrowNode );
    }
}
