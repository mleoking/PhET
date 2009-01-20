/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.findunknown;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.persistence.FindUnknownConfig;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * FindUnknownModule is the "Find The Unknown" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FindUnknownModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_FIND_THE_UNKNOWN_MODULE;
    private static final ABSClock CLOCK = new ABSClock( SolutionsDefaults.CLOCK_FRAME_RATE, SolutionsDefaults.CLOCK_DT );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private FindUnknownModel _model;
    private FindUnknownCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public FindUnknownModule( Frame parentFrame ) {
        super( TITLE, CLOCK, false /* startsPaused */ );
        setLogoPanelVisible( false );

        // Model
        _model = new FindUnknownModel( CLOCK );

        // Canvas
        _canvas = new FindUnknownCanvas( _model );
        setSimulationPanel( _canvas );

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

        // Clock
        ABSClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( SolutionsDefaults.CLOCK_DT );
        setClockRunningWhenActive( SolutionsDefaults.CLOCK_RUNNING );

        //XXX other stuff
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public FindUnknownConfig save() {

        FindUnknownConfig config = new FindUnknownConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        ABSClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        //XXX other stuff
        
        return config;
    }

    public void load( FindUnknownConfig config ) {

        // Module
        if ( config.isActive() ) {
            AcidBaseSolutionsApplication.getInstance().setActiveModule( this );
        }

        // Clock
        ABSClock clock = _model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        //XXX other stuff
    }
}
