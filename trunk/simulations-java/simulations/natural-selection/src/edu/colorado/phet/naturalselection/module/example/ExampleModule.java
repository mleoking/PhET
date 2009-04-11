/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.naturalselection.module.example;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionStrings;
import edu.colorado.phet.naturalselection.defaults.ExampleDefaults;
import edu.colorado.phet.naturalselection.model.ExampleModelElement;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;
import edu.colorado.phet.naturalselection.persistence.ExampleConfig;
import edu.colorado.phet.naturalselection.view.ExampleNode;

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
    private PiccoloClockControlPanel _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModule( Frame parentFrame ) {
        super( NaturalSelectionStrings.TITLE_EXAMPLE_MODULE, new NaturalSelectionClock( ExampleDefaults.CLOCK_FRAME_RATE, ExampleDefaults.CLOCK_DT ) );

        // Model
        NaturalSelectionClock clock = (NaturalSelectionClock) getClock();
        _model = new ExampleModel( clock );

        // Canvas
        _canvas = new ExampleCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new ExampleControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new PiccoloClockControlPanel( getClock() );
        _clockControlPanel.setRewindButtonVisible( true );
        _clockControlPanel.setTimeDisplayVisible( true );
        _clockControlPanel.setUnits( NaturalSelectionStrings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Controller
        ExampleController controller = new ExampleController( _model, _canvas, _controlPanel );
        
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
        NaturalSelectionClock clock = _model.getClock();
        clock.resetSimulationTime();
        clock.setDt( ExampleDefaults.CLOCK_DT );
        setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        exampleModelElement.setPosition( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION );
        exampleModelElement.setOrientation( ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        
        // ExampleNode
        ExampleNode exampleNode = _canvas.getExampleNode();
        exampleNode.setSize( exampleModelElement.getWidth(), exampleModelElement.getHeight() );
        exampleNode.setPosition( exampleModelElement.getPosition() );
        exampleNode.setOrientation( exampleModelElement.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        NaturalSelectionClock clock = _model.getClock();
        config.setClockDt( clock.getDt() );
        config.setClockRunning( getClockRunningWhenActive() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        config.setExampleModelElementPosition( exampleModelElement.getPositionReference() );
        config.setExampleModelElementOrientation( exampleModelElement.getOrientation() );

        // Control panel settings that are specific to the view
        //XXX
        
        return config;
    }

    public void load( ExampleConfig config ) {

        // Module
        if ( config.isActive() ) {
            NaturalSelectionApplication.instance().setActiveModule( this );
        }

        // Clock
        NaturalSelectionClock clock = _model.getClock();
        clock.setDt( config.getClockDt() );
        setClockRunningWhenActive( config.isClockRunning() );

        // ExampleModelElement
        ExampleModelElement exampleModelElement = _model.getExampleModelElement();
        exampleModelElement.setPosition( config.getExampleModelElementPosition() );
        exampleModelElement.setOrientation( config.getExampleModelElementOrientation() );

        // Control panel settings that are specific to the view
        //XXX
    }
}
