/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.neuron.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.controlpanel.NeuronControlPanel;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.model.Axon;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NeuronModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Axon model;
    private NeuronCanvas canvas;
    private NeuronControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NeuronModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_EXAMPLE_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE, NeuronDefaults.CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new Axon( clock );

        // Canvas
        canvas = new NeuronCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new NeuronControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( NeuronStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( NeuronDefaults.CLOCK_TIME_COLUMNS );
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
        NeuronClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
