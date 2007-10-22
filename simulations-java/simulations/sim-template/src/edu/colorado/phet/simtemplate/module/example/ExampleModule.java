/* Copyright 2007, University of Colorado */

package edu.colorado.phet.simtemplate.module.example;

import java.awt.Dimension;
import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.simtemplate.TemplateApplication;
import edu.colorado.phet.simtemplate.TemplateResources;
import edu.colorado.phet.simtemplate.defaults.ExampleDefaults;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.TemplateClock;
import edu.colorado.phet.simtemplate.persistence.ExampleConfig;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModel _model;
    private ExampleCanvas _canvas;
    private ExampleControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModule( Frame parentFrame ) {
        super( TemplateResources.getString( "title.exampleModule" ), ExampleDefaults.CLOCK );

        // Model
        TemplateClock clock = (TemplateClock) getClock();
        _model = new ExampleModel( clock );

        // Canvas
        _canvas = new ExampleCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ExampleControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );

        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (TemplateClock) getClock() );
        _clockControlPanel.setUnits( TemplateResources.getString( "units.time" ) );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

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
     * Resets the module.
     */
    public void reset() {

        // Model
        {
            // Clock
            TemplateClock clock = _model.getClock();
            clock.setDt( ExampleDefaults.CLOCK_DT );
            setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );
            
            // ExampleModelElement
            ExampleModelElement exampleModelElement = _model.getExampleModelElement();
            exampleModelElement.setSize( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_SIZE );
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
            TemplateClock clock = model.getClock();
            config.setClockDt( clock.getDt() );
            config.setClockRunning( getClockRunningWhenActive() );

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            config.setExampleModelElementWidth( exampleModelElement.getSizeReference().getWidth() );
            config.setExampleModelElementHeight( exampleModelElement.getSizeReference().getHeight() );
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
            TemplateApplication.instance().setActiveModule( this );
        }

        // Model
        {
            // Clock
            TemplateClock clock = model.getClock();
            clock.setDt( config.getClockDt() );
            setClockRunningWhenActive( config.isClockRunning() );

            // ExampleModelElement
            ExampleModelElement exampleModelElement = model.getExampleModelElement();
            exampleModelElement.setSize( new Dimension( (int) config.getExampleModelElementWidth(), (int) config.getExampleModelElementHeight() ) );
            exampleModelElement.setPosition( config.getExampleModelElementPosition() );
            exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );
        }

        // Control panel settings that are view-related
        {
            //XXX
        }
    }
}
