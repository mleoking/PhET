// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.energyformsandchanges.energysystems.EnergySystemsModule;
import edu.colorado.phet.energyformsandchanges.intro.EFACIntroModule;

/**
 * Main application class for PhET's Energy Forms and Changes simulation.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class EnergyFormsAndChangesApplication extends PiccoloPhetApplication {

    public EnergyFormsAndChangesApplication( PhetApplicationConfig config ) {
        super( config );

        // Create the modules
        addModule( new EFACIntroModule() );
        addModule( new EnergySystemsModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, EnergyFormsAndChangesResources.PROJECT_NAME, "energy-forms-and-changes", EnergyFormsAndChangesApplication.class );
    }
}