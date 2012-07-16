// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energystories;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.energystories.view.EnergyStoriesCanvas;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemsModel;

/**
 * The "Energy Stories" module for the Energy Forms and Changes simulation.
 * This is where the model and canvas are hooked together.
 *
 * @author John Blanco
 */
public class EnergyStoriesModule extends SimSharingPiccoloModule {

    private final EnergySystemsModel model;

    public EnergyStoriesModule() {
        this( new EnergySystemsModel() );
    }

    private EnergyStoriesModule( EnergySystemsModel model ) {
        super( EnergyFormsAndChangesSimSharing.UserComponents.energyStoriesTab, EnergyFormsAndChangesResources.Strings.ENERGY_STORIES, model.getClock() );
        this.model = model;
        setClockControlPanel( null );
        setSimulationPanel( new EnergyStoriesCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
