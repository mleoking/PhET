// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.intro.model.EFACIntroModel;
import edu.colorado.phet.energyformsandchanges.intro.view.EFACIntroCanvas;

/**
 * The "Intro" module for the Energy Forms and Changes simulation.  This is
 * where the model and canvas are hooked together.
 *
 * @author John Blanco
 */
public class EFACIntroModule extends SimSharingPiccoloModule {

    private EFACIntroModel model;

    public EFACIntroModule() {
        this( new EFACIntroModel() );
    }

    private EFACIntroModule( EFACIntroModel model ) {
        super( EnergyFormsAndChangesSimSharing.UserComponents.introTab, EnergyFormsAndChangesResources.Strings.INTRO, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        setSimulationPanel( new EFACIntroCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
