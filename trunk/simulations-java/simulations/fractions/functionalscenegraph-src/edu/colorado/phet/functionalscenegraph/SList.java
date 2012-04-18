package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.List;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;

/**
 * @author Sam Reid
 */
public class SList extends SEffect {
    private final List<SEffect> children;

    public SList( final SEffect... children ) {
        this.children = List.iterableList( Arrays.asList( children ) );
    }

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        children.foreach( new Effect<SEffect>() {
            @Override public void e( final SEffect s ) {
                s.render( graphics2D );
            }
        } );
    }

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {

        //TODO: use fold after ImmutableRectangle2D supports EMPTY.union
        ImmutableRectangle2D rect = children.head().getBounds( mockState );
        for ( SEffect sEffect : children.tail() ) {
            rect = rect.union( sEffect.getBounds( mockState ) );
        }
        return rect;
    }
}