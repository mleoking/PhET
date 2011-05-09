// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture;


import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.isotopemixture.model.MixIsotopesModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.view.MixIsotopesCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Mix Isotopes module.
 */
public class MixIsotopesModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final MixIsotopesModel model;
    private final MixIsotopesCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MixIsotopesModule() {
        super( BuildAnAtomStrings.TITLE_ISOTOPE_MIXTURES_MODULE, new BuildAnAtomClock() );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new MixIsotopesModel( clock );

        // Canvas
        canvas = new MixIsotopesCanvas( model );
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
