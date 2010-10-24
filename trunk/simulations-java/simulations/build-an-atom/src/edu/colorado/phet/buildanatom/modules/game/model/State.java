package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.modules.game.view.GameCanvas;
import edu.colorado.phet.buildanatom.modules.game.view.StateView;

/**
 * A state that the GameModel can be in.
 *
 * @author Sam Reid
 */
public abstract class State {
    protected final BuildAnAtomGameModel model;

    public State( BuildAnAtomGameModel model ) {
        this.model = model;
    }

    public abstract StateView createView( GameCanvas gameCanvas );
}
