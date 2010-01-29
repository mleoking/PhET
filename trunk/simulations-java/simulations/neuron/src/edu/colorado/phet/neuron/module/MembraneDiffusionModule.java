/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.controlpanel.MembraneDiffusionControlPanel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.HodgkinsHuxleyModel2;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Membrane Diffusion module.
 *
 * @author John Blanco
 */
public class MembraneDiffusionModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AxonModel model;
    private NeuronCanvas canvas;
    private MembraneDiffusionControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MembraneDiffusionModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_MEMBRANE_DIFFUSION_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE,
        		NeuronDefaults.CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new AxonModel( clock );
        
        // Canvas
        canvas = new NeuronCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new MembraneDiffusionControlPanel( this, parentFrame, model, canvas );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        setClockControlPanel( clockControlPanel );

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public HodgkinsHuxleyModel2 getHodgkinHuxleyModel(){
    	return model.getHodgkinHuxleyModel();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock
        model.getClock().resetSimulationTime();
        
        // Reset the model.
        model.reset();
    }    
}
