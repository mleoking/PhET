
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
    
    private GameModel model;

    public GameModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_GAME, new RPALClock(), true /* startsPaused */ );

        // Model
        model = new GameModel( getClock() );
        
        // Canvas
        GameCanvas canvas = new GameCanvas( model, this );
        setSimulationPanel( canvas );

        // no control panel
        setControlPanel( null );
        
        // no clock controls
        setClockControlPanel( null );

        // Set initial state
        reset();
    }
    
    public void reset() {
        super.reset();
        model.reset();
    }
}
