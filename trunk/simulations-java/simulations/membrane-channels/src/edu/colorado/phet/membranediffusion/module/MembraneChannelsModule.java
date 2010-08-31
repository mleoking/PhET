/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.module;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.membranediffusion.MembraneChannelsStrings;
import edu.colorado.phet.membranediffusion.controlpanel.MembraneChannelsControlPanel;
import edu.colorado.phet.membranediffusion.model.MembraneChannelsClock;
import edu.colorado.phet.membranediffusion.model.MembraneChannelsModel;
import edu.colorado.phet.membranediffusion.view.MembraneChannelsCanvas;

/**
 * Membrane Diffusion module.
 *
 * @author John Blanco
 */
public class MembraneChannelsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

	private MembraneChannelsModel model;
    private MembraneChannelsCanvas canvas;
    private MembraneChannelsControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MembraneChannelsModule( Frame parentFrame ) {
        super( MembraneChannelsStrings.TITLE_MEMBRANE_DIFFUSION_MODULE, 
                new MembraneChannelsClock( MembraneChannelsDefaults.CLOCK_FRAME_RATE,
                        MembraneChannelsDefaults.DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT ) );
        // Model
        MembraneChannelsClock clock = (MembraneChannelsClock) getClock();
        model = new MembraneChannelsModel(clock);
        
        // Canvas
        canvas = new MembraneChannelsCanvas(model);
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new MembraneChannelsControlPanel( this, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(MembraneChannelsDefaults.MIN_MEMBRANE_DIFFUSION_CLOCK_DT, 
    			MembraneChannelsDefaults.MAX_MEMBRANE_DIFFUSION_CLOCK_DT, "0.00", (ConstantDtClock)getClock(), null);
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
