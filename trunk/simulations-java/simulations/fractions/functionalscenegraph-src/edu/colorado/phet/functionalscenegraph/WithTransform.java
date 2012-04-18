package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * Apply an effect to the graphics2d with the specified paint
 *
 * @author Sam Reid
 */
public @Data class WithTransform extends SEffect {
    public final AffineTransform transform;
    public final SEffect child;

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        AffineTransform orig = graphics2D.getTransform();
        graphics2D.transform( transform );
        child.render( graphics2D );
        graphics2D.setTransform( orig );
    }

    //TODO: is this the right direction of transform?
    //Also reduce wrapping here
    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {
        return new ImmutableRectangle2D( transform.createTransformedShape( child.getBounds( mockState ).toRectangle2D() ) );
    }
}