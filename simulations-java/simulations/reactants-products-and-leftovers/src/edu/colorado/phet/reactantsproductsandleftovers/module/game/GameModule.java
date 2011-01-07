// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Frame;

import edu.colorado.phet.reactantsproductsandleftovers.RPALModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends RPALModule {
    
    private final GameModel model;
    private final GameCanvas canvas;
    private boolean rewardWasRunning = false; // was the game reward animation running when this module was deactivated?

    public GameModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_GAME, new RPALClock(), true /* startsPaused */ );

        // Model
        model = new GameModel( getClock() );
        
        // Canvas
        canvas = new GameCanvas( model, this );
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
