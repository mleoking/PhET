package edu.colorado.phet.functionalscenegraph;

import lombok.Data;

import java.awt.Paint;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public abstract @Data class SNode {
    abstract public void render( final DrawableGraphicsContext context );

    abstract public ImmutableRectangle2D getBounds( GraphicsContext context );

    public SNode translate( final double dx, final double dy ) {
        return new WithTransform( AffineTransform.getTranslateInstance( dx, dy ), this );
    }

    public ImmutableRectangle2D getBounds() { return getBounds( new MockState( SCanvas.DEFAULT_FONT ) );}

    public SNode scale( final int s ) { return new WithTransform( AffineTransform.getScaleInstance( s, s ), this ); }

    public SNode withPaint( final Paint paint ) { return new WithPaint( paint, this );}
}