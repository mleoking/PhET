/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse;

import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.greenhouse.model.GreenhouseClock;
import edu.colorado.phet.greenhouse.model.GreenhouseEffectModel;
import edu.colorado.phet.greenhouse.view.GreenhouseEffectCanvas;

/**
 * This class is where the model and view classes are created and connected
 * for the portion of the sim that presents the greenhouse effect within the
 * atmosphere.
 *
 * @author John Blanco
 */
public class GreenhouseEffectModule extends PiccoloModule {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------

	private static final double MAX_TIME_BETWEEN_TICKS = (double)GreenhouseClock.DEFAULT_DELAY_BETWEEN_TICKS * 5;

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

	private GreenhouseEffectModel model;
    private PhetPCanvas canvas;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GreenhouseEffectModule( Frame parentFrame ) {
        super( GreenhouseResources.getString("ModuleTitle.GreenHouseModule"), new GreenhouseClock());
        
        // Physical model
        model = new GreenhouseEffectModel((GreenhouseClock)getClock());

        // Canvas
        canvas = new GreenhouseEffectCanvas(model);
        setSimulationPanel( canvas );
        
        // Control panel
        GreenhouseEffectControlPanel greenhouseEffectControlPanel = new GreenhouseEffectControlPanel();
        setControlPanel(greenhouseEffectControlPanel);

        // Clock controls.
    	// Create the clock control panel, including slider.
        final ConstantDtClock clock = (ConstantDtClock)getClock();
    	PiccoloClockControlPanel clockControlPanel = new PiccoloClockControlPanel( getClock() );
    	final ClockDelaySlider timeSpeedSlider = new ClockDelaySlider(150, 30, "0.00", clock, null);
        timeSpeedSlider.addChangeListener( new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
                clock.setDelay((int)Math.round(MAX_TIME_BETWEEN_TICKS/timeSpeedSlider.getValue()));
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
    // Module overrides
    //----------------------------------------------------------------------------
    
	@Override
    protected JComponent createClockControlPanel( IClock clock ) {
		clockControlPanel = new PiccoloClockControlPanel( clock );
        return clockControlPanel;
    }
}
