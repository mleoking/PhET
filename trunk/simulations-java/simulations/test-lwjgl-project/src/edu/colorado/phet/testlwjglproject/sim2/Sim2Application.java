// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.sim2;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.testlwjglproject.EmptyModule;
import edu.colorado.phet.testlwjglproject.TestProjectConstants;
import edu.colorado.phet.testlwjglproject.TestProjectResources;

public class Sim2Application extends PiccoloPhetApplication {
    public Sim2Application( PhetApplicationConfig config ) {
        super( config );
        addModule( new EmptyModule( TestProjectResources.getString( "sim2.module1" ) ) );
        addModule( new EmptyModule( TestProjectResources.getString( "sim2.module2" ) ) );
    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, TestProjectConstants.PROJECT_NAME, "sim2" ), Sim2Application.class );
    }
}