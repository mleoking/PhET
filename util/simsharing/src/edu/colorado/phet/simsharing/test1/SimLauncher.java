// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.test1;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * @author Sam Reid
 */
public class SimLauncher {
    public static void main( String[] commandLineArgs, GravityAndOrbitsApplication application ) {
        PhetFrame frame = application.getPhetFrame();
        if ( commandLineArgs.length > 0 ) {
            frame.setTitle( frame.getTitle() + ": " + Arrays.toString( commandLineArgs ) ); //simsharing, append command line args to title
        }
        if ( Arrays.asList( commandLineArgs ).contains( "-teacher" ) ) {
            application.getGravityAndOrbitsModule().setTeacherMode( true );
            try {
                new SimSharingTeacherClient( application, frame ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            if ( Arrays.asList( commandLineArgs ).contains( "-history" ) ) {//load and play back history
                int index = Arrays.asList( commandLineArgs ).indexOf( "-history" );
                String historyFile = commandLineArgs[index + 1];
                SimHistoryPlayback.playHistory( application, new File( historyFile ) );
            }
        }
        else if ( Arrays.asList( commandLineArgs ).contains( "-student" ) ) {
            try {
                new SimSharingStudentClient( application, frame ).start();
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public static void main( final String[] args ) {
        final GravityAndOrbitsApplication[] launchedApp = new GravityAndOrbitsApplication[1];
        new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsApplication.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                launchedApp[0] = new GravityAndOrbitsApplication( config );
                return launchedApp[0];
            }
        } );
        main( args, launchedApp[0] );
    }
}
