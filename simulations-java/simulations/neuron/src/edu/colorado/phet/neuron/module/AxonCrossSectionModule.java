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
import edu.colorado.phet.neuron.controlpanel.AxonCrossSectionControlPanel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.IHodgkinHuxleyModel;
import edu.colorado.phet.neuron.model.NeuronClock;
import edu.colorado.phet.neuron.view.NeuronCanvas;

/**
 * Axon Slice module.
 *
 * @author John Blanco
 */
public class AxonCrossSectionModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private AxonModel model;
    private NeuronCanvas canvas;
    private AxonCrossSectionControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AxonCrossSectionModule( Frame parentFrame ) {
        super( NeuronStrings.TITLE_AXON_CROSS_SECTION_MODULE, new NeuronClock( NeuronDefaults.CLOCK_FRAME_RATE,
        		NeuronDefaults.DEFAULT_ACTION_POTENTIAL_CLOCK_DT ) );

        // Model
        NeuronClock clock = (NeuronClock) getClock();
        model = new AxonModel( clock );
        
        // Canvas
        canvas = new NeuronCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new AxonCrossSectionControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(NeuronDefaults.MIN_ACTION_POTENTIAL_CLOCK_DT, 
    			NeuronDefaults.MAX_ACTION_POTENTIAL_CLOCK_DT, "0.00", (ConstantDtClock)getClock(), " ");
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
    
    public IHodgkinHuxleyModel getHodgkinHuxleyModel(){
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
        
        // Reset the canvas.
        canvas.reset();
    }    
}
