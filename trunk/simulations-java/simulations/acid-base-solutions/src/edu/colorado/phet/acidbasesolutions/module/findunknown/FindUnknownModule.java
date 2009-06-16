/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.findunknown;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.module.ABSAbstractModule;
import edu.colorado.phet.acidbasesolutions.persistence.FindUnknownConfig;

/**
 * FindUnknownModule is the "Find The Unknown" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindUnknownModule extends ABSAbstractModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_FIND_THE_UNKNOWN_MODULE;
    private static final ABSClock CLOCK = new ABSClock();

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FindUnknownModel model;
    private FindUnknownCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FindUnknownModule( Frame parentFrame ) {
        super( TITLE, CLOCK );

        // Model
        model = new FindUnknownModel( CLOCK );

        // Canvas
        canvas = new FindUnknownCanvas( model, this );
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
        super.reset();
        load( FindUnknownDefaults.getInstance().getConfig() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public FindUnknownConfig save() {

        FindUnknownConfig config = new FindUnknownConfig();

        // Module
        config.setActive( isActive() );

        //XXX call config setters
        
        return config;
    }

    public void load( FindUnknownConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        //XXX call config getters
    }
}
