// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
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
 * <p>
 * By default, a line is not labeled with an equation. Subclasses are responsible for creating an equation
 * in the correct form (slope, slope-intercept, point-slope.) The line's equation is positioned towards
 * the tip, parallel with the line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineNode extends PComposite {

    private static final PDimension ARROW_HEAD_SIZE = new PDimension( 10, 10 );
    private static final double LINE_THICKNESS = 3;
    private static final double LINE_EXTENT = 25; // how far the line extends past the grid
    private static final PhetFont EQUATION_FONT = new PhetFont( Font.BOLD, 18 );

    public final Line line;
    private final DoubleArrowNode arrowNode;
    private final PNode equationParentNode;
    private final Vector2D tailLocation, tipLocation;

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
        tailLocation = new Vector2D( mvt.modelToViewX( tailX ), mvt.modelToViewY( tailY ) );
        tipLocation = new Vector2D( mvt.modelToViewX( tipX ), mvt.modelToViewY( tipY ) );
        arrowNode = new DoubleArrowNode( tailLocation.toPoint2D(), tipLocation.toPoint2D(), ARROW_HEAD_SIZE.getHeight(), ARROW_HEAD_SIZE.getWidth(), LINE_THICKNESS );
        arrowNode.setPaint( line.color );
        arrowNode.setStroke( null ); // DoubleArrowNode is a shape that we fill, no need to stroke
        addChild( arrowNode );

        // equation
        equationParentNode = new PNode();
        addChild( equationParentNode );
        equationParentNode.setOffset( tipLocation.toPoint2D() );
        equationParentNode.setRotation( line.undefinedSlope() ? Math.PI / 2 : -Math.atan( line.getSlope() ) );
        updateEquation( line, EQUATION_FONT, line.color );
    }

    /*
     * By default, a line does not display an equation.
     * Subclasses must override this method to return an equation in the correct form.
     */
    protected PNode createEquationNode( Line line, PhetFont font, Color color ) {
        return new PNode();
    }

    public void setEquationVisible( boolean visible ) {
        equationParentNode.setVisible( visible );
    }

    protected void updateColor( Color color ) {
        arrowNode.setPaint( color );
        updateEquation( line, EQUATION_FONT, color );
    }

    private void updateEquation( Line line, PhetFont font, Color color ) {

        equationParentNode.removeAllChildren();

        PNode zeroOffsetNode = new ZeroOffsetNode( createEquationNode( line, font, color ) );
        equationParentNode.addChild( new ZeroOffsetNode( zeroOffsetNode ) );

        // Put equation where it won't interfere with slope tool or y-axis, at the end of the line that would have the slope manipulator.
        if ( line.undefinedSlope() ) {
            // this puts the "undefined slope" label to the right of the y-axis, at the same end of the line as the slope manipulator
            if ( line.rise < 0 ) {
                equationParentNode.setOffset( tipLocation.toPoint2D() );
                zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 30, -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
            }
            else {
                equationParentNode.setOffset( tailLocation.toPoint2D() );
                zeroOffsetNode.setOffset( 30, -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
            }
        }
        else if ( line.rise <= 0 ) {
            if ( line.run >= 0 ) {
                // equation above the line, at tip
                equationParentNode.setOffset( tipLocation.toPoint2D() );
                zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 30, -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
            }
            else {
                // equation above the line, at tail
                equationParentNode.setOffset( tailLocation.toPoint2D() );
                zeroOffsetNode.setOffset( 30, -zeroOffsetNode.getFullBoundsReference().getHeight() - 12 );
            }
        }
        else {
            if ( line.run > 0 ) {
                // equation below the line, at tip
                equationParentNode.setOffset( tipLocation.toPoint2D() );
                zeroOffsetNode.setOffset( -zeroOffsetNode.getFullBoundsReference().getWidth() - 30, 10 );
            }
            else {
                // equation below the line, at tail
                equationParentNode.setOffset( tailLocation.toPoint2D() );
                zeroOffsetNode.setOffset( 30, 10 );
            }
        }
    }
}
