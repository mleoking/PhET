// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.linegraphing.LGResources.Strings;
import edu.colorado.phet.linegraphing.intro.model.SlopeInterceptLine;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays a slope-intercept equation in reduced form.
 * <p>
 * Possible forms:
 * <ul>
 * <li>slope undefined
 * <li>y = b
 * <li>y = -b
 * <li>y = x + b
 * <li>y = x - b
 * <li>y = rise x + b
 * <li>y = rise x - b
 * <li>y = -rise x + b
 * <li>y = -rise x - b
 * <li>y = (rise/run) x + b
 * <li>y = (rise/run) x - b
 * <li>y = -(rise/run) x + b
 * <li>y = -(rise/run) x - b
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ReducedSlopeInterceptEquationNode extends PComposite {

    private final PNode parentNode; // parent of all components of the equation

    public ReducedSlopeInterceptEquationNode( SlopeInterceptLine line, Color color, PhetFont font ) {
        setPickable( false );

        parentNode = new PNode();

        final double xSpacing = 3;
        final double ySpacing = 0;

        if ( MathUtil.round( line.run ) == 0 ) {
            parentNode.addChild( new PhetPText( Strings.SLOPE_UNDEFINED, font, color ) );
        }
        else if ( MathUtil.round( line.rise ) == 0 ) {

            // y = b
            PText yString = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsString = new PhetPText( "=", font, color );
            PText interceptString = new PhetPText( String.valueOf( MathUtil.round( line.intercept ) ), font, color );

            // rendering order
            parentNode.addChild( yString );
            parentNode.addChild( equalsString );
            parentNode.addChild( interceptString );

            // layout
            yString.setOffset( 0, 0 );
            equalsString.setOffset( yString.getFullBoundsReference().getWidth() + xSpacing, yString.getYOffset() );
            interceptString.setOffset( equalsString.getFullBoundsReference().getMaxX() + xSpacing,
                                       equalsString.getYOffset() );
        }
        else {

            final int reducedRise = line.getReducedRise();
            final int reducedRun = line.getReducedRun();
            final boolean slopeIsPositive = ( reducedRise * reducedRun ) > 0;

            // y = -(reducedRise/reducedRun)x + b
            PText yString = new PhetPText( Strings.SYMBOL_Y, font, color );
            PText equalsString = new PhetPText( "=", font, color );
            PText signString = new PhetPText( slopeIsPositive ? "" : "-", font, color );
            PText riseString = new PhetPText( String.valueOf( Math.abs( reducedRise ) ), font, color );
            PText runString = new PhetPText( String.valueOf( Math.abs( reducedRun ) ), font, color );
            PPath lineNode = new PhetPPath( new Line2D.Double( 0, 0, Math.max( riseString.getFullBoundsReference().getWidth(), runString.getFullBoundsReference().getHeight() ), 0 ), new BasicStroke( 1f ), color );
            PText xString = new PhetPText( Strings.SYMBOL_X, font, color );
            PText operatorString = new PhetPText( line.intercept > 0 ? "+" : "-", font, color );
            PText interceptString = new PhetPText( String.valueOf( MathUtil.round( Math.abs( line.intercept ) ) ), font, color );

            // rendering order
            parentNode.addChild( yString );
            parentNode.addChild( equalsString );
            if ( !slopeIsPositive ) {
                parentNode.addChild( signString );
            }
            if ( !( reducedRun == 1 && reducedRise == 1 ) ) {
                parentNode.addChild( riseString );
            }
            if ( reducedRun != 1 ) {
                parentNode.addChild( lineNode );
                parentNode.addChild( runString );
            }
            parentNode.addChild( xString );
            if ( line.intercept != 0 ) {
                parentNode.addChild( operatorString );
                parentNode.addChild( interceptString );
            }

            // layout
            yString.setOffset( 0, 0 );
            equalsString.setOffset( yString.getFullBoundsReference().getWidth() + xSpacing, yString.getYOffset() );
            if ( !slopeIsPositive ) {
                signString.setOffset( equalsString.getFullBoundsReference().getMaxX() + xSpacing,
                                      equalsString.getYOffset() );
                lineNode.setOffset( signString.getFullBoundsReference().getMaxX() + xSpacing,
                                    equalsString.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            else {
                lineNode.setOffset( equalsString.getFullBoundsReference().getMaxX() + xSpacing,
                                    equalsString.getFullBoundsReference().getCenterY() - ( lineNode.getFullBoundsReference().getHeight() / 2 ) );
            }
            if ( reducedRun != 1 ) {
                riseString.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseString.getFullBoundsReference().getWidth() / 2 ),
                                      lineNode.getFullBoundsReference().getMinY() - riseString.getFullBoundsReference().getHeight() - ySpacing );
            }
            else {
                riseString.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( riseString.getFullBoundsReference().getWidth() / 2 ),
                                      equalsString.getYOffset() );
            }
            runString.setOffset( lineNode.getFullBoundsReference().getCenterX() - ( runString.getFullBoundsReference().getWidth() / 2 ),
                                 lineNode.getFullBoundsReference().getMaxY() + ySpacing );
            if ( !( reducedRun == 1 && reducedRise == 1 ) ) {
                xString.setOffset( lineNode.getFullBoundsReference().getMaxX() + xSpacing,
                                   equalsString.getYOffset() );
            }
            else{
               xString.setOffset( equalsString.getFullBoundsReference().getMaxX() + xSpacing,
                                   equalsString.getYOffset() );
            }
            operatorString.setOffset( xString.getFullBoundsReference().getMaxX() + xSpacing,
                                      equalsString.getYOffset() );
            interceptString.setOffset( operatorString.getFullBoundsReference().getMaxX() + xSpacing,
                                       equalsString.getYOffset() );
        }

        addChild( new ZeroOffsetNode( parentNode ) ); // do this after adding all nodes and layout, or ZeroOffsetNode will be ineffective
    }

    // Changes the color of the equation
    public void setEquationColor( Color color ) {
        for ( int i = 0; i < parentNode.getChildrenCount(); i++ ) {
            PNode child = parentNode.getChild( i );
            if ( child instanceof PText ) {
                ( (PText) child ).setTextPaint( color );
            }
            else if ( child instanceof PPath ) {
                ( (PPath) child ).setStrokePaint( color );
            }
        }
    }
}
