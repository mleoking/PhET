// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

import static java.awt.BasicStroke.JOIN_MITER;

/**
 * It was requested for the equals sign to have rounded edges, so we won't render with a font.
 *
 * @author Sam Reid
 */
public class EqualsSignNode extends RichPNode {
    public EqualsSignNode() {
        addChild( createPath( 0 ) );
        addChild( createPath( 22 ) );
    }

    private PhetPPath createPath( double y ) { return createSignNode( new BasicStroke( 11, BasicStroke.CAP_SQUARE, JOIN_MITER ).createStrokedShape( new Line2D.Double( 0, y, 60, y ) ) ); }

    //Encapsulates stroke, paint and stroke paint for a sign node like "=", "<", ">"
    public static PhetPPath createSignNode( Shape shape ) { return new PhetPPath( shape, Color.yellow, new BasicStroke( 2 ), Color.black ); }

    private void addSignNode( final MatchingGameState state, final PNode scales ) {
        class TextSign extends PNode {
            TextSign( String text ) {
                final PhetFont textFont = new PhetFont( 100, true );
                final PNode sign = createSignNode( textFont.createGlyphVector( new FontRenderContext( new AffineTransform(), true, true ), text ).getOutline() );
                addChild( sign );
            }
        }

        final PNode sign = state.getLeftScaleValue() < state.getRightScaleValue() ? new TextSign( "<" ) :
                           state.getLeftScaleValue() > state.getRightScaleValue() ? new TextSign( ">" ) :
                           new EqualsSignNode();
        sign.centerFullBoundsOnPoint( scales.getFullBounds().getCenter2D().getX(), scales.getFullBounds().getCenter2D().getY() + 10 );
        addChild( sign );
    }

}
