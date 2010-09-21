package edu.colorado.phet.theramp.v2.view;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

public class TestRamp extends PiccoloPhetApplication {
    public TestRamp( PhetApplicationConfig config ) {
        super( config );
        addModule( new TestRampModule() );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( args, "the-ramp", TestRamp.class );
    }
}
