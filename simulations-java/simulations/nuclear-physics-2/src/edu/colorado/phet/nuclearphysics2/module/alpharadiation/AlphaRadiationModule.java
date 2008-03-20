/* Copyright 2008, University of Colorado */

package edu.colorado.phet.nuclearphysics2.module.alpharadiation;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Application;
import edu.colorado.phet.nuclearphysics2.NuclearPhysics2Strings;
import edu.colorado.phet.nuclearphysics2.control.ExampleSubPanel;
import edu.colorado.phet.nuclearphysics2.defaults.AlphaRadiationDefaults;
import edu.colorado.phet.nuclearphysics2.defaults.ExampleDefaults;
import edu.colorado.phet.nuclearphysics2.model.ExampleModelElement;
import edu.colorado.phet.nuclearphysics2.model.NuclearPhysics2Clock;
import edu.colorado.phet.nuclearphysics2.module.example.ExampleCanvas;
import edu.colorado.phet.nuclearphysics2.module.example.ExampleModel;
import edu.colorado.phet.nuclearphysics2.persistence.ExampleConfig;
import edu.colorado.phet.nuclearphysics2.view.ExampleNode;

/**
 * This class is where the model and view classes for the Alpha Radiation
 * tab of this simulation are created and contained. 
 *
 * @author John Blanco
 */
public class AlphaRadiationModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AlphaRadiationModel _model;
    private AlphaRadiationCanvas _canvas;
    private AlphaRadiationControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AlphaRadiationModule( Frame parentFrame ) {
        super( NuclearPhysics2Strings.TITLE_ALPHA_RADIATION_MODULE,
               new NuclearPhysics2Clock( AlphaRadiationDefaults.CLOCK_FRAME_RATE, AlphaRadiationDefaults.CLOCK_DT ));
 
        // Model
        NuclearPhysics2Clock clock = (NuclearPhysics2Clock) getClock();
        _model = new AlphaRadiationModel(clock);

        // Canvas
        _canvas = new AlphaRadiationCanvas( _model );
        setSimulationPanel( _canvas );

        // Control Panel
        _controlPanel = new AlphaRadiationControlPanel( this, parentFrame );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (NuclearPhysics2Clock) getClock() );
        _clockControlPanel.setUnits( NuclearPhysics2Strings.UNITS_TIME );
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
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Clock
        /* TODO: JPB TBD - Need to implement reset functionality.
        NuclearPhysics2Clock clock = _model.getClock();
        clock.setDt( ExampleDefaults.CLOCK_DT );
        setClockRunningWhenActive( ExampleDefaults.CLOCK_RUNNING );
        */

        // ExampleNode
        /*
        ExampleNode exampleNode = _canvas.getExampleNode();
        exampleNode.setSize( exampleModelElement.getWidth(), exampleModelElement.getHeight() );
        exampleNode.setPosition( exampleModelElement.getPosition() );
        exampleNode.setOrientation( exampleModelElement.getOrientation() );
        */

        // Control panel settings
        /*
        ExampleSubPanel subPanel = _controlPanel.getExampleSubPanel();
        subPanel.setPosition( exampleModelElement.getPositionReference() );
        subPanel.setOrientation( exampleModelElement.getOrientation() );
        */
    }
}
