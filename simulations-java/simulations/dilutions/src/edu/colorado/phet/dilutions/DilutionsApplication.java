// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions;

import java.awt.Frame;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.dilutions.modules.BeersLawLabModule;
import edu.colorado.phet.dilutions.modules.BeersLawModule;
import edu.colorado.phet.dilutions.modules.DilutionIntroModule;
import edu.colorado.phet.dilutions.modules.MakeDilutionsModule;
import edu.colorado.phet.dilutions.modules.MolarityModule;

/**
 * Main class for the "Dilutions" application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DilutionsApplication extends PiccoloPhetApplication {

    public DilutionsApplication( PhetApplicationConfig config ) {
        super( config );
        Frame frame = getPhetFrame();
        addModule( new MolarityModule( frame ) );
        addModule( new DilutionIntroModule() );
        addModule( new MakeDilutionsModule() );
        addModule( new BeersLawModule() );
        addModule( new BeersLawLabModule() );
    }

    public static void main( final String[] args ) {
        new PhetApplicationLauncher().launchSim( args, DilutionsResources.PROJECT_NAME, DilutionsApplication.class );
    }
}
