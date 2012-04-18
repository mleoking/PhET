package edu.colorado.phet.functionalscenegraph;

import fj.data.Option;
import lombok.Data;

import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

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

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {
        return shape.contains( vector2D.x, vector2D.y ) ? Option.some( new PickResult( this, mockState.getDragHandler() ) ) : Option.<PickResult>none();
    }
}