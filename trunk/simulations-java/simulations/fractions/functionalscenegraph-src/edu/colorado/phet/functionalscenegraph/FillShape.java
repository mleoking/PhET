package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class FillShape extends SEffect {
    public final Shape shape;

    @Override public void e( final DrawableGraphicsContext graphics2D ) {
        graphics2D.fill( shape );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        return new ImmutableRectangle2D( shape.getBounds2D() );
    }
}