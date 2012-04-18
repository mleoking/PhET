package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.Option;
import lombok.Data;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class WithDragEvent extends SNode {
    private final Effect<Vector2D> effect;
    private final SNode child;

    //TODO: abstraction for HasChild?
    @Override public void render( final DrawableGraphicsContext context ) { child.render( context );}

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext context ) {return child.getBounds( context );}

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {
        Effect<Vector2D> original = mockState.getDragHandler();
        mockState.setDragHandler( effect );
        final Option<PickResult> result = child.pick( vector2D, mockState );
        mockState.setDragHandler( original );
        return result;
    }
}