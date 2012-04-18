package edu.colorado.phet.functionalscenegraph;

import fj.data.Option;
import lombok.Data;

import java.awt.Font;
import java.awt.Paint;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class DrawText extends SNode {
    public final String text;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        graphics2D.drawString( text, 0, 0 );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        return new ImmutableRectangle2D( toTextLayout( mockState ).getBounds() );
    }

    @Override protected Option<? extends SNode> pick( final Vector2D vector2D, final MockState mockState ) {
        return toTextLayout( mockState ).getOutline( new AffineTransform() ).contains( vector2D.toPoint2D() ) ? Option.some( this ) : Option.<SNode>none();
    }

    private TextLayout toTextLayout( final GraphicsContext mockState ) {return new TextLayout( text, mockState.getFont(), mockState.getFontRenderContext() );}

    public static SNode textNode( String text, Font font, Paint paint ) {
        final WithFont textNode = new WithFont( font, new WithPaint( paint, new DrawText( text ) ) );

        //Put origin at top left
        return textNode.translate( -textNode.getBounds().x, -textNode.getBounds().y );
    }
}