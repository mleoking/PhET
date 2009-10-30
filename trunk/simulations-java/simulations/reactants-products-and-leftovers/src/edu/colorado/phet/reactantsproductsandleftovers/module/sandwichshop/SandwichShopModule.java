
package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;

/**
 * The "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopModule extends PiccoloModule {

    public SandwichShopModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_SANDWICH_SHOP, new RPALClock(), true /* startsPaused */ );

        // Model
        SandwichShopModel model = new SandwichShopModel();
        
        // Canvas
        SandwichShopCanvas canvas = new SandwichShopCanvas( model );
        setSimulationPanel( canvas );

        // no control panel
        setControlPanel( null );
        
        // no clock controls
        setClockControlPanel( null );

        // Set initial state
        reset();
    }
}
