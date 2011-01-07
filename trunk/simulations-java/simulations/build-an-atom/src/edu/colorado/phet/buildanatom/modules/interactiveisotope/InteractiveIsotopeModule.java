/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.buildanatom.modules.interactiveisotope;


import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.view.InteractiveIsotopeCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Build Isotope tab.
 */
public class InteractiveIsotopeModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final InteractiveIsotopeModel model;
    private final InteractiveIsotopeCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public InteractiveIsotopeModule() {
        super( BuildAnAtomStrings.TITLE_INTERACTIVE_ISOTOPE_MODULE, new BuildAnAtomClock() );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new InteractiveIsotopeModel( clock );

        // Canvas
        canvas = new InteractiveIsotopeCanvas( model );
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
