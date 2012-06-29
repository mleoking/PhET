// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.functionalscenegraph;

import fj.Effect;
import fj.data.List;
import fj.data.Option;
import lombok.Data;

import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;

/**
 * @author Sam Reid
 */
public @Data class SList extends SNode {
    public final List<SNode> children;

    public SList( final List<SNode> children ) {
        this.children = children;
    }

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

    @Override protected Option<PickResult> pick( final Vector2D vector2D, final MockState mockState ) {
        for ( SNode sNode : children.reverse() ) {
            final Option<PickResult> picked = sNode.pick( vector2D, mockState );
            if ( picked.isSome() ) {
                return picked;
            }
        }
        return Option.none();
    }

    public int length() {
        return children.length();
    }
}