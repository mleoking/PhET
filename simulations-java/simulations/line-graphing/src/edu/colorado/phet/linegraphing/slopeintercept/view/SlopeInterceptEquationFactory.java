// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.Color;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.colorado.phet.linegraphing.common.view.StaticEquationNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Factory that creates line equations in slope-intercept equation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptEquationFactory extends EquationFactory {

    public StaticEquationNode createNode( Line line, PhetFont font, Color color ) {
        assert ( line.x1 == 0 ); // line is in slope-intercept form
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

    // Verbose form of slope-intercept, not simplified, for debugging.
    private static class VerboseNode extends StaticEquationNode {
        public VerboseNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );
            addChild( new PhetPText( MessageFormat.format( "y = ({0}/{1})x + {2})", line.rise, line.run, line.y1 ), font, color ) );
        }
    }

    /*
     * Forms where slope is zero.
     * y = b
     * y = -b
     */
    private static class ZeroSlopeNode extends StaticEquationNode {

        public ZeroSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( line.rise == 0 );

            // y = b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode interceptSignNode = createSignNode( line.y1, color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.y1 < 0 ) {
                addChild( interceptSignNode );
            }
            addChild( interceptNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( line.y1 < 0 ) {
                interceptSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                             equalsNode.getFullBoundsReference().getCenterY() - ( interceptSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, equalsNode.getYOffset() );
            }
            else {
                interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, equalsNode.getYOffset() );
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
    private static class UnitSlopeNode extends StaticEquationNode {

        public UnitSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( Math.abs( line.rise ) == Math.abs( line.run ) );

            // y = x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, color );
            PNode operatorNode = createOperatorNode( line.y1, color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
                addChild( slopeSignNode );
            }
            addChild( xNode );
            if ( line.y1 != 0 ) {
                addChild( operatorNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( line.getSlope() < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                xNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, yNode.getYOffset() );
            }
            else {
                xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            }
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing, yNode.getYOffset() );
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
    private static class IntegerSlopeNode extends StaticEquationNode {

        public IntegerSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            assert ( Math.abs( line.run ) == 1 );

            // y = rise x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.getSlope() ) ), font, color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, color );
            PNode operatorNode = createOperatorNode( line.y1, color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
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
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            if ( line.getSlope() < 0 ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing,
                                         equalsNode.getFullBoundsReference().getCenterY() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + slopeSignYFudgeFactor );
                riseNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + integerSignXSpacing, yNode.getYOffset() );
            }
            else {
                riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
            }
            xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + slopeXSpacing, yNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing, yNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * y = (rise/run) x + b
    * y = (rise/run) x - b
    * y = -(rise/run) x + b
    * y = -(rise/run) x - b
    */
    private static class FractionSlopeNode extends StaticEquationNode {

        public FractionSlopeNode( Line line, PhetFont font, Color color ) {
            super( font.getSize() );

            // y = -(absRise/absRun)x + b
            PNode yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PNode equalsNode = new PhetPText( "=", font, color );
            PNode slopeSignNode = createSignNode( line.getSlope(), color );
            PNode riseNode = new PhetPText( toIntString( Math.abs( line.rise ) ), font, color );
            PNode runNode = new PhetPText( toIntString( Math.abs( line.run ) ), font, color );
            PNode lineNode = createFractionLineNode( Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getWidth() ), color );
            PNode xNode = new PhetPText( Strings.SYMBOL_X, font, color );
            PNode operatorNode = createOperatorNode( line.y1, color );
            PNode interceptNode = new PhetPText( toIntString( Math.abs( line.y1 ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( line.getSlope() < 0 ) {
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
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + relationalOperatorXSpacing, yNode.getYOffset() );
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
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + slopeXSpacing,
                             yNode.getYOffset() );
            operatorNode.setOffset( xNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( operatorNode.getFullBoundsReference().getHeight() / 2 ) + operatorYFudgeFactor );
            interceptNode.setOffset( operatorNode.getFullBoundsReference().getMaxX() + operatorXSpacing,
                                     yNode.getYOffset() );
        }
    }
}
