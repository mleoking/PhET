/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.module;

import java.awt.Frame;

import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.controlpanel.BuildAnAtomControlPanel;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.view.BuildAnAtomCanvas;

/**
 * Module template.
 */
public class BuildAnAtomModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAnAtomModel model;
    private BuildAnAtomCanvas canvas;
    private BuildAnAtomControlPanel controlPanel;
    private PiccoloClockControlPanel clockControlPanel;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildAnAtomModule( Frame parentFrame ) {
        super( BuildAnAtomStrings.TITLE_BUILD_ATOM_MODULE, new BuildAnAtomClock( BuildAnAtomDefaults.CLOCK_FRAME_RATE, BuildAnAtomDefaults.CLOCK_DT ) );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new BuildAnAtomModel( clock );

        // Canvas
        canvas = new BuildAnAtomCanvas( model );
        setSimulationPanel( canvas );

        // Control Panel
        controlPanel = new BuildAnAtomControlPanel( this, parentFrame, model );
        setControlPanel( controlPanel );
        
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
        BuildAnAtomClock clock = model.getClock();
        clock.resetSimulationTime();
    }    
}
