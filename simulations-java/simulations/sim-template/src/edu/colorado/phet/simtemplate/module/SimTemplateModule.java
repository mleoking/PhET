/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.simtemplate.SimTemplateStrings;
import edu.colorado.phet.simtemplate.controlpanel.SimTemplateControlPanel;
import edu.colorado.phet.simtemplate.model.SimTemplateClock;
import edu.colorado.phet.simtemplate.model.SimTemplateModel;
import edu.colorado.phet.simtemplate.view.SimTemplateCanvas;

/**
 * ExampleModule is the "Example" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimTemplateModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private SimTemplateModel model;
    private SimTemplateCanvas canvas;
    private SimTemplateControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public SimTemplateModule( Frame parentFrame ) {
        super( SimTemplateStrings.TITLE_EXAMPLE_MODULE, new SimTemplateClock( SimTemplateDefaults.CLOCK_FRAME_RATE, SimTemplateDefaults.CLOCK_DT ) );

        // Model
        SimTemplateClock clock = (SimTemplateClock) getClock();
        model = new SimTemplateModel( clock );

        // Canvas
        canvas = new SimTemplateCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new SimTemplateControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( SimTemplateStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( SimTemplateDefaults.CLOCK_TIME_COLUMNS );
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
        SimTemplateClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
