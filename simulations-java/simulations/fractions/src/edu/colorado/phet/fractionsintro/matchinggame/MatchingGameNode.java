// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import fj.Effect;
import fj.F;

import edu.colorado.phet.fractions.util.Cache;
import edu.colorado.phet.fractionsintro.matchinggame.model.Fraction;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class MatchingGameNode extends PNode {
    //TODO: put addChild in FNode extends PNode?
    private Effect<PNode> addChild = new Effect<PNode>() {
        @Override public void e( PNode p ) {
            addChild( p );
        }
    };

    public MatchingGameNode( MatchingGameState state ) {
        state.fractions.map( new Cache<Fraction, PNode>( new F<Fraction, PNode>() {
            @Override public PNode f( Fraction f ) {
                return new PText( f.toString() );
            }
        } ) ).foreach( addChild );
    }
}
