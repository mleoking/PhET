/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.titrate;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.titration.TitrationApplication;
import edu.colorado.phet.titration.TitrationStrings;
import edu.colorado.phet.titration.model.TitrationClock;
import edu.colorado.phet.titration.persistence.TitrateConfig;

/**
 * The "Titrate" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitrateModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private TitrateModel model;
    private TitrateCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public TitrateModule( Frame parentFrame ) {
        super( TitrationStrings.TITLE_TITRATE, new TitrationClock() );

        // Model
        TitrationClock clock = (TitrationClock) getClock();
        model = new TitrateModel( clock );

        // Canvas
        canvas = new TitrateCanvas( model );
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
        load( TitrateDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public TitrateConfig save() {

        TitrateConfig config = new TitrateConfig();

        // Module
        config.setActive( isActive() );
        
        //XXX

        return config;
    }

    public void load( TitrateConfig config ) {

        // Module
        if ( config.isActive() ) {
            TitrationApplication.getInstance().setActiveModule( this );
        }

        //XXX
    }
}
