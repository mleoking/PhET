/* Copyright 2008, University of Colorado */


package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Application;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.control.ExampleSubPanel;
import edu.colorado.phet.nuclearphysics2.defaults.ExampleDefaults;
import edu.colorado.phet.nuclearphysics2.model.ExampleModelElement;
import edu.colorado.phet.nuclearphysics2.model.NulcearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.module.example.ExampleCanvas;
import edu.colorado.phet.nuclearphysics2.module.example.ExampleModel;
import edu.colorado.phet.nuclearphysics2.module.example.FakeAlphaRadiationController;
import edu.colorado.phet.nuclearphysics2.persistence.ExampleConfig;
import edu.colorado.phet.nuclearphysics2.view.ExampleNode;


public class AlphaRadiationModule extends PiccoloModule {
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private ExampleModel _model;
    private ExampleCanvas _canvas;
    private AlphaRadiationControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AlphaRadiationModule( Frame parentFrame ) {
        super( NuclearPhysics2Strings.TITLE_ALPHA_RADIATION_MODULE, 
               new NulcearPhysics2Clock( ExampleDefaults.CLOCK_FRAME_RATE, ExampleDefaults.CLOCK_DT ));
 
        // Model
        NulcearPhysics2Clock clock = (NulcearPhysics2Clock) getClock();
        _model = new ExampleModel( clock );

        // Canvas
        _canvas = new ExampleCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new AlphaRadiationControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (NulcearPhysics2Clock) getClock() );
        _clockControlPanel.setUnits( NuclearPhysics2Strings.UNITS_TIME );
        _clockControlPanel.setTimeColumns( ExampleDefaults.CLOCK_TIME_COLUMNS );
        setClockControlPanel( _clockControlPanel );

        // Controller
        // JPB TBD
        /*
        FakeAlphaRadiationController controller = 
            new AlphaRadiationControlPanel( _model, _canvas, _controlPanel );
            */
        
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
        NulcearPhysics2Clock clock = _model.getClock();
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

        // Control panel settings
        ExampleSubPanel subPanel = _controlPanel.getExampleSubPanel();
        subPanel.setPosition( exampleModelElement.getPositionReference() );
        subPanel.setOrientation( exampleModelElement.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Persistence
    //----------------------------------------------------------------------------

    public ExampleConfig save() {

        ExampleConfig config = new ExampleConfig();

        // Module
        config.setActive( isActive() );

        // Clock
        NulcearPhysics2Clock clock = _model.getClock();
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
            NuclearPhysics2Application.instance().setActiveModule( this );
        }

        // Clock
        NulcearPhysics2Clock clock = _model.getClock();
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
