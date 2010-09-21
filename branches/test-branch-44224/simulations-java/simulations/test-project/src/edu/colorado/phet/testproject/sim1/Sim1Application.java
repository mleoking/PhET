package edu.colorado.phet.testproject.sim1;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.testproject.EmptyModule;
import edu.colorado.phet.testproject.TestProjectResources;

public class Sim1Application extends PiccoloPhetApplication {
    
    public Sim1Application( PhetApplicationConfig config ) {
        super( config );
        addModule( new EmptyModule( TestProjectResources.getString( "sim1.module1" ) ) );
        addModule( new EmptyModule( TestProjectResources.getString( "sim1.module2" ) ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "test-project", "sim1" ), Sim1Application.class );
    }
}
