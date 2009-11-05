
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

    public RealReactionModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_REAL_REACTION, new RPALClock() );

        // Model
        RealReactionModel model = new RealReactionModel( null /* XXX */ );

        // Canvas
        RealReactionCanvas canvas = new RealReactionCanvas();
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
}
