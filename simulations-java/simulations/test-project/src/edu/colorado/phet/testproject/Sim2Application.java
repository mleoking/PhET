package edu.colorado.phet.testproject;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class Sim2Application extends PiccoloPhetApplication {
    public Sim2Application( PhetApplicationConfig config ) {
        super( config );
        addModule( new TestModule( new PhetResources( Sim1Application.PROJECT_NAME ).getLocalizedString( "sim-2.module-name" ) ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, Sim1Application.PROJECT_NAME ), Sim2Application.class );
    }
}