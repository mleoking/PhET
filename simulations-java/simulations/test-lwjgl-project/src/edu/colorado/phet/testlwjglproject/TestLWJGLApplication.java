// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.testlwjglproject.lwjgl.StartupUtils;

public class TestLWJGLApplication extends PiccoloPhetApplication {

    public TestLWJGLApplication( PhetApplicationConfig config ) {
        super( config );
        addModule( new TestModule( TestProjectResources.getString( "sim1.module1" ) ) );
//        addModule( new TestModule( TestProjectResources.getString( "sim1.module2" ) ) );
        addModule( new EmptyModule( TestProjectResources.getString( "sim1.module2" ) ) );
    }

    public static void main( String[] args ) {
        try {
            StartupUtils.setupLibraries();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "test-lwjgl-project", "sim1" ), TestLWJGLApplication.class );
    }
}
