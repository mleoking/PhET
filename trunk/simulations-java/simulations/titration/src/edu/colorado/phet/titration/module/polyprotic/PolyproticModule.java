/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.polyprotic;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.titration.TitrationApplication;
import edu.colorado.phet.titration.TitrationStrings;
import edu.colorado.phet.titration.defaults.PolyproticDefaults;
import edu.colorado.phet.titration.model.TitrationClock;
import edu.colorado.phet.titration.persistence.PolyproticConfig;

/**
 * The "Polyprotic Acids" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PolyproticModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private PolyproticModel model;
    private PolyproticCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public PolyproticModule( Frame parentFrame ) {
        super( TitrationStrings.TITLE_POLYPROTIC_ACIDS, new TitrationClock() );

        // Model
        TitrationClock clock = (TitrationClock) getClock();
        model = new PolyproticModel( clock );

        // Canvas
        canvas = new PolyproticCanvas( model );
        setSimulationPanel( canvas );

        // No control Panel
        setControlPanel( null );
        
        //  No clock controls
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
        getClock().resetSimulationTime();
        
        // load the default configuration
        load( PolyproticDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public PolyproticConfig save() {

        PolyproticConfig config = new PolyproticConfig();

        // Module
        config.setActive( isActive() );
        
        //XXX

        return config;
    }

    public void load( PolyproticConfig config ) {

        // Module
        if ( config.isActive() ) {
            TitrationApplication.getInstance().setActiveModule( this );
        }

        //XXX
    }
}
