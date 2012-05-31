// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.slopeintercept.view;

import java.awt.BasicStroke;
import java.awt.geom.Line2D;
import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.common.LGResources.Strings;
import edu.colorado.phet.linegraphing.common.model.StraightLine;
import edu.colorado.phet.linegraphing.common.view.EquationNode;
import edu.colorado.phet.linegraphing.common.view.EquationFactory;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Factory that creates a node for displaying a slope-intercept equation in simplified form.
 * Simplifications include:
 * <li>
 * <ul>slope, eg 6/8 -> 3/4
 * </li>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class SlopeInterceptEquationFactory extends EquationFactory {

    public EquationNode createNode( StraightLine line, PhetFont font ) {
        if ( MathUtil.round( line.run ) == 0 ) {
            return new UndefinedSlopeNode( line, font );
        }
        else if ( MathUtil.round( line.rise ) == 0 ) {
            return new ZeroSlopeNode( line, font );
        }
        else if ( Math.abs( line.getReducedRise() ) == Math.abs( line.getReducedRun() ) ) {
            return new UnitSlopeNode( line, font );
        }
        else if ( Math.abs( line.getReducedRun() ) == 1 ) {
            return new IntegerSlopeNode( line, font );
        }
        else {
            return new FractionSlopeNode( line, font );
        }
    }

    // Verbose form of slope-intercept, not reduced, for debugging.
    private static class VerboseNode extends ReducedEquationNode {
        public VerboseNode( StraightLine line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "y = ({0}/{1})x + {2})", line.rise, line.run, line.yIntercept ), font, line.color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = b
     * y = -b
     */
    private static class ZeroSlopeNode extends ReducedEquationNode {

        public ZeroSlopeNode( StraightLine line, PhetFont font ) {

            // y = b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( line.yIntercept ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( interceptNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            interceptNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    /*
     * Forms where abs slope is 1.
     * y = x
     * y = -x
     * y = x + b
     * y = x - b
     * y = -x + b
     * y = -x - b
    */
    private static class UnitSlopeNode extends ReducedEquationNode {

        public UnitSlopeNode( StraightLine line, PhetFont font ) {

            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText xNode = new PhetPText( slopeIsPositive ? Strings.SYMBOL_X : "-" + Strings.SYMBOL_X, font, line.color );
            PText interceptSignNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
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
    private static class IntegerSlopeNode extends ReducedEquationNode {

        public IntegerSlopeNode( StraightLine line, PhetFont font ) {

            // y = rise x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText riseNode = new PhetPText( String.valueOf( line.getReducedRise() / line.getReducedRun() ), font, line.color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PText signNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( signNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            riseNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            xNode.setOffset( riseNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            signNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptNode.setOffset( signNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * y = (rise/run) x + b
    * y = (rise/run) x - b
    * y = -(rise/run) x + b
    * y = -(rise/run) x - b
    */
    private static class FractionSlopeNode extends ReducedEquationNode {

        public FractionSlopeNode( StraightLine line, PhetFont font ) {

            final int reducedRise = Math.abs( line.getReducedRise() );
            final int reducedRun = Math.abs( line.getReducedRun() );
            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = -(reducedRise/reducedRun)x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText slopeSignNode = new PhetPText( slopeIsPositive ? "" : "-", font, line.color );
            PText riseNode = new PhetPText( String.valueOf( Math.abs( reducedRise ) ), font, line.color );
            PText runNode = new PhetPText( String.valueOf( Math.abs( reducedRun ) ), font, line.color );
            PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( 1f ), line.color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, line.color );
            PText interceptSignNode = new PhetPText( line.yIntercept > 0 ? "+" : "-", font, line.color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.yIntercept ) ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            if ( !slopeIsPositive ) {
                addChild( slopeSignNode );
            }
            addChild( riseNode );
            addChild( lineNode );
            addChild( runNode );
            addChild( xNode );
            if ( line.yIntercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            final double yFudgeFactor = 2; // fudge factor to align fraction dividing line with the center of the equals sign, visually tweaked
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            if ( !slopeIsPositive ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() - ( slopeSignNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) + yFudgeFactor );
            }
            riseNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseNode.getFullBoundsReference().getWidth() / 2 ),
                                lineNode.getFullBoundsReference().getMinY() - riseNode.getFullBoundsReference().getHeight() - Y_SPACING );
            runNode.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runNode.getFullBoundsReference().getWidth() / 2 ),
                               lineNode.getFullBoundsReference().getMaxY() + Y_SPACING );
            xNode.setOffset( lineNode.getFullBoundsReference().getMaxX() + X_SPACING,
                             equalsNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                     equalsNode.getYOffset() );
        }
    }
}
