package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.List;
import fj.data.Option;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public class SList extends SNode {
    private final List<SNode> children;

    public SList( final SNode... children ) {
        this.children = List.iterableList( Arrays.asList( children ) );
    }

    @Override public void render( final DrawableGraphicsContext graphics2D ) {
        children.foreach( new Effect<SNode>() {
            @Override public void e( final SNode s ) {
                s.render( graphics2D );
            }
        } );
    }

    @Override public ImmutableRectangle2D getBounds( final GraphicsContext mockState ) {

        //TODO: use fold after ImmutableRectangle2D supports EMPTY.union
        ImmutableRectangle2D rect = children.head().getBounds( mockState );
        for ( SNode sEffect : children.tail() ) {
            rect = rect.union( sEffect.getBounds( mockState ) );
        }
        return rect;
    }

    @Override protected Option<? extends SNode> pick( final Vector2D vector2D, final MockState mockState ) {
        for ( SNode sNode : children.reverse() ) {
            final Option<? extends SNode> picked = sNode.pick( vector2D, mockState );
            if ( picked.isSome() ) {
                return picked;
            }
        }
        return Option.none();
    }
}