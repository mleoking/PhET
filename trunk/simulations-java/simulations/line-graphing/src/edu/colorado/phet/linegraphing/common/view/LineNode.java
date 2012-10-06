// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for visual representation of all lines.
 * The line's equation (in reduced form) is positioned towards the tip, parallel with the line.
 * Subclasses are responsible for creating the equation in the correct form (slope-intercept, point-slope.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class LineNode extends PComposite {

    private static final PDimension ARROW_HEAD_SIZE = new PDimension( 10, 10 );
    private static final double LINE_THICKNESS = 3;
    private static final double LINE_EXTENT = 25; // how far the line extends past the grid
    private static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 18 );

    public final Line line;
    private final DoubleArrowNode arrowNode;
    private final EquationNode equationNode;

    /**
     * Constructor
     *
     * @param line  the line to draw
     * @param graph the graph to draw it on
     * @param mvt   the transform between model and view coordinate frames
     */
    public LineNode( final Line line, Graph graph, ModelViewTransform mvt ) {

        this.line = line;

        final double xExtent = mvt.viewToModelDeltaX( LINE_EXTENT );
        final double yExtent = Math.abs( mvt.viewToModelDeltaY( LINE_EXTENT ) );

        double tailX, tailY, tipX, tipY;

        if ( line.run == 0 ) {
            // x = 0
            tailX = line.x1;
            tailY = graph.yRange.getMax() + yExtent;
            tipX = line.x1;
            tipY = graph.yRange.getMin() - yExtent;
        }
        else if ( line.rise == 0 ) {
            // y = b
            tailX = graph.xRange.getMin() - xExtent;
            tailY = line.y1;
            tipX = graph.xRange.getMax() + yExtent;
            tipY = line.y1;
        }
        else {
            // tail is the left-most end point. Compute x such that the point is inside the grid.
            tailX = graph.xRange.getMin() - xExtent;
            tailY = line.solveY( tailX );
            if ( tailY < graph.yRange.getMin() - yExtent ) {
                tailX = line.solveX( graph.yRange.getMin() - yExtent );
                tailY = line.solveY( tailX );
            }
            else if ( tailY > graph.yRange.getMax() + yExtent ) {
                tailX = line.solveX( graph.yRange.getMax() + yExtent );
                tailY = line.solveY( tailX );
            }

            // tip is the right-most end point. Compute x such that the point is inside the grid.
            tipX = graph.xRange.getMax() + xExtent;
            tipY = line.solveY( tipX );
            if ( tipY < graph.yRange.getMin() - yExtent ) {
                tipX = line.solveX( graph.yRange.getMin() - yExtent );
                tipY = line.solveY( tipX );
            }
            else if ( tipY > graph.yRange.getMax() + yExtent ) {
                tipX = line.solveX( graph.yRange.getMax() + yExtent );
                tipY = line.solveY( tipX );
            }
        }

        // double-headed arrow
        Point2D tailLocation = new Point2D.Double( mvt.modelToViewX( tailX ), mvt.modelToViewY( tailY ) );
        Point2D tipLocation = new Point2D.Double( mvt.modelToViewX( tipX ), mvt.modelToViewY( tipY ) );
        arrowNode = new DoubleArrowNode( tailLocation, tipLocation, ARROW_HEAD_SIZE.getHeight(), ARROW_HEAD_SIZE.getWidth(), LINE_THICKNESS );
        arrowNode.setPaint( line.color );
        arrowNode.setStroke( null ); // DoubleArrowNode is a shape that we fill, no need to stroke
        addChild( arrowNode );

        // equation
        PNode equationParentNode = new PNode();
        addChild( equationParentNode );
        equationParentNode.setOffset( tipLocation );
        equationParentNode.setRotation( line.run == 0 ? Math.PI / 2 : -Math.atan( line.getSlope() ) );
        equationNode = createEquationNode( line, EQUATION_FONT, line.color );
        PNode zeroOffsetNode = new ZeroOffsetNode( equationNode );
        equationParentNode.addChild( zeroOffsetNode );
        if ( line.rise > 0 && line.run > 0 ) {
            // quadrant 1: put equation below the line, so that it isn't occluded by the rise/run brackets
            zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 12, 5 );
        }
        else {
            // all other quadrants: put equation above the line
            zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 12,
                                      -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
        }
    }

    // Creates the line's equation in the correct form.
    protected abstract EquationNode createEquationNode( Line line, PhetFont font, Color color );

    public void setEquationVisible( boolean visible ) {
        equationNode.setVisible( visible );
    }

    protected void updateColor( Color color ) {
        arrowNode.setPaint( color );
        equationNode.setPaintDeep( color );
    }
}
