/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;

/**
 * The "Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GameModule extends PiccoloModule {
    
    private final GameModel model;
    private final GameCanvas canvas;

    public GameModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_GAME, new RPALClock(), true /* startsPaused */ );

        // Model
        model = new GameModel( getClock() );
        
        // Canvas
        canvas = new GameCanvas( model, this );
        setSimulationPanel( canvas );

        // no control panel
        setControlPanel( null );
        
        // no clock controls
        setClockControlPanel( null );
    }
    
    @Override
    public void activate() {
        super.activate();
        canvas.activate();
    }
    
    @Override
    public void deactivate() {
        super.deactivate();
        canvas.deactivate();
    }
}
