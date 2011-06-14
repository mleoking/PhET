// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.neuron.module;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.controlpanel.AxonCrossSectionControlPanel;
import edu.colorado.phet.neuron.model.IHodgkinHuxleyModel;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.model.NeuronModel;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Axon Slice module.
 *
 * @author John Blanco
 */
public class NeuronModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final NeuronModel model;
    private final NeuronCanvas canvas;
    private final AxonCrossSectionControlPanel controlPanel;
    private final PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public NeuronModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_AXON_CROSS_SECTION_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE,
        		NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new NeuronModel( clock );

        // Canvas
        canvas = new NeuronCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new AxonCrossSectionControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );

        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final SimSpeedControl timeSpeedSlider = new SimSpeedControl(NeuronDefaults.MIN_ACTION_POTENTIAL_CLOCK_DT,
    			NeuronDefaults.MAX_ACTION_POTENTIAL_CLOCK_DT, (ConstantDtClock)getClock(), PhetCommonResources.getString( "Common.sim.speed" ));
        timeSpeedSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
                ((ConstantDtClock)getClock()).setDt( timeSpeedSlider.getValue() );
			}
        } );
        clockControlPanel.addBetweenTimeDisplayAndButtons(timeSpeedSlider);

        // Enable the "Step Back" button.
        clockControlPanel.setStepBackButtonVisible(true);

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

    public IHodgkinHuxleyModel getHodgkinHuxleyModel(){
    	return model.getHodgkinHuxleyModel();
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    @Override
    public void reset() {

        // Reset the clock
        model.getClock().resetSimulationTime();

        // Reset the model.
        model.reset();

        // Reset the canvas.
        canvas.reset();

        // Make sure the clock starts off running.
        setClockRunningWhenActive( true );

    }
}
