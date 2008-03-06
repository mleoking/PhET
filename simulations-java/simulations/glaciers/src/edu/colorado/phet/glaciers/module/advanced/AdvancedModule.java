/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.module.advanced;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.defaults.AdvancedDefaults;
import edu.colorado.phet.glaciers.model.Climate;
import edu.colorado.phet.glaciers.model.Glacier;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.model.Valley;
import edu.colorado.phet.glaciers.persistence.AdvancedConfig;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.colorado.phet.glaciers.view.PlayArea;

/**
 * AdvancedModule is the "Advanced" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AdvancedModel _model;
    private AdvancedController _controller;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AdvancedModule( Frame parentFrame ) {
        super( GlaciersStrings.TITLE_ADVANCED, AdvancedDefaults.CLOCK );
        setLogoPanel( null );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        Valley valley = new Valley();
        Climate climate = new Climate( AdvancedDefaults.TEMPERATURE_RANGE.getDefault(), AdvancedDefaults.SNOWFALL_RANGE.getDefault(), AdvancedDefaults.SNOWFALL_REFERENCE_ELEVATION_RANGE.getDefault() );
        Glacier glacier = new Glacier( valley, climate, clock );
        _model = new AdvancedModel( clock, glacier );

        // Play Area
        ModelViewTransform mvt = new ModelViewTransform(); //XXX identity
        PlayArea playArea = new PlayArea( _model, mvt, AdvancedDefaults.VALLEY_X_MIN, AdvancedDefaults.VALLEY_X_MAX );
        setSimulationPanel( playArea );

        // Bottom panel goes when clock controls normally go
        AdvancedControlPanel controlPanel = new AdvancedControlPanel( clock );
        setClockControlPanel( controlPanel );

        // Controller
        _controller = new AdvancedController( _model, playArea, controlPanel );
        
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
    public void resetAll() {

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setFrameRate( AdvancedDefaults.CLOCK_FRAME_RATE_RANGE.getDefault() );
            setClockRunningWhenActive( AdvancedDefaults.CLOCK_RUNNING );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public AdvancedConfig save() {

        AdvancedConfig config = new AdvancedConfig();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( AdvancedConfig config ) {

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
