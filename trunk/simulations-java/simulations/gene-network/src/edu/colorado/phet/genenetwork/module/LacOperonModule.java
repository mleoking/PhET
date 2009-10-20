/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.genenetwork.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.controlpanel.LacOperonControlPanel;
import edu.colorado.phet.genenetwork.model.GeneNetworkClock;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.colorado.phet.genetwork.view.GeneNetworkCanvas;

/**
 * Module template.
 */
public class LacOperonModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private LacOperonModel model;
    private GeneNetworkCanvas canvas;
    private LacOperonControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public LacOperonModule( Frame parentFrame ) {
        super( GeneNetworkStrings.TITLE_EXAMPLE_MODULE, new GeneNetworkClock( LacOperonDefaults.CLOCK_FRAME_RATE, LacOperonDefaults.CLOCK_DT ) );

        // Model
        GeneNetworkClock clock = (GeneNetworkClock) getClock();
        model = new LacOperonModel( clock );

        // Canvas
        canvas = new GeneNetworkCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new LacOperonControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
        // Clock controls
        clockControlPanel = new PiccoloClockControlPanel( getClock() );
        clockControlPanel.setRewindButtonVisible( true );
        clockControlPanel.setTimeDisplayVisible( true );
        clockControlPanel.setUnits( GeneNetworkStrings.UNITS_TIME );
        clockControlPanel.setTimeColumns( LacOperonDefaults.CLOCK_TIME_COLUMNS );
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
        GeneNetworkClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
