// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.State;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract base class for any state within the game, including ProblemViews and GameOverView, GameSettingsView, etc.
 *
 * @author Sam Reid
 */
public abstract class StateView {
    private final BuildAnAtomGameCanvas gameCanvas;
    private final State state;
    private final BuildAnAtomGameModel model;

    StateView( BuildAnAtomGameModel model, State state, BuildAnAtomGameCanvas gameCanvas ) {
        this.model = model;
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

    protected BuildAnAtomGameModel getModel(){
        return model;
    }
}
