// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * Factored out code that launched GravityAndOrbitsApplication and returns a reference to the created application, used by Teacher and Student.
 *
 * @author Sam Reid
 */
public class GAOHelper {
    public static GravityAndOrbitsApplication launchApplication( String[] args, final VoidFunction0 exitAction ) {
        final GravityAndOrbitsApplication[] launchedApp = new GravityAndOrbitsApplication[1];
        new PhetApplicationLauncher().launchSim( args, GravityAndOrbitsApplication.PROJECT_NAME, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                launchedApp[0] = new GravityAndOrbitsApplication( config ) {
                    @Override
                    public void exit() {
                        exitAction.apply();
                    }
                };
                return launchedApp[0];
            }
        } );
        final GravityAndOrbitsApplication application = launchedApp[0];
        return application;
    }
}
