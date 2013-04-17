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
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeEquationNode;
import edu.colorado.phet.linegraphing.slope.view.SlopeEquationNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptEquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Tests the equations used in this simulation, in their various interactive and non-interactive configurations.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestEquationNodes {

    public static void main( String[] args ) {

        PhetFont interactiveFont = new PhetFont( Font.BOLD, 28 );
        final PhetFont staticFont = new PhetFont( Font.PLAIN, interactiveFont.getSize() );
        final Color staticColor = Color.BLACK;

        DoubleRange range = new DoubleRange( -10, 10 );
        Property<DoubleRange> x1Range = new Property<DoubleRange>( range );
        Property<DoubleRange> y1Range = new Property<DoubleRange>( range );
        Property<DoubleRange> riseRange = new Property<DoubleRange>( range );
        Property<DoubleRange> runRange = new Property<DoubleRange>( range );

        final int xSpacing = 100;
        final int ySpacing = 60;

        // point-slope tests
        PNode pointSlopeParent = new PNode();
        {
            // all equations synchronize with this line
            Property<Line> line = new Property<Line>( Line.createPointSlope( 1, 2, 3, 4, LGColors.INTERACTIVE_LINE ) );

            // equations, various combinations of interactivity
            PointSlopeEquationNode equationNode1 = new PointSlopeEquationNode( line, x1Range, y1Range, riseRange, runRange, true, true, true, interactiveFont, staticFont, staticColor );
            PointSlopeEquationNode equationNode2 = new PointSlopeEquationNode( line, x1Range, y1Range, riseRange, runRange, false, false, true, interactiveFont, staticFont, staticColor );
            PointSlopeEquationNode equationNode3 = new PointSlopeEquationNode( line, x1Range, y1Range, riseRange, runRange, true, true, false, interactiveFont, staticFont, staticColor );
            PointSlopeEquationNode equationNode4 = new PointSlopeEquationNode( line, x1Range, y1Range, riseRange, runRange, false, false, false, interactiveFont, staticFont, staticColor );
            // add a non-interactive slope-intercept equation, to demonstrate fractional y-intercept
            SlopeInterceptEquationNode equationNode5 = new SlopeInterceptEquationNode( line, riseRange, runRange, y1Range, false, false, interactiveFont, staticFont, staticColor );

            // rendering order
            pointSlopeParent.addChild( equationNode1 );
            pointSlopeParent.addChild( equationNode2 );
            pointSlopeParent.addChild( equationNode3 );
            pointSlopeParent.addChild( equationNode4 );
            pointSlopeParent.addChild( equationNode5 );

            // layout
            equationNode1.setOffset( 0, 0 );
            equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode3.setOffset( equationNode1.getXOffset(), equationNode2.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode4.setOffset( equationNode1.getXOffset(), equationNode3.getFullBoundsReference().getMaxY() + ySpacing );
            equationNode5.setOffset( equationNode1.getXOffset(), equationNode4.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // slope-intercept tests
        PNode slopeInterceptParent = new PNode();
        {
            // all equations synchronize with this line
            Property<Line> line = new Property<Line>( Line.createSlopeIntercept( 1, 1, 1, LGColors.INTERACTIVE_LINE ) );

            // equations, various combinations of interactivity
            SlopeInterceptEquationNode equationNode1 = new SlopeInterceptEquationNode( line, riseRange, runRange, y1Range, true, true, interactiveFont, staticFont, staticColor );
            SlopeInterceptEquationNode equationNode2 = new SlopeInterceptEquationNode( line, riseRange, runRange, y1Range, true, false, interactiveFont, staticFont, staticColor );
            SlopeInterceptEquationNode equationNode3 = new SlopeInterceptEquationNode( line, riseRange, runRange, y1Range, false, true, interactiveFont, staticFont, staticColor );
            SlopeInterceptEquationNode equationNode4 = new SlopeInterceptEquationNode( line, riseRange, runRange, y1Range, false, false, interactiveFont, staticFont, staticColor );

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

        // slope tests
        PNode slopeParent = new PNode();
        {
            // all equations synchronize with this line
            Property<Line> line = new Property<Line>( new Line( 1, 3, 2, 4, LGColors.INTERACTIVE_LINE ) );

            // equations, interactive and non-interactive
            SlopeEquationNode equationNode1 = new SlopeEquationNode( line, x1Range, y1Range, interactiveFont, staticFont, staticColor );
            SlopeEquationNode equationNode2 = new SlopeEquationNode( line, staticFont, staticColor );

            // rendering order
            slopeParent.addChild( equationNode1 );
            slopeParent.addChild( equationNode2 );

            // layout
            equationNode1.setOffset( 0, 0 );
            equationNode2.setOffset( equationNode1.getXOffset(), equationNode1.getFullBoundsReference().getMaxY() + ySpacing );
        }

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 1024, 768 ) );
        canvas.getLayer().addChild( pointSlopeParent );
        canvas.getLayer().addChild( slopeInterceptParent );
        canvas.getLayer().addChild( slopeParent );

        // layout
        pointSlopeParent.setOffset( 50, 50 );
        slopeInterceptParent.setOffset( pointSlopeParent.getFullBoundsReference().getMaxX() + xSpacing, pointSlopeParent.getYOffset() );
        slopeParent.setOffset( slopeInterceptParent.getXOffset(), slopeInterceptParent.getFullBoundsReference().getMaxY() + 75 );

        // frame
        JFrame frame = new JFrame( TestEquationNodes.class.getName() );
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
