// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Dimension;
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
 * Factory that creates line equations in slope-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font ) {
        assert ( line.x1 == 0 ); // line is in slope-intercept form
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

    // Verbose form of slope-intercept, not reduced, for debugging.
    private static class VerboseNode extends EquationNode {
        public VerboseNode( Line line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "y = ({0}/{1})x + {2})", line.rise, line.run, line.y1 ), font, line.color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = b
     * y = -b
     */
    private static class ZeroSlopeNode extends EquationNode {

        public ZeroSlopeNode( Line line, PhetFont font ) {

            assert ( line.rise == 0 );

            // y = b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode interceptSignNode = createSignNode( line.y1, line.color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.y1 < 0 ) {
                addChild( interceptSignNode );
            }
            addChild( interceptNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( line.y1 > 0 ) {
                interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            }
            else {
                interceptSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                             equalsNode.getFullBoundsReference().getCenterY() - ( interceptSignNode.getFullBoundsReference().getHeight() / 2 ) + SIGN_Y_FUDGE_FACTOR );
                interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            }
        }
    }

    /*
     * Forms where absolute slope is 1.
     * y = x
     * y = -x
     * y = x + b
     * y = x - b
     * y = -x + b
     * y = -x - b
    */
    private static class UnitSlopeNode extends EquationNode {

        public UnitSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.rise ) == Math.abs( line.run ) );

            final double slope = ( line.rise / line.run );

            // y = x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( slope, line.color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PNode operatorNode = createOperatorNode( line.y1, line.color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( xNode );
            if ( line.y1 != 0 ) {
                addChild( operatorNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( slope > 0 ) {
                xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
            else {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SIGN_Y_FUDGE_FACTOR );
                xNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
     * Forms where the slope is an integer.
     * y = mx
     * y = -mx
     * y = mx + b
     * y = mx - b
     * y = -mx + b
     * y = -mx - b
     */
    private static class IntegerSlopeNode extends EquationNode {

        public IntegerSlopeNode( Line line, PhetFont font ) {

            assert ( Math.abs( line.run ) == 1 );

            final double slope = line.rise / line.run;

            // y = rise x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( slope, line.color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( slope ) ), font, line.color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PNode operatorNode = createOperatorNode( line.y1, line.color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( xNode );
            if ( line.y1 != 0 ) {
                addChild( operatorNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( slope > 0 ) {
                riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
            else {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SIGN_Y_FUDGE_FACTOR );
                riseNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            }
            xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * y = (rise/run) x + b
    * y = (rise/run) x - b
    * y = -(rise/run) x + b
    * y = -(rise/run) x - b
    */
    private static class FractionSlopeNode extends EquationNode {

        public FractionSlopeNode( Line line, PhetFont font ) {

            final double slope = line.rise / line.run;

            // y = -(absRise/absRun)x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PNode equalsNode = new PhetPText( "=", font, line.color );
            PNode slopeSignNode = createSignNode( slope, line.color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.rise ) ), font, line.color );
            PNode runNode = new PhetPText( toIntString( Math.abs( line.run ) ), font, line.color );
            PNode lineNode = createFractionLineNode( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), line.color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PNode operatorNode = createOperatorNode( line.y1, line.color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( slope < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            if ( line.y1 != 0 ) {
                addChild( operatorNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( slope > 0 ) {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + FRACTION_LINE_Y_FUDGE_FACTOR );
            }
            else {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + SIGN_Y_FUDGE_FACTOR + SLOPE_SIGN_Y_OFFSET );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + FRACTION_LINE_Y_FUDGE_FACTOR );

            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - Y_SPACING );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + X_SPACING,
                             yNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + OPERATOR_Y_FUDGE_FACTOR );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                     yNode.getYOffset() );
        }
    }

    // test
    public static void main( String[] args ) {

        // factory
        SlopeInterceptEquationFactory factory = new SlopeInterceptEquationFactory();
        PhetFont font = new PhetFont( 24 );

        // nodes
        PNode undefinedSlopeNode = factory.createNode( Line.createSlopeIntercept( 1, 0, 1 ), font );
        PNode positiveZeroSlopeNode = factory.createNode( Line.createSlopeIntercept( 0, 1, 2 ), font );
        PNode negativeZeroSlopeNode = factory.createNode( Line.createSlopeIntercept( 0, 1, -2 ), font );
        PNode positiveUnitSlopeNode = factory.createNode( Line.createSlopeIntercept( 3, 3, 2 ), font );
        PNode negativeUnitSlopeNode = factory.createNode( Line.createSlopeIntercept( -3, 3, -2 ), font );
        PNode positiveIntegerSlopeNode = factory.createNode( Line.createSlopeIntercept( 3, 1, 2 ), font );
        PNode negativeIntegerSlopeNode = factory.createNode( Line.createSlopeIntercept( -3, 1, -2 ), font );
        PNode positiveFractionSlopeNode = factory.createNode( Line.createSlopeIntercept( 3, 5, 2 ), font );
        PNode negativeFractionSlopeNode = factory.createNode( Line.createSlopeIntercept( -3, 5, -2 ), font );

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
