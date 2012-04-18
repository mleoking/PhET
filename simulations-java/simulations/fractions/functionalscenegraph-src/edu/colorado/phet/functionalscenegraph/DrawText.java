package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.font.TextLayout;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class DrawText extends SEffect {
    public final String text;

    @Override public void e( final DrawableGraphicsContext graphics2D ) {
        graphics2D.drawString( text, 0, 0 );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        TextLayout textLayout = new TextLayout( text, mockState.getFont(), mockState.getFontRenderContext() );
        return new ImmutableRectangle2D( textLayout.getBounds() );
    }
}