// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.pointslope.view;

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
import edu.colorado.phet.linegraphing.common.view.EquationNodeFactory;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Factory that creates a node for displaying a point-slope equation in reduced form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class PointSlopeEquationFactory extends EquationNodeFactory {

    private static final boolean SHOW_ZEROS = false; // shows values of x1 and y1 that are zero

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

    // Verbose form of point-slope, not reduced, for debugging.
    private static class VerboseNode extends ReducedEquationNode {
        public VerboseNode( StraightLine line, PhetFont font ) {
            addChild( new PhetPText( MessageFormat.format( "(y - {0}) = ({1}/{2})(x - {3})", line.y1, line.rise, line.run, line.x1 ), font, line.color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = y1
     */
    private static class ZeroSlopeNode extends ReducedEquationNode {

        public ZeroSlopeNode( StraightLine line, PhetFont font ) {

            // y = y1
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText y1Node = new PhetPText( String.valueOf( MathUtil.round( line.y1 ) ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( y1Node );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            y1Node.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
     * Forms where abs slope is 1.
     * (y - y1) = (x - x1)
     * (y - y1) = -(x - x1)
     */
    private static class UnitSlopeNode extends ReducedEquationNode {

        public UnitSlopeNode( StraightLine line, PhetFont font ) {

            PText yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            String slopeText = ( line.rise * line.run >= 0 ) ? "" : "-";
            PText xNode = new PhetPText( slopeText + getXText( line.x1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( xNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
     * Forms where the slope is an integer.
     * (y - y1) = m(x - x1)
     * (y - y1) = -m(x - x1)
     */
    private static class IntegerSlopeNode extends ReducedEquationNode {

        public IntegerSlopeNode( StraightLine line, PhetFont font ) {

            PText yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText slopeNode = new PhetPText( String.valueOf( line.getReducedRise() / line.getReducedRun() ), font, line.color );
            PText xNode = new PhetPText( getXText( line.x1 ), font, line.color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( slopeNode );
            addChild( xNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            slopeNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( slopeNode.getFullBoundsReference().getMaxX() + X_SPACING, yNode.getYOffset() );
        }
    }

    /*
    * Forms where the slope is a fraction.
    * (y - y1) = (rise/run)(x - x1)
    * (y - y1) = -(rise/run)(x - x1)
    */
    private static class FractionSlopeNode extends ReducedEquationNode {

        public FractionSlopeNode( StraightLine line, PhetFont font ) {

            final int reducedRise = Math.abs( line.getReducedRise() );
            final int reducedRun = Math.abs( line.getReducedRun() );
            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = -(reducedRise/reducedRun)x + b
            PText yNode = new PhetPText( getYText( line.y1 ), font, line.color );
            PText equalsNode = new PhetPText( "=", font, line.color );
            PText slopeSignNode = new PhetPText( slopeIsPositive ? "" : "-", font, line.color );
            PText riseNode = new PhetPText( String.valueOf( Math.abs( reducedRise ) ), font, line.color );
            PText runNode = new PhetPText( String.valueOf( Math.abs( reducedRun ) ), font, line.color );
            PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( 1f ), line.color );
            PText xNode = new PhetPText( getXText( line.x1 ), font, line.color );

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
                             yNode.getYOffset() );
        }
    }

    // (x-x1)
    private static String getXText( double x1 ) {
        if ( x1 == 0 && !SHOW_ZEROS ) {
            return MessageFormat.format( "{0}", Strings.SYMBOL_X );
        }
        else {
            return MessageFormat.format( "({0}{1}{2})", Strings.SYMBOL_X, x1 < 0 ? "+" : "-", MathUtil.round( Math.abs( x1 ) ) );
        }
    }

    // (y-y1)
    private static String getYText( double y1 ) {
        if ( y1 == 0 && !SHOW_ZEROS ) {
            return MessageFormat.format( "{0}", Strings.SYMBOL_Y );
        }
        else {
            return MessageFormat.format( "({0}{1}{2})", Strings.SYMBOL_Y, y1 < 0 ? "+" : "-", MathUtil.round( Math.abs( y1 ) ) );
        }
    }
}
