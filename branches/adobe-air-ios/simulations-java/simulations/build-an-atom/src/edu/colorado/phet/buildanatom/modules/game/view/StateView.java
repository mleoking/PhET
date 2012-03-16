// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.State;
import edu.colorado.phet.common.games.GameScoreboardNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Abstract base class that provides common functionality for displaying the
 * various states that occur during the course of the game.  This includes the
 * states at the beginning and end of the game, as well as the individual
 * problem views.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class StateView {
    private final BuildAnAtomGameCanvas gameCanvas;
    private final State state;
    private final BuildAnAtomGameModel model;

    /**
     * Constructor.
     */
    StateView( BuildAnAtomGameModel model, State state, BuildAnAtomGameCanvas gameCanvas ) {
        this.model = model;
        this.gameCanvas = gameCanvas;
        this.state = state;
    }

    public State getState() {
        return state;
    }

    /**
     * Initialize this state view.  This is called whenever the game
     * transitions into the associated state.
     */
    public abstract void init();

    /**
     * Tear down this state view.  This is called when the game transitions
     * out of the associated state.
     */
    public abstract void teardown();

    public GameScoreboardNode getScoreboard() {
        return gameCanvas.getScoreboard();
    }

    protected void addChild( PNode child ) {
        gameCanvas.getRootNode().addChild( child );
    }

    protected void removeChild( PNode child ) {
        gameCanvas.getRootNode().removeChild( child );
    }

    protected BuildAnAtomGameModel getModel() {
        return model;
    }
}
