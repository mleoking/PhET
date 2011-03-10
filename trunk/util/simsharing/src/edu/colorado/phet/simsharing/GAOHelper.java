// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * Factored out code that launched GravityAndOrbitsApplication and returns a reference to the created application, used by Teacher and Student.
 *
 * @author Sam Reid
 */
public class GAOHelper {
    public static GravityAndOrbitsApplication launchApplication( final String[] args, final VoidFunction0 exitAction ) {
        //TODO: this skips splash screen, statistics, etc.
        final GravityAndOrbitsApplication[] myapp = new GravityAndOrbitsApplication[1];
        Runnable runnable = new Runnable() {
            public void run() {
                GravityAndOrbitsApplication app = new GravityAndOrbitsApplication( new PhetApplicationConfig( args, GravityAndOrbitsApplication.PROJECT_NAME ) ) {
                    @Override
                    public void exit() {
                        exitAction.apply();
                    }
                };
                app.startApplication();
                myapp[0] = app;
            }
        };
        if ( SwingUtilities.isEventDispatchThread() ) {
            runnable.run();
        }
        else {
            try {
                SwingUtilities.invokeAndWait( runnable );
            }
            catch ( InterruptedException e ) {
                e.printStackTrace();
            }
            catch ( InvocationTargetException e ) {
                e.printStackTrace();
            }
        }
        return myapp[0];
    }
}
