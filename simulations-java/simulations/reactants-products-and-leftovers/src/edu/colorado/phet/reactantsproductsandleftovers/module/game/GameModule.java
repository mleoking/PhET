
package edu.colorado.phet.reactantsproductsandleftovers.module.game;

import java.awt.Frame;

import javax.swing.JLabel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
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
        model = new GameModel();
        
        // Canvas
        GameCanvas canvas = new GameCanvas( model, this );
        setSimulationPanel( canvas );

        // no control panel
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.addControl( new JLabel( "------- controls go here ------" ) );
        setControlPanel( controlPanel );
        
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
