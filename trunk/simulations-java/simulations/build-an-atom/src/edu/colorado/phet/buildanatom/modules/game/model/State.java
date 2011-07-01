// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.BuildAnAtomGameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * A state that the GameModel can be in, such as the GameOverState, or a specific Problem state.
 *
 * @author Sam Reid
 */
public abstract class State {
    protected final BuildAnAtomGameModel model;

    public State( BuildAnAtomGameModel model ) {
        this.model = model;
    }

    public abstract StateView createView( BuildAnAtomGameCanvas gameCanvas );

    /**
     * Execute any functions that need to occur upon entry into this state,
     * does nothing by default.
     */
    public void init() {
    }

    ;

    /**
     * Execute any functions that need to occur upon exit of this state, does
     * nothing by default.
     */
    public void teardown() {
    }

    ;
}
