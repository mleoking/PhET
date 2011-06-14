// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.module.game;

import edu.colorado.phet.balancingchemicalequations.BCEGlobalProperties;
import edu.colorado.phet.balancingchemicalequations.BCEModule;
import edu.colorado.phet.balancingchemicalequations.BCEStrings;
import edu.colorado.phet.balancingchemicalequations.model.BCEClock;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends BCEModule {

    private final GameModel model;
    private final GameCanvas canvas;
    private boolean rewardWasRunning = false; // was the game reward animation running when this module was deactivated?

    public GameModule( BCEGlobalProperties globalProperties ) {
        super( BCEStrings.BALANCING_GAME, new BCEClock(), true /* startsPaused */ );
        model = new GameModel( globalProperties );
        canvas = new GameCanvas( model, globalProperties );
        setSimulationPanel( canvas );
    }

    @Override
    public void activate() {
        super.activate();
        if ( rewardWasRunning ) {
            canvas.getRewardNode().play();
        }
    }

    @Override
    public void deactivate() {
        super.deactivate();
        rewardWasRunning = canvas.getRewardNode().isRunning();
        canvas.getRewardNode().pause();
    }
}
