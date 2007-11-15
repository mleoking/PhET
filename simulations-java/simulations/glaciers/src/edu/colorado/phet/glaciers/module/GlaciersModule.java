/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.GlaciersControlPanel;
import edu.colorado.phet.glaciers.defaults.GlaciersDefaults;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.persistence.GlaciersModuleConfig;

/**
 * GlaciersModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private GlaciersModel _model;
    private GlaciersCanvas _canvas;
    private GlaciersControlPanel _bottomPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GlaciersModule( Frame parentFrame ) {
        super( GlaciersResources.getString( "title.exampleModule" ), GlaciersDefaults.CLOCK );
        setLogoPanel( null );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new GlaciersModel( clock );

        // Canvas
        _canvas = new GlaciersCanvas( _model );
        setSimulationPanel( _canvas );

        // Bottom panel goes when clock controls normally go
        _bottomPanel = new GlaciersControlPanel( clock );
        setClockControlPanel( _bottomPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public GlaciersModel getExampleModel() {
        return _model;
    }

    public GlaciersCanvas getExampleCanvas() {
        return _canvas;
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( GlaciersDefaults.CLOCK_DT_RANGE.getDefault() );
            setClockRunningWhenActive( GlaciersDefaults.CLOCK_RUNNING );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public GlaciersModuleConfig save() {

        GlaciersModuleConfig config = new GlaciersModuleConfig();
        GlaciersModel model = getExampleModel();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( GlaciersModuleConfig config ) {

        GlaciersModel model = getExampleModel();

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
