package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Font;
import java.awt.Paint;
import java.awt.font.TextLayout;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class DrawText extends SNode {
    public final String text;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        graphics2D.drawString( text, 0, 0 );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        TextLayout textLayout = new TextLayout( text, mockState.getFont(), mockState.getFontRenderContext() );
        return new ImmutableRectangle2D( textLayout.getBounds() );
    }

    public static SNode textNode( String text, Font font, Paint paint ) {
        final WithFont textNode = new WithFont( font, new WithPaint( paint, new DrawText( text ) ) );

        //Put origin at top left
        return textNode.translate( -textNode.getBounds().x, -textNode.getBounds().y );
    }
}