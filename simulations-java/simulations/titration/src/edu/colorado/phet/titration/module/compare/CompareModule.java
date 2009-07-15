/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.compare;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.titration.TitrationApplication;
import edu.colorado.phet.titration.TitrationStrings;
import edu.colorado.phet.titration.defaults.CompareDefaults;
import edu.colorado.phet.titration.model.TitrationClock;
import edu.colorado.phet.titration.persistence.CompareConfig;

/**
 * The "Compare Titrations" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CompareModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private CompareModel model;
    private CompareCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public CompareModule( Frame parentFrame ) {
        super( TitrationStrings.TITLE_COMPARE_TITRATIONS, new TitrationClock() );

        // Model
        TitrationClock clock = (TitrationClock) getClock();
        model = new CompareModel( clock );

        // Canvas
        canvas = new CompareCanvas( model );
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
        load( CompareDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public CompareConfig save() {

        CompareConfig config = new CompareConfig();

        // Module
        config.setActive( isActive() );
        
        //XXX

        return config;
    }

    public void load( CompareConfig config ) {

        // Module
        if ( config.isActive() ) {
            TitrationApplication.getInstance().setActiveModule( this );
        }

        //XXX
    }
}
