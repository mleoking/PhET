package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

import static java.awt.Color.yellow;

/**
 * Shows "less than" sign, "equals sign" or "greater than" sign after testing two fractions on the scales.
 *
 * @author Sam Reid
 */
public class SignNode extends PNode {
    public SignNode( final MatchingGameState state, final PNode scales ) {
        class TextSign extends PNode {
            TextSign( String text ) {
                final PhetFont textFont = new PhetFont( 100, true );
                addChild( getSignNode( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), text ).getOutline() ) );
            }
        }

        final PNode sign = state.getLeftScaleValue() < state.getRightScaleValue() ? new TextSign( "<" ) :
                           state.getLeftScaleValue() > state.getRightScaleValue() ? new TextSign( ">" ) :
                           new EqualsSignNode();
        sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
        addChild( sign );
    }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    private static PhetPPath getSignNode( Shape shape ) { return new PhetPPath( shape, yellow, new BasicStroke( 2 ), Color.black ); }
}