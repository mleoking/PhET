
package edu.colorado.phet.reactantsproductsandleftovers.module.realreaction;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;

/**
 * The "Real Reaction" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RealReactionModule extends PiccoloModule {
    
    private final RealReactionModel model;

    public RealReactionModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_REAL_REACTION, new RPALClock() );

        // Model
        model = new RealReactionModel();

        // Canvas
        RealReactionCanvas canvas = new RealReactionCanvas( model, this );
        setSimulationPanel( canvas );

        // this module has no control panel
        setControlPanel( null );
        
        // this module has no clock controls
        setClockControlPanel( null );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }
    
    public void reset() {
        super.reset();
        model.reset();
    }
}
