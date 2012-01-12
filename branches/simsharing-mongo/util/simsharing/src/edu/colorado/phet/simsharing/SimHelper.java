// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;
import edu.colorado.phet.simsharingtestsim.SimSharingTestSim;

/**
 * Factored out code that launched GravityAndOrbitsApplication and returns a reference to the created application, used by Teacher and Student.
 *
 * @author Sam Reid
 */
public class SimHelper {

    public static final Function0<GravityAndOrbitsApplication> GRAVITY_AND_ORBITS_LAUNCHER = new Function0<GravityAndOrbitsApplication>() {
        public GravityAndOrbitsApplication apply() {
            //TODO: this skips splash screen, statistics, etc.
            final GravityAndOrbitsApplication[] myapp = new GravityAndOrbitsApplication[1];
            Runnable runnable = new Runnable() {
                public void run() {
                    GravityAndOrbitsApplication app = new GravityAndOrbitsApplication( new PhetApplicationConfig( new String[0], GravityAndOrbitsApplication.PROJECT_NAME ) );
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
    };

    static final Function0<SimSharingTestSim> TEST_SIM_LAUNCHER = new Function0<SimSharingTestSim>() {
        public SimSharingTestSim apply() {
            //TODO: this skips splash screen, statistics, etc.
            final SimSharingTestSim[] myapp = new SimSharingTestSim[1];
            Runnable runnable = new Runnable() {
                public void run() {
                    SimSharingTestSim app = new SimSharingTestSim( new PhetApplicationConfig( new String[0], SimSharingTestSim.PROJECT_NAME ) );
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
    };

    public static Function0<GravityAndOrbitsApplication> LAUNCHER = GRAVITY_AND_ORBITS_LAUNCHER;
}