// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.modules.isotopemixture;


import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.interactiveisotope.model.InteractiveIsotopeModel;
import edu.colorado.phet.buildanatom.modules.isotopemixture.view.IsotopeMixtureCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;

/**
 * Module for the Build Isotope tab.
 */
public class IsotopeMixtureModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final InteractiveIsotopeModel model; // TODO: Wrong model, temp to get things working.
    private final IsotopeMixtureCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public IsotopeMixtureModule() {
        super( BuildAnAtomStrings.TITLE_ISOTOPE_MIXTURES_MODULE, new BuildAnAtomClock() );
        setClockControlPanel( null );

        // Model
        BuildAnAtomClock clock = (BuildAnAtomClock) getClock();
        model = new InteractiveIsotopeModel( clock );

        // Canvas
        canvas = new IsotopeMixtureCanvas( model );
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
