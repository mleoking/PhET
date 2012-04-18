package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Paint;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * Apply an effect to the graphics2d with the specified paint
 *
 * @author Sam Reid
 */
public @Data class WithPaint extends SNode {
    public final Paint paint;
    public final SNode child;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        Paint origPaint = graphics2D.getPaint();
        graphics2D.setPaint( paint );
        child.render( graphics2D );
        graphics2D.setPaint( origPaint );
    }

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {
        return child.getBounds( mockState );
    }

    @Override protected boolean hits( final Vector2D vector2D, final MockState mockState ) {
        return child.hits( vector2D, mockState );
    }
}