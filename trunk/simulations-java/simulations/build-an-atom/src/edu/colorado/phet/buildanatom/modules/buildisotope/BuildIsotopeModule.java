/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.modules.buildisotope;


import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.buildisotope.model.BuildIsotopeModel;
import edu.colorado.phet.buildanatom.modules.buildisotope.view.BuildIsotopeCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Build Isotope tab.
 */
public class BuildIsotopeModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final BuildIsotopeModel model;
    private final BuildIsotopeCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public BuildIsotopeModule() {
        super( BuildAnAtomStrings.TITLE_BUILD_ATOM_MODULE, new BuildAnAtomClock() );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new BuildIsotopeModel( clock );

        // Canvas
        canvas = new BuildIsotopeCanvas( model );
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
    @Override
    public void reset() {

        // reset the clock
        BuildAnAtomClock clock = model.getClock();
        clock.resetSimulationTime();

        // reset the model
        model.reset();
    }
}
