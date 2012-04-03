// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems;

import edu.colorado.phet.common.piccolophet.SimSharingPiccoloModule;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.energysystems.model.EnergySystemsModel;
import edu.colorado.phet.energyformsandchanges.energysystems.view.EnergySystemsCanvas;

/**
 * The "Energy Systems" module for the Energy Forms and Changes simulation.
 * This is where the model and canvas are hooked together.
 *
 * @author John Blanco
 */
public class EnergySystemsModule extends SimSharingPiccoloModule {

    private EnergySystemsModel model;

    public EnergySystemsModule() {
        this( new EnergySystemsModel() );
    }

    private EnergySystemsModule( EnergySystemsModel model ) {
        super( EnergyFormsAndChangesSimSharing.UserComponents.energySystemsTab, EnergyFormsAndChangesResources.Strings.ENERGY_SYSTEMS, model.getClock() );
        this.model = model;
        setLogoPanel( null ); // Turn off the "logo panel", which is the control panel on right side.
        setClockControlPanel( null );
        setSimulationPanel( new EnergySystemsCanvas( model ) );
        reset();
    }

    @Override public void reset() {
        model.reset();
    }
}
