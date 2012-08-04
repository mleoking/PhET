// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.view;

import fj.Effect;
import fj.F;
import fj.data.List;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo PNode that adds some functionality for improved interoperability with the Functional Java library.
 * In particular:
 * 1. There is a constructor that takes a fj.data.List of PNodes.
 * 2. addChild is provided as a fj.Effect so that it can be applied with foreach
 * 3. Static function wrappers are provided for PNode bounds computation within functional java list comprehensions
 * -(Note, these static function wrappers could be declared elsewhere, like in a utility class, but I thought it made sense to keep it with FNode since they can be used here).
 *
 * @author Sam Reid
 */
public class FNode extends RichPNode {

    public FNode() {
        this( List.<PNode>nil() );
    }

    public FNode( final List<? extends PNode> children ) {
        super( children.toCollection() );
    }

    //Function for adding children to this node
    public final Effect<PNode> addChild = new Effect<PNode>() {
        @Override public void e( PNode p ) {
            addChild( p );
        }
    };

    //Function wrappers for use with the functional java library, for performing tasks such as finding the maximum width of a list of PNodes.

    public static final F<PNode, Double> _fullWidth = new F<PNode, Double>() {
        @Override public Double f( final PNode pnode ) {
            return pnode.getFullBounds().getWidth();
        }
    };

    public static final F<PNode, Double> _fullHeight = new F<PNode, Double>() {
        @Override public Double f( final PNode pnode ) {
            return pnode.getFullBounds().getHeight();
        }
    };

    //See declaration site variance discussion here: https://groups.google.com/forum/?fromgroups#!topic/functionaljava/uGzU8x-rKJ4
    public static <A extends PNode> F<A, Double> _minX() {
        return new F<A, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getMinX();
            }
        };
    }

    public static <A extends PNode> F<A, Double> _maxX() {
        return new F<A, Double>() {
            @Override public Double f( final PNode pNode ) {
                return pNode.getFullBounds().getMaxX();
            }
        };
    }

    //Get all immediate (i.e. non-recursively) children that are instances of the specified type
    public static <T> List<T> getChildren( final PNode node, final Class<T> type ) {
        List<T> list = List.nil();
        for ( Object child : node.getChildrenReference() ) {
            if ( type.isInstance( child ) ) {
                list = list.snoc( (T) child );
            }
        }
        return list;
    }
}