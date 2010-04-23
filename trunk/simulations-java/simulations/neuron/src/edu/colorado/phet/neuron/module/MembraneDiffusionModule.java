/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.module;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.controlpanel.MembraneDiffusionControlPanel;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.view.MembraneDiffusionCanvas;

/**
 * Membrane Diffusion module.
 *
 * @author John Blanco
 */
public class MembraneDiffusionModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

	private MembraneDiffusionModel model;
    private MembraneDiffusionCanvas canvas;
    private MembraneDiffusionControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MembraneDiffusionModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_MEMBRANE_DIFFUSION_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE,
        		NeuronDefaults.DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new MembraneDiffusionModel(clock);
        
        // Canvas
        canvas = new MembraneDiffusionCanvas(model);
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new MembraneDiffusionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(NeuronDefaults.MIN_MEMBRANE_DIFFUSION_CLOCK_DT, 
    			NeuronDefaults.MAX_MEMBRANE_DIFFUSION_CLOCK_DT, "0.00", (ConstantDtClock)getClock(), null);
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                ((ConstantDtClock)getClock()).setDt( timeSpeedSlider.getValue() );
            }
        } );
    	clockControlPanel.addBetweenTimeDisplayAndButtons(timeSpeedSlider);
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
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
    	
    	// Reset the model.
    	model.reset();
    }    
}
