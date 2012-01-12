// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup.CenteredWithSize;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:20:53 PM
 */

public class TravoltageApplication extends PhetApplication {

    public TravoltageApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new TravoltageModule() );
    }

    public static void main( final String[] args ) {

        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new TravoltageApplication( config );
            }
        };

        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, TravoltageConstants.PROJECT_NAME );
        appConfig.setFrameSetup( new CenteredWithSize( 800, 600 ) );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
