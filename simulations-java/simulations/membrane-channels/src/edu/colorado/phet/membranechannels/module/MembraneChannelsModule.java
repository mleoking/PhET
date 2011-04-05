// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.membranechannels.module;

import java.awt.Frame;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.membranechannels.MembraneChannelsStrings;
import edu.colorado.phet.membranechannels.controlpanel.MembraneChannelsControlPanel;
import edu.colorado.phet.membranechannels.model.MembraneChannelsClock;
import edu.colorado.phet.membranechannels.model.MembraneChannelsModel;
import edu.colorado.phet.membranechannels.view.MembraneChannelsCanvas;

/**
 * Membrane Channels module.
 *
 * @author John Blanco
 */
public class MembraneChannelsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

	private final MembraneChannelsModel model;
    private final MembraneChannelsCanvas canvas;
    private final MembraneChannelsControlPanel controlPanel;
    private final PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MembraneChannelsModule( Frame parentFrame ) {
        super( MembraneChannelsStrings.TITLE_MEMBRANE_CHANNELS_MODULE,
                new MembraneChannelsClock( MembraneChannelsDefaults.CLOCK_FRAME_RATE,
                        MembraneChannelsDefaults.DEFAULT_MEMBRANE_CHANNELS_CLOCK_DT ) );
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
    	final SimSpeedControl timeSpeedSlider = new SimSpeedControl(MembraneChannelsDefaults.MIN_MEMBRANE_CHANNELS_CLOCK_DT,
    			MembraneChannelsDefaults.MAX_MEMBRANE_CHANNELS_CLOCK_DT, (ConstantDtClock)getClock());
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
    @Override
    public void reset() {

    	// Reset the model.
    	model.reset();
    }
}
