/* Copyright 2009, University of Colorado */

package edu.colorado.phet.membranediffusion.module;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.TimeSpeedSlider;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.membranediffusion.MembraneDiffusionStrings;
import edu.colorado.phet.membranediffusion.controlpanel.MembraneDiffusionControlPanel;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionClock;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.view.MembraneDiffusionCanvas;

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
        super( MembraneDiffusionStrings.TITLE_MEMBRANE_DIFFUSION_MODULE, new MembraneDiffusionClock( MembraneDiffusionDefaults.CLOCK_FRAME_RATE,
        		MembraneDiffusionDefaults.DEFAULT_MEMBRANE_DIFFUSION_CLOCK_DT ) );

        // Model
        MembraneDiffusionClock clock = (MembraneDiffusionClock) getClock();
        model = new MembraneDiffusionModel(clock);
        
        // Canvas
        canvas = new MembraneDiffusionCanvas(model);
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new MembraneDiffusionControlPanel( this, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider(MembraneDiffusionDefaults.MIN_MEMBRANE_DIFFUSION_CLOCK_DT, 
    			MembraneDiffusionDefaults.MAX_MEMBRANE_DIFFUSION_CLOCK_DT, "0.00", (ConstantDtClock)getClock(), null);
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
