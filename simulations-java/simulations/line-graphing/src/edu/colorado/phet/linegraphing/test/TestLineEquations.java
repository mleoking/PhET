// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeInteractiveEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptInteractiveEquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Tests simplification and rendering of equations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestLineEquations {

    public static void main( String[] args ) {

        PhetFont interactiveFont = new PhetFont( Font.BOLD, 28 );
        final PhetFont staticFont = new PhetFont( Font.PLAIN, interactiveFont.getSize() );
        final Color staticColor = Color.BLACK;

        DoubleRange range = new DoubleRange( -10, 10 );
        Property<DoubleRange> x1Range = new Property<DoubleRange>( range );
        Property<DoubleRange> y1Range = new Property<DoubleRange>( range );
        Property<DoubleRange> riseRange = new Property<DoubleRange>( range );
        Property<DoubleRange> runRange = new Property<DoubleRange>( range );

        final int xSpacing = 150;
        final int ySpacing = 60;

        // slope-intercept tests
        PNode slopeInterceptParent = new PNode();
        {
            // model
            Property<Line> line = new Property<Line>( Line.createSlopeIntercept( 1, 1, 1, LGColors.INTERACTIVE_LINE ) );

            // equations, all combinations of interactivity
            SlopeInterceptInteractiveEquationNode equationNode1 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, y1Range, true, true, interactiveFont, staticFont, staticColor );
            SlopeInterceptInteractiveEquationNode equationNode2 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, y1Range, true, false, interactiveFont, staticFont, staticColor );
            SlopeInterceptInteractiveEquationNode equationNode3 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, y1Range, false, true, interactiveFont, staticFont, staticColor );
            SlopeInterceptInteractiveEquationNode equationNode4 = new SlopeInterceptInteractiveEquationNode( line, riseRange, runRange, y1Range, false, false, interactiveFont, staticFont, staticColor );

            // rendering order
            slopeInterceptParent.addChild( equationNode1 );
            slopeInterceptParent.addChild( equationNode2 );
            slopeInterceptParent.addChild( equationNode3 );
            slopeInterceptParent.addChild( equationNode4 );

            // layout
            equationNode1.setOffset( 0, 0 );
            equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode4.setOffset( equationNode1.getXOffset(), equationNode3.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // point-slope tests
        PNode pointSlopeParent = new PNode();
        {
            // model
            Property<Line> line = new Property<Line>( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );

            // equations, all combinations of interactivity
            PointSlopeInteractiveEquationNode equationNode1 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, true, true, true, interactiveFont, staticFont, staticColor );
            PointSlopeInteractiveEquationNode equationNode2 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, false, false, true, interactiveFont, staticFont, staticColor );
            PointSlopeInteractiveEquationNode equationNode3 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, true, true, false, interactiveFont, staticFont, staticColor );
            PointSlopeInteractiveEquationNode equationNode4 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, true, false, false, interactiveFont, staticFont, staticColor );
            PointSlopeInteractiveEquationNode equationNode5 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, false, true, false, interactiveFont, staticFont, staticColor );
            PointSlopeInteractiveEquationNode equationNode6 = new PointSlopeInteractiveEquationNode( line, x1Range, y1Range, riseRange, runRange, false, false, false, interactiveFont, staticFont, staticColor );

            // rendering order
            pointSlopeParent.addChild( equationNode1 );
            pointSlopeParent.addChild( equationNode2 );
            pointSlopeParent.addChild( equationNode3 );
            pointSlopeParent.addChild( equationNode4 );
            pointSlopeParent.addChild( equationNode5 );
            pointSlopeParent.addChild( equationNode6 );

            // layout
            equationNode1.setOffset( 0, 0 );
            equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode4.setOffset( equationNode1.getXOffset(), equationNode3.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode5.setOffset( equationNode1.getXOffset(), equationNode4.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode6.setOffset( equationNode1.getXOffset(), equationNode5.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );
        canvas.getLayer().addChild( slopeInterceptParent );
        canvas.getLayer().addChild( pointSlopeParent );

        // layout
        slopeInterceptParent.setOffset( 100, 50 );
        pointSlopeParent.setOffset( slopeInterceptParent.getFullBoundsReference().getMaxX() + xSpacing, slopeInterceptParent.getYOffset() );

        // frame
        JFrame frame = new JFrame( TestLineEquations.class.getName() );
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
