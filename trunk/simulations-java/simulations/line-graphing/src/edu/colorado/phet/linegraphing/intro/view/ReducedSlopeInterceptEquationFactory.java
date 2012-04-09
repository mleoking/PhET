// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Factory that creates a node for displaying a slope-intercept equation in reduced form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ReducedSlopeInterceptEquationFactory {

    // not intended for instantiation
    private ReducedSlopeInterceptEquationFactory() {}

    public static ReducedSlopeInterceptEquationNode createNode( SlopeInterceptLine line, Color color, PhetFont font ) {
        if ( MathUtil.round( line.run ) == 0 ) {
            return new SlopeUndefinedNode( line, color, font );
        }
        else if ( MathUtil.round( line.rise ) == 0 ) {
            return new SlopeZeroNode( line, color, font );
        }
        else if ( Math.abs( line.getReducedRise() ) == Math.abs( line.getReducedRun() ) ) {
            return new SlopeOneNode( line, color, font );
        }
        else if ( Math.abs( line.getReducedRun() ) == 1 ) {
            return new SlopeIntegerNode( line, color, font );
        }
        else {
            return new SlopeFractionFraction( line, color, font );
        }
    }

    /*
     * Base class for all forms of the equation.
     */
    public static abstract class ReducedSlopeInterceptEquationNode extends PComposite {

        protected static final double X_SPACING = 3;
        protected static final double Y_SPACING = 0;

        public ReducedSlopeInterceptEquationNode() {
            setPickable( false );
        }

        // Changes the color of the equation
        public void setEquationColor( Color color ) {
            for ( int i = 0; i < getChildrenCount(); i++ ) {
                PNode child = getChild( i );
                if ( child instanceof PText ) {
                    ( (PText) child ).setTextPaint( color );
                }
                else if ( child instanceof PPath ) {
                    ( (PPath) child ).setStrokePaint( color );
                }
            }
        }
    }

    /*
     * Slope is undefined.
     */
    private static class SlopeUndefinedNode extends ReducedSlopeInterceptEquationNode {
        public SlopeUndefinedNode( SlopeInterceptLine line, Color color, PhetFont font ) {
            setPickable( false );
            addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, color ) );
        }
    }

    /*
     * Forms when slope is zero.
     * y = b
     * y = -b
     */
    private static class SlopeZeroNode extends ReducedSlopeInterceptEquationNode {

        public SlopeZeroNode( SlopeInterceptLine line, Color color, PhetFont font ) {
            setPickable( false );

            // y = b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsNode = new PhetPText( "=", font, color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( line.intercept ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( interceptNode );

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
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
    private static class SlopeOneNode extends ReducedSlopeInterceptEquationNode {

        public SlopeOneNode( SlopeInterceptLine line, Color color, PhetFont font ) {

            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsNode = new PhetPText( "=", font, color );
            PText xNode = new PhetPText( slopeIsPositive ? Strings.SYMBOL_X : "-" + Strings.SYMBOL_X, font, color );
            PText interceptSignNode = new PhetPText( line.intercept > 0 ? "+" : "-", font, color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.intercept ) ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( xNode );
            if ( line.intercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            xNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptSignNode.setOffset( xNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
            interceptNode.setOffset( interceptSignNode.getFullBoundsReference().getMaxX() + X_SPACING, equalsNode.getYOffset() );
        }
    }

    /*
     * Forms where the slope is an integer.
     * y = rise x
     * y = -rise x
     * y = rise x + b
     * y = rise x - b
     * y = -rise x + b
     * y = -rise x - b
     */
    private static class SlopeIntegerNode extends ReducedSlopeInterceptEquationNode {

        public SlopeIntegerNode( SlopeInterceptLine line, Color color, PhetFont font ) {

            // y = rise x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsNode = new PhetPText( "=", font, color );
            PText riseNode = new PhetPText( String.valueOf( line.getReducedRise() ), font, color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, color );
            PText signNode = new PhetPText( line.intercept > 0 ? "+" : "-", font, color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.intercept ) ) ), font, color );

            // rendering order
            addChild( yNode );
            addChild( equalsNode );
            addChild( riseNode );
            addChild( xNode );
            if ( line.intercept != 0 ) {
                addChild( signNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
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
    private static class SlopeFractionFraction extends ReducedSlopeInterceptEquationNode {

        public SlopeFractionFraction( SlopeInterceptLine line, Color color, PhetFont font ) {

            final int reducedRise = Math.abs( line.getReducedRise() );
            final int reducedRun = Math.abs( line.getReducedRun() );
            final boolean slopeIsPositive = ( line.rise * line.run ) >= 0;

            // y = -(reducedRise/reducedRun)x + b
            PText yNode = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsNode = new PhetPText( "=", font, color );
            PText slopeSignNode = new PhetPText( slopeIsPositive ? "" : "-", font, color );
            PText riseNode = new PhetPText( String.valueOf( Math.abs( reducedRise ) ), font, color );
            PText runNode = new PhetPText( String.valueOf( Math.abs( reducedRun ) ), font, color );
            PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseNode.getFullBoundsReference().getWidth(), runNode.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( 1f ), color );
            PText xNode = new PhetPText( Strings.SYMBOL_X, font, color );
            PText interceptSignNode = new PhetPText( line.intercept > 0 ? "+" : "-", font, color );
            PText interceptNode = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.intercept ) ) ), font, color );

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
            if ( line.intercept != 0 ) {
                addChild( interceptSignNode );
                addChild( interceptNode );
            }

            // layout
            yNode.setOffset( 0, 0 );
            equalsNode.setOffset( yNode.getFullBoundsReference().getWidth() + X_SPACING, yNode.getYOffset() );
            if ( !slopeIsPositive ) {
                slopeSignNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                         equalsNode.getYOffset() );
                lineNode.setOffset( slopeSignNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                lineNode.setOffset( equalsNode.getFullBoundsReference().getMaxX() + X_SPACING,
                                    equalsNode.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) );
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
