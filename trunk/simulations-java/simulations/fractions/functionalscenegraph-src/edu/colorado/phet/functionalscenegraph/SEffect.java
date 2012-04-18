package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public abstract @Data class SEffect {
    abstract public void e( final DrawableGraphicsContext context );

    abstract public ImmutableRectangle2D getBounds( GraphicsContext context );
}