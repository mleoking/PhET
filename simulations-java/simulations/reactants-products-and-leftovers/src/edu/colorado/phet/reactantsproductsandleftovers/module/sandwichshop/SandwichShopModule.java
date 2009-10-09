/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALClock;
import edu.colorado.phet.reactantsproductsandleftovers.model.RPALModel;
import edu.colorado.phet.reactantsproductsandleftovers.view.RPALCanvas;

/**
 * The "Sandwich Shop" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SandwichShopModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private RPALModel model;
    private RPALCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SandwichShopModule( Frame parentFrame ) {
        super( RPALStrings.TITLE_SANDWICH_SHOP, new RPALClock( SandwichShopDefaults.CLOCK_FRAME_RATE, SandwichShopDefaults.CLOCK_DT ) );

        // Model
        RPALClock clock = (RPALClock) getClock();
        model = new RPALModel( clock );

        // Canvas
        canvas = new RPALCanvas( model );
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

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // reset the clock
        RPALClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
