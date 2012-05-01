package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.Option;
import lombok.Data;

import java.awt.Cursor;
import java.awt.Paint;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public abstract @Data class SNode {

    abstract public void render( final DrawableGraphicsContext context );

    abstract public ImmutableRectangle2D getBounds( GraphicsContext context );

    public SNode translate( Vector2D delta ) { return translate( delta.x, delta.y ); }

    public SNode translate( final double dx, final double dy ) {
        return new WithTransform( AffineTransform.getTranslateInstance( dx, dy ), this );
    }

    public ImmutableRectangle2D getBounds() { return getBounds( createMockState() );}

    public SNode scale( final int s ) { return new WithTransform( AffineTransform.getScaleInstance( s, s ), this ); }

    public SNode withPaint( final Paint paint ) { return new WithPaint( paint, this );}

    public Option<PickResult> pick( final Vector2D vector2D ) { return pick( vector2D, createMockState() ); }

    private MockState createMockState() {return new MockState( SCanvas.DEFAULT_FONT );}

    protected abstract Option<PickResult> pick( final Vector2D vector2D, final MockState mockState );

    //TODO: move to property effect
    public Cursor getCursor() { return Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ); }

    public SNode withDragEvent( final Effect<Vector2D> effect ) { return new WithDragEvent( effect, this ); }

    //Assumes same coordinate frame
    public SNode centeredOn( final SNode track ) { return centeredOn( new Vector2D( track.getBounds().getCenter() ) ); }

    public SNode centeredOn( final Vector2D point ) { return translate( new Vector2D( point.toPoint2D(), getBounds().getCenter().toPoint2D() ) ); }

    public Vector2D leftCenter() {
        final ImmutableRectangle2D bounds = getBounds();
        return new Vector2D( bounds.x, bounds.getCenter().getY() );
    }
}