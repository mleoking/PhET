// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.view;

import fj.Effect;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Factored out a reused abstraction in the MatchingGameModule, which recreates a PNode when 1 or more properties change
 *
 * @author Sam Reid
 */
class UpdateNode extends PNode {

    public UpdateNode( final Effect<PNode> effect, ObservableProperty... p ) {
        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();
                effect.e( UpdateNode.this );
            }
        }.observe( p );
    }
}