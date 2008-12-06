package edu.colorado.phet.testproject;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class Sim1Application extends PiccoloPhetApplication {
    public static final String PROJECT_NAME = "test-project";

    public Sim1Application( PhetApplicationConfig config ) {
        super( config );
        addModule( new TestModule( new PhetResources( PROJECT_NAME ).getLocalizedString( "sim-2.module-name" ) ) );
        addModule( new TestModule( new PhetResources( PROJECT_NAME ).getLocalizedString( "sim-2.module-name" ) ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, PROJECT_NAME ), Sim1Application.class );
    }
}
