// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab;

import edu.colorado.phet.beerslawlab.modules.BeersLawModule;
import edu.colorado.phet.beerslawlab.modules.ConcentrationModule;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * Main class for the "Beer's Law Lab" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawLabApplication extends PiccoloPhetApplication {

    public BeersLawLabApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new ConcentrationModule() );
        addModule( new BeersLawModule() );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, BLLResources.PROJECT_NAME, BeersLawLabApplication.class );
    }
}
