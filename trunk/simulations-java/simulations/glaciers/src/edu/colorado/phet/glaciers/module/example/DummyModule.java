/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.GlaciersClockControlPanel;
import edu.colorado.phet.glaciers.defaults.DummyDefaults;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.module.GlaciersAbstractModule;
import edu.colorado.phet.glaciers.persistence.ExampleConfig;
import edu.colorado.phet.glaciers.persistence.GlaciersConfig;

/**
 * DummyModule
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DummyModule extends GlaciersAbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private DummyModel _model;
    private DummyCanvas _canvas;
    private DummyControlPanel _controlPanel;
    private GlaciersClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DummyModule() {
        super( GlaciersResources.getString( "title.dummyModule" ), DummyDefaults.CLOCK );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new DummyModel( clock );

        // Canvas
        _canvas = new DummyCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new DummyControlPanel( this );
        setControlPanel( _controlPanel );

        // Clock controls
        _clockControlPanel = new GlaciersClockControlPanel( (GlaciersClock) getClock() );
        _clockControlPanel.setTimeColumns( DummyDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        resetAll();
    }

    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------

    public DummyModel getDummyModel() {
        return _model;
    }

    public DummyCanvas getDummyCanvas() {
        return _canvas;
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Indicates whether this module has help.
     *
     * @return true or false
     */
    public boolean hasHelp() {
        return false;
    }

    /**
     * Open selected dialogs when this module is activated.
     */
    public void activate() {
        super.activate();
    }

    /**
     * Close all dialogs when this module is deactivated.
     */
    public void deactivate() {
        _controlPanel.closeAllDialogs();
        super.deactivate();
    }

    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------

    public void resetAll() {

        // Model
        {
            // Clock
            GlaciersClock clock = _model.getClock();
            clock.setDt( DummyDefaults.CLOCK_DT );
            setClockRunningWhenActive( DummyDefaults.CLOCK_RUNNING );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }

    public void save( GlaciersConfig appConfig ) {

        ExampleConfig config = appConfig.getDummyConfig();
        DummyModel model = getDummyModel();

        // Module
        config.setActive( isActive() );

        // Clock
        GlaciersClock clock = model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // Control panel settings
        {
            //XXX
        }
    }

    public void load( GlaciersConfig appConfig ) {

        ExampleConfig config = appConfig.getDummyConfig();
        DummyModel model = getDummyModel();

        // Module
        if ( config.isActive() ) {
            GlaciersApplication.instance().setActiveModule( this );
        }

        // Clock
        GlaciersClock clock = model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        // Control panel settings
        {
            //XXX
        }
    }
}
