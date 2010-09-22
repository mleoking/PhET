/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.buildanatom.module;

import java.awt.Frame;

import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.model.BuildAnAtomModel;
import edu.colorado.phet.buildanatom.view.TestingCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * This module exists for trying out visual ideas, prototypes, etc.  At the
 * very end of the project when everything is pretty much worked out, this
 * should be removed.
 */
public class TestingModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private BuildAnAtomModel model;
    private TestingCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public TestingModule( Frame parentFrame ) {
        super( "Fuzz Test", new BuildAnAtomClock( BuildAnAtomDefaults.CLOCK_FRAME_RATE, BuildAnAtomDefaults.CLOCK_DT ) );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new BuildAnAtomModel( clock );

        // Canvas
        canvas = new TestingCanvas( model );
        setSimulationPanel( canvas );

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
