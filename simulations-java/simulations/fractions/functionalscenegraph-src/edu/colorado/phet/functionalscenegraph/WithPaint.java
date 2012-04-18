package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Paint;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * Apply an effect to the graphics2d with the specified paint
 *
 * @author Sam Reid
 */
public @Data class WithPaint extends SEffect {
    public final Paint paint;
    public final SEffect child;

    @Override public void e( final DrawableGraphicsContext graphics2D ) {
        Paint origPaint = graphics2D.getPaint();
        graphics2D.setPaint( paint );
        child.e( graphics2D );
        graphics2D.setPaint( origPaint );
    }

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {
        return child.getBounds( mockState );
    }
}