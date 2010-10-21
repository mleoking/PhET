package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.State;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.umd.cs.piccolo.PNode;

/**
* @author Sam Reid
*/
public abstract class StateView {
    private final GameCanvas gameCanvas;
    State state;

    StateView( GameCanvas gameCanvas, State state ) {
        this.gameCanvas = gameCanvas;
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public abstract void teardown();

    public abstract void init();

    public GameScoreboardNode getScoreboard() {
        return gameCanvas.getScoreboard();
    }

    public void addChild( PNode child ) {
        gameCanvas.getRootNode().addChild( child );
    }

    public void removeChild( PNode child ) {
        gameCanvas.getRootNode().removeChild( child );
    }
}
