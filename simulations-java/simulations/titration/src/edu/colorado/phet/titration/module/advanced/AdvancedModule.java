/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.advanced;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.titration.TitrationApplication;
import edu.colorado.phet.titration.TitrationStrings;
import edu.colorado.phet.titration.defaults.AdvancedDefaults;
import edu.colorado.phet.titration.model.TitrationClock;
import edu.colorado.phet.titration.persistence.AdvancedConfig;

/**
 * The "Advanced Titration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AdvancedModel model;
    private AdvancedCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AdvancedModule( Frame parentFrame ) {
        super( TitrationStrings.TITLE_ADVANCED_TITRATION, new TitrationClock() );

        // Model
        TitrationClock clock = (TitrationClock) getClock();
        model = new AdvancedModel( clock );

        // Canvas
        canvas = new AdvancedCanvas( model );
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
        load( AdvancedDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public AdvancedConfig save() {

        AdvancedConfig config = new AdvancedConfig();

        // Module
        config.setActive( isActive() );
        
        //XXX

        return config;
    }

    public void load( AdvancedConfig config ) {

        // Module
        if ( config.isActive() ) {
            TitrationApplication.getInstance().setActiveModule( this );
        }

        //XXX
    }
}
