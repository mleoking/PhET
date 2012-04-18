package edu.colorado.phet.functionalscenegraph;

import fj.F;
import fj.data.Option;
import lombok.Data;

import java.awt.Font;
import java.awt.Paint;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class DrawText extends SNode {
    public final String text;

    public @Data static class Args {
        public final Font font;
        public final String text;
        public final FontRenderContext fontRenderContext;
    }

    //Expensive, could cache it.
    //TODO: clear the cache sometimes?  We don't want OOMEs
    //Not clear how valuable caching is, it looks like it saved 10% of simple app time, but maybe some of that was pushed over into cache lookup time
    public static final F<Args, TextLayout> toTextLayout = new Cache<Args, TextLayout>( new F<Args, TextLayout>() {
        @Override public TextLayout f( final Args args ) {
            return new TextLayout( args.text, args.font, args.fontRenderContext );
        }
    } );

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        graphics2D.drawString( text, 0, 0 );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        return new ImmutableRectangle2D( toTextLayout.f( new Args( mockState.getFont(), text, mockState.getFontRenderContext() ) ).getBounds() );
    }

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {
        return toTextLayout.f( new Args( mockState.getFont(), text, mockState.getFontRenderContext() ) ).
                getOutline( new AffineTransform() ).contains( vector2D.toPoint2D() ) ? Option.some( new PickResult( this, mockState.getDragHandler() ) ) : Option.<PickResult>none();
    }

    public static SNode textNode( String text, Font font, Paint paint ) {
        final WithFont textNode = new WithFont( font, new WithPaint( paint, new DrawText( text ) ) );

        //Put origin at top left
        return textNode.translate( -textNode.getBounds().x, -textNode.getBounds().y );
    }
}