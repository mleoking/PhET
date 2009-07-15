/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.titration.TitrationApplication;
import edu.colorado.phet.titration.TitrationStrings;
import edu.colorado.phet.titration.defaults.ExampleDefaults;
import edu.colorado.phet.titration.model.ExampleModelElement;
import edu.colorado.phet.titration.model.SimTemplateClock;
import edu.colorado.phet.titration.persistence.ExampleConfig;
import edu.colorado.phet.titration.view.ExampleNode;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModel model;
    private ExampleCanvas canvas;
    private ExampleControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModule( Frame parentFrame ) {
        super( TitrationStrings.TITLE_EXAMPLE_MODULE, new SimTemplateClock( ExampleDefaults.CLOCK_FRAME_RATE, ExampleDefaults.CLOCK_DT ) );

        // Model
        SimTemplateClock clock = (SimTemplateClock) getClock();
        model = new ExampleModel( clock );

        // Canvas
        canvas = new ExampleCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new ExampleControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( TitrationStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( clockControlPanel );

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
        SimTemplateClock clock = model.getClock();
        clock.resetSimulationTime();
        
        // load the default configuration
        load( ExampleDefaults.getInstance().getConfig() );
        
//        // Clock
//        SimTemplateClock clock = model.getClock();
//        clock.resetSimulationTime();
//        clock.setDt( ExampleDefaults.CLOCK_DT );
//        setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );
//
//        // ExampleModelElement
//        ExampleModelElement exampleModelElement = model.getExampleModelElement();
//        exampleModelElement.setPosition( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
//        exampleModelElement.setOrientation( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
//        
//        // ExampleNode
//        ExampleNode exampleNode = canvas.getExampleNode();
//        exampleNode.setSize( exampleModelElement.getWidth(), exampleModelElement.getHeight() );
//        exampleNode.setPosition( exampleModelElement.getPosition() );
//        exampleNode.setOrientation( exampleModelElement.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        SimTemplateClock clock = model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = model.getExampleModelElement();
        config.setExampleModelElementPosition( exampleModelElement.getPositionReference() );
        config.setExampleModelElementOrientation( exampleModelElement.getOrientation() );

        // Control panel settings that are specific to the view
        //XXX
        
        return config;
    }

    public void load( ExampleConfig config ) {

        // Module
        if ( config.isActive() ) {
            TitrationApplication.getInstance().setActiveModule( this );
        }

        // Clock
        SimTemplateClock clock = model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = model.getExampleModelElement();
        exampleModelElement.setPosition( config.getExampleModelElementPosition() );
        exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );

        // Control panel settings that are specific to the view
        //XXX
    }
}
