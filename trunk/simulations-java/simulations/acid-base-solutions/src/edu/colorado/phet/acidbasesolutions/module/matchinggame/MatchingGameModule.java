/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.awt.Frame;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.acidbasesolutions.defaults.SolutionsDefaults;
import edu.colorado.phet.acidbasesolutions.model.ABSClock;
import edu.colorado.phet.acidbasesolutions.persistence.MatchingGameConfig;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * MatchingGameModule is the "Matching Game" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String TITLE = ABSStrings.TITLE_MATCHING_GAME_MODULE;
    private static final ABSClock CLOCK = new ABSClock( SolutionsDefaults.CLOCK_FRAME_RATE, SolutionsDefaults.CLOCK_DT );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MatchingGameModel _model;
    private MatchingGameCanvas _canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MatchingGameModule( Frame parentFrame ) {
        super( TITLE, CLOCK, false /* startsPaused */ );
        setLogoPanelVisible( false );

        // Model
        _model = new MatchingGameModel( CLOCK );

        // Canvas
        _canvas = new MatchingGameCanvas( _model );
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

    public MatchingGameConfig save() {

        MatchingGameConfig config = new MatchingGameConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        ABSClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        //XXX other stuff
        
        return config;
    }

    public void load( MatchingGameConfig config ) {

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
