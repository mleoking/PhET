// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas2 extends AbstractFractionsCanvas {
    public MatchingGameCanvas2( final MatchingGameModel model ) {
        addChild( new PNode() {{
            model.state.addObserver( new VoidFunction1<MatchingGameState>() {
                @Override public void apply( MatchingGameState s ) {
                    removeAllChildren();
                    addChild( new MatchingGameNode( s ) );
                }
            } );
        }} );
    }
}