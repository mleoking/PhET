// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.view;

import fj.Effect;

import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo PNode that adds some functionality for interoperation with functionaljava
 *
 * @author Sam Reid
 */
public class FNode extends RichPNode {

    //Function for adding children to this node
    public final Effect<PNode> addChild = new Effect<PNode>() {
        @Override public void e( PNode p ) {
            addChild( p );
        }
    };

    public void addChildren( PNode... children ) {
        for ( PNode child : children ) {
            addChild( child );
        }
    }
}