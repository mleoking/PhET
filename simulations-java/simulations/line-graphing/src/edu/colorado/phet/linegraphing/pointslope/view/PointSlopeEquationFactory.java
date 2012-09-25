// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

import java.awt.Color;
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
 * Factory that creates line equations in point-slope form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointSlopeEquationFactory extends EquationFactory {

    public EquationNode createNode( Line line, PhetFont font, Color color ) {
        if ( line.run == 0 ) {
            return new UndefinedSlopeNode( line, font, color );
        }
        else if ( line.rise == 0 ) {
            return new ZeroSlopeNode( line, font, color );
        }
        else if ( Math.abs( line.rise ) == Math.abs( line.run ) ) {
            return new UnitSlopeNode( line, font, color );
        }
        else if ( Math.abs( line.run ) == 1 ) {
            return new IntegerSlopeNode( line, font, color );
        }
        else {
            return new FractionSlopeNode( line, font, color );
        }
    }

    // Verbose form of point-slope, not simplified, for debugging.
    private static class VerboseNode extends EquationNode {
        public VerboseNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );
            addChild( new PhetPText( MessageFormat.format( "(y - {0}) = ({1}/{2})(x - {3})", line.y1, line.rise, line.run, line.x1 ), font, color ) );
        }
    }

    /*
     * Forms where slope is zero.
     * y = y1
     * y = -y1
     */
    private static class ZeroSlopeNode extends EquationNode {

        public ZeroSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( line.rise == 0 );

            // nodes
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode y1SignNode = createSignNode( line.y1, color );
            PNode y1Node = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.y1 < 0 ) {
                addChild( y1SignNode );
            }
            addChild( y1Node );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( line.y1 < 0 ) {
                y1SignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                      equalsNode.getFullBoundsReference().getCenterY() - ( y1SignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                y1Node.setOffset( y1SignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, yNode.getYOffset() );
            }
            else {
                y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            }
        }
    }

    /*
     * Forms where absolute slope is 1.
     * (y - y1) = (x - x1)
     * (y - y1) = -(x - x1)
     */
    private static class UnitSlopeNode extends EquationNode {

        public UnitSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( Math.abs( line.rise ) == Math.abs( line.run ) );

            // nodes
            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yTermNode.getYOffset() );
            if ( line.getSlope() < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                xTermNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, yTermNode.getYOffset() );
            }
            else {
                xTermNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yTermNode.getYOffset() );
            }
        }
    }

    /*
     * Forms where the slope is an integer.
     * (y - y1) = m(x - x1)
     * (y - y1) = -m(x - x1)
     */
    private static class IntegerSlopeNode extends EquationNode {

        public IntegerSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( Math.abs( line.run ) == 1 );

            // nodes
            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode slopeNode = new PhetPText( toIntString( Math.abs( line.getSlope() ) ), font, color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( slopeNode );
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yTermNode.getYOffset() );
            if ( line.getSlope() < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                slopeNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, yTermNode.getYOffset() );
            }
            else {
                slopeNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yTermNode.getYOffset() );
            }
            xTermNode.setOffset( slopeNode.getFullBoundsReference().getMaxX() + slopeXSpacing, yTermNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * (y - y1) = (rise/run)(x - x1)
    * (y - y1) = -(rise/run)(x - x1)
    */
    private static class FractionSlopeNode extends EquationNode {

        public FractionSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            // nodes
            PNode yTermNode = createTermNode( line.y1, Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.rise ) ), font, color );
            PNode runNode = new PhetPText( toIntString( Math.abs( line.run ) ), font, color );
            PNode lineNode = createFractionLineNode( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), color );
            PNode xTermNode = createTermNode( line.x1, Strings.SYMBOL_X, font, color );

            // rendering order
            addChild( yTermNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xTermNode );

            // layout
            yTermNode.setOffset( 0, 0 );
            equalsNode.setOffset( yTermNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yTermNode.getYOffset() );
            if ( line.getSlope() < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor + slopeSignYOffset );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + fractionSignXSpacing,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + fractionLineYFudgeFactor );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + fractionLineYFudgeFactor );
            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - ySpacing );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + ySpacing );
            xTermNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + slopeXSpacing,
                                 yTermNode.getYOffset() );
        }
    }

    // test
    public static void main( String[] args ) {

        // factory
        PointSlopeEquationFactory factory = new PointSlopeEquationFactory();
        PhetFont font = new PhetFont( 24 );
        Color color = Color.BLACK;

        // nodes
        PNode undefinedSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 1, 0 ), font, color );
        PNode positiveZeroSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 0, 1 ), font, color );
        PNode negativeZeroSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, 0, 1 ), font, color );
        PNode positiveUnitSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 3 ), font, color );
        PNode negativeUnitSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 3 ), font, color );
        PNode positiveIntegerSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 1 ), font, color );
        PNode negativeIntegerSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 1 ), font, color );
        PNode positiveFractionSlopeNode = factory.createNode( Line.createPointSlope( 1, 2, 3, 5 ), font, color );
        PNode negativeFractionSlopeNode = factory.createNode( Line.createPointSlope( -1, -2, -3, 5 ), font, color );

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
