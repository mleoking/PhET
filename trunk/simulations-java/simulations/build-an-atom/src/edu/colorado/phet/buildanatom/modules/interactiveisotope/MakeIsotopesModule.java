// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.interactiveisotope;


import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.MakeIsotopesModel;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.view.MakeIsotopesCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Make Isotopes tab.
 */
public class MakeIsotopesModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final MakeIsotopesModel model;
    private final MakeIsotopesCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MakeIsotopesModule() {
        super( BuildAnAtomStrings.TITLE_INTERACTIVE_ISOTOPE_MODULE, new BuildAnAtomClock() );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new MakeIsotopesModel( clock );

        // Canvas
        canvas = new MakeIsotopesCanvas( model );
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
