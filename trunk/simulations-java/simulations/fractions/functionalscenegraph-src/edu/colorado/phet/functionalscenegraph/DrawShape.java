package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class DrawShape extends SNode {
    public final Shape shape;

    public DrawShape( ImmutableRectangle2D rectangle ) {
        this( rectangle.toRectangle2D() );
    }

    public DrawShape( Shape shape ) {
        this.shape = shape;
    }

    @Override public void render( final DrawableGraphicsContext context ) {
        context.draw( shape );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext context ) {
        return new ImmutableRectangle2D( shape.getBounds2D() );
    }
}