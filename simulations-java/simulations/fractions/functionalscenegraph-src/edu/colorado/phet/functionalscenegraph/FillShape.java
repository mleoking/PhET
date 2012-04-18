package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public @Data class FillShape extends SNode {
    public final Shape shape;

    public FillShape( Shape shape ) {
        this.shape = shape;
    }

    public FillShape( ImmutableRectangle2D shape ) {
        this( shape.toRectangle2D() );
    }

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        graphics2D.fill( shape );
    }

    @Override public ImmutableRectangle2D getBounds( GraphicsContext mockState ) {
        return new ImmutableRectangle2D( shape.getBounds2D() );
    }
}