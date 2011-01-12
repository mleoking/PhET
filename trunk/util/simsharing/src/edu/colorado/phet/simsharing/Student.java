// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.awt.*;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.simsharing.test1.SimSharingStudentClient;

/**
 * @author Sam Reid
 */
public class Student {
    public static void main( GravityAndOrbitsApplication application ) throws IOException, AWTException {
        PhetFrame frame = application.getPhetFrame();
        frame.setTitle( frame.getTitle() + ": Student Edition" );
        new SimSharingStudentClient( application ).start();
    }

    public static void main( final String[] args ) throws IOException, AWTException {
        final GravityAndOrbitsApplication[] launchedApp = new GravityAndOrbitsApplication[1];
        new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsApplication.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                launchedApp[0] = new GravityAndOrbitsApplication( config );
                return launchedApp[0];
            }
        } );
        main( launchedApp[0] );
    }
}
