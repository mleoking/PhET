// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.MessageFormat;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Factory that creates line equations in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font ) {
        if ( line.run == 0 ) {
            return new UndefinedSlopeNode( line, font );
        }
        else if ( line.rise == 0 ) {
            return new ZeroSlopeNode( line, font );
        }
        else if ( Math.abs( line.rise ) == Math.abs( line.run ) ) {
            return new UnitSlopeNode( line, font );
        }
        else if ( Math.abs( line.run ) == 1 ) {
            return new IntegerSlopeNode( line, font );
        }
        else {
            return new FractionSlopeNode( line, font );
        }
    }

    // Verbose form of point-slope, not reduced, for debugging.
    private static class VerboseNode extends EquationNode {
        public VerboseNode( Line line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "(y - {0}) = ({1}/{2})(x - {3})", line.y1, line.rise, line.run, line.x1 ), font, line.color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = y1
     */
    private static class ZeroSlopeNode extends EquationNode {

        public ZeroSlopeNode( Line line, PhetFont font ) {

            assert ( line.rise == 0 );

            // y = y1
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode y1SignNode = createSignNode( line.y1, line.color );
            PNode y1Node = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.y1 < 0 ) {
                addChild( y1SignNode );
            }
            addChild( y1Node );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( line.y1 < 0 ) {
                y1SignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                      equalsNode.getFullBoundsReference().getCenterY() - ( y1SignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR );
                y1Node.setOffset( y1SignNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
            else {
                y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
        }
    }

    /*
     * Forms where absolute slope is 1.
     * (y - y1) = (x - x1)
     * (y - y1) = -(x - x1)
     */
    private static class UnitSlopeNode extends EquationNode {

        public UnitSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.rise ) == Math.abs( line.run ) );

            final double slope = line.rise / line.run;

            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( line.rise / line.run, line.color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, line.color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            if ( slope < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_OFFSET );
                xTermNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            }
            else {
                xTermNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            }
        }
    }

    /*
     * Forms where the slope is an integer.
     * (y - y1) = m(x - x1)
     * (y - y1) = -m(x - x1)
     */
    private static class IntegerSlopeNode extends EquationNode {

        public IntegerSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.run ) == 1 );

            final double slope = line.rise / line.run;

            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( slope, line.color );
            PNode slopeNode = new PhetPText( toIntString( Math.abs( slope ) ), font, line.color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, line.color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( slopeNode );
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            if ( slope < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_OFFSET );
                slopeNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            }
            else {
                slopeNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            }
            xTermNode.setOffset( slopeNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * (y - y1) = (rise/run)(x - x1)
    * (y - y1) = -(rise/run)(x - x1)
    */
    private static class FractionSlopeNode extends EquationNode {

        public FractionSlopeNode( Line line, PhetFont font ) {

            final double slope = line.rise / line.run;

            // y = -(reducedRise/reducedRun)x + b
            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( slope, line.color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.rise ) ), font, line.color );
            PNode runNode = new PhetPText( toIntString( Math.abs( line.run ) ), font, line.color );
            PNode lineNode = createFractionLineNode( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), line.color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, line.color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + X_SPACING, yTermNode.getYOffset() );
            if ( slope < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SLOPE_SIGN_Y_FUDGE_FACTOR + SLOPE_SIGN_Y_OFFSET );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + FRACTION_LINE_Y_FUDGE_FACTOR );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + FRACTION_LINE_Y_FUDGE_FACTOR );
            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - Y_SPACING );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            xTermNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                 yTermNode.getYOffset() );
        }
    }

    // Creates the portion of the equation that contains the x or y term.
    private static PNode createTermNode( double value, String symbol, Font font, Color color ) {
        if ( value == 0 ) {
            // x or y
            return new PhetPText( symbol, font, color );
        }
        else {
            // (x-x1) or (y-y1)
            PNode leftParenNode = new PhetPText( "(", font, color );
            PNode xNode = new PhetPText( symbol, font, color );
            PNode operatorNode = createOperatorNode( -value, color ); // flip sign on x1
            PNode x1Node = new PhetPText( toIntString( Math.abs( value ) ), font, color );
            PNode rightParenNode = new PhetPText( ")", font, color );

            PNode parentNode = new PNode();
            parentNode.addChild( leftParenNode );
            parentNode.addChild( xNode );
            parentNode.addChild( operatorNode );
            parentNode.addChild( x1Node );
            parentNode.addChild( rightParenNode );

            // layout
            leftParenNode.setOffset( 0, 0 );
            xNode.setOffset( leftParenNode.getFullBoundsReference().getMaxX() + X_SPACING, leftParenNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    leftParenNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
            x1Node.setOffset( operatorNode.getFullBoundsReference().getMaxX() + X_SPACING, leftParenNode.getYOffset() );
            rightParenNode.setOffset( x1Node.getFullBoundsReference().getMaxX() + X_SPACING, leftParenNode.getYOffset() );

            return parentNode;
        }
    }

    // test
    public static void main( String[] args ) {

        // factory
        PointSlopeEquationFactory factory = new PointSlopeEquationFactory();
        PhetFont font = new PhetFont( 24 );

        // nodes
        PNode undefinedSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 1, 0 ), font );
        PNode positiveZeroSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 0, 1 ), font );
        PNode negativeZeroSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, 0, 1 ), font );
        PNode positiveUnitSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 3 ), font );
        PNode negativeUnitSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 3 ), font );
        PNode positiveIntegerSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 1 ), font );
        PNode negativeIntegerSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 1 ), font );
        PNode positiveFractionSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 5 ), font );
        PNode negativeFractionSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 5 ), font );

        // layout
        final double xOffset = 50;
        final double ySpacing = 15;
        undefinedSlopeNode.setOffset( xOffset, 50 );
        positiveZeroSlopeNode.setOffset( xOffset, undefinedSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeZeroSlopeNode.setOffset( xOffset, positiveZeroSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveUnitSlopeNode.setOffset( xOffset, negativeZeroSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeUnitSlopeNode.setOffset( xOffset, positiveUnitSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveIntegerSlopeNode.setOffset( xOffset, negativeUnitSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeIntegerSlopeNode.setOffset( xOffset, positiveIntegerSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        positiveFractionSlopeNode.setOffset( xOffset, negativeIntegerSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );
        negativeFractionSlopeNode.setOffset( xOffset, positiveFractionSlopeNode.getFullBoundsReference().getMaxY() + ySpacing );

        // canvas
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( new Dimension( 800, 600 ) );
        canvas.getLayer().addChild( undefinedSlopeNode );
        canvas.getLayer().addChild( positiveZeroSlopeNode );
        canvas.getLayer().addChild( negativeZeroSlopeNode );
        canvas.getLayer().addChild( positiveUnitSlopeNode );
        canvas.getLayer().addChild( negativeUnitSlopeNode );
        canvas.getLayer().addChild( positiveIntegerSlopeNode );
        canvas.getLayer().addChild( negativeIntegerSlopeNode );
        canvas.getLayer().addChild( positiveFractionSlopeNode );
        canvas.getLayer().addChild( negativeFractionSlopeNode );

        // frame
        JFrame frame = new JFrame();
        frame.setContentPane( canvas );
        frame.pack();
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
