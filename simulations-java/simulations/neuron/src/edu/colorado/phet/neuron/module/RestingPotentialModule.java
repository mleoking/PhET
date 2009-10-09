/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.controlpanel.NeuronControlPanel;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.ParticlePosition;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Module that illustrates the concept of Resting Potential.
 *
 * @author John Blanco
 */
public class RestingPotentialModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AxonModel model;
    private NeuronCanvas canvas;
    private NeuronControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public RestingPotentialModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_RESTING_POTENTIAL_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE,
        		NeuronDefaults.CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new AxonModel( clock );
        
        // Initialize the model as needed for this module.
        model.addParticles(ParticleType.PROTEIN_ION, ParticlePosition.INSIDE_MEMBRANE, 50);
        model.addParticles(ParticleType.POTASSIUM_ION, ParticlePosition.INSIDE_MEMBRANE, 50);

        // Canvas
        canvas = new NeuronCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new NeuronControlPanel( this, parentFrame, model, canvas );
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
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {

        // Reset the clock
        NeuronClock clock = model.getClock();
        clock.resetSimulationTime();
        
        // Reset the model.
        model.reset();
    }    
}
