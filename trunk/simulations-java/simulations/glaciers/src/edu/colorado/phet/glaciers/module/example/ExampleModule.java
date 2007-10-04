/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.module.example;

import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.control.GlaciersClockControlPanel;
import edu.colorado.phet.glaciers.defaults.ExampleDefaults;
import edu.colorado.phet.glaciers.model.ExampleModelElement;
import edu.colorado.phet.glaciers.model.GlaciersClock;
import edu.colorado.phet.glaciers.module.GlaciersAbstractModule;
import edu.colorado.phet.glaciers.persistence.ExampleConfig;
import edu.colorado.phet.glaciers.persistence.GlaciersConfig;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModule extends GlaciersAbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModel _model;
    private ExampleCanvas _canvas;
    private ExampleControlPanel _controlPanel;
    private GlaciersClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModule() {
        super( GlaciersResources.getString( "title.exampleModule" ), ExampleDefaults.CLOCK );

        // Model
        GlaciersClock clock = (GlaciersClock) getClock();
        _model = new ExampleModel( clock );

        // Canvas
        _canvas = new ExampleCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ExampleControlPanel( this );
        setControlPanel( _controlPanel );

        // Clock controls
        _clockControlPanel = new GlaciersClockControlPanel( (GlaciersClock) getClock() );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
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

    public ExampleModel getExampleModel() {
        return _model;
    }

    public ExampleCanvas getExampleCanvas() {
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
            clock.setDt( ExampleDefaults.CLOCK_DT );
            setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );
            
            // ExampleModelElement
            ExampleModelElement exampleModelElement = _model.getExampleModelElement();
            exampleModelElement.setPosition( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
            exampleModelElement.setOrientation( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();
        ExampleModel model = getExampleModel();

        // Module
        config.setActive( isActive() );

        // Model
        {
            // Clock
            GlaciersClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            config.setExampleModelElementPosition( exampleModelElement.getPositionReference() );
            config.setExampleModelElementOrientation( exampleModelElement.getOrientation() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
        
        return config;
    }

    public void load( ExampleConfig config ) {

        ExampleModel model = getExampleModel();

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

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            exampleModelElement.setPosition( config.getExampleModelElementPosition() );
            exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
