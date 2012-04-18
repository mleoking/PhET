package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class DrawShape extends SEffect {
    public final Shape shape;

    @Override public void e( final DrawableGraphicsContext context ) {
        context.draw( shape );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext context ) {
        return new ImmutableRectangle2D( shape.getBounds2D() );
    }
}