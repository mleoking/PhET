package edu.colorado.phet.common.phetcommon.view.util;

import java.lang.reflect.Constructor;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;


public class PhetSimLauncher {
    /**
     * Launches a simulation (child of NonPiccoloPhetApplication) that has a
     * constructor accepting a String array (the command line arguments of the
     * program).
     *
     * @param args            The arguments passed to the main() method.
     * @param phetApplication The class that extends NonPiccoloPhetApplication.
     */
    public static void launchSim( final String[] args, final Class phetApplication ) {
        
        if ( !PhetApplication.class.isAssignableFrom( phetApplication ) ) {
            throw new IllegalArgumentException( "The class " + phetApplication.getName() + " should extend NonPiccoloPhetApplication." );
        }

        final Class stringClass = ( new String[0] ).getClass();

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    // Construct and start the application in the event-dispatch thread.
                    // Application construction involves realization of Swing components, and
                    // Sun now recommends doing all Swing realization in the event dispatch thread.
                    // And all code that might affect or depend on the state of Swing components  
                    // should be executed in the event-dispatching thread.
                    Constructor constructor = phetApplication.getConstructor( new Class[] { stringClass } );
                    PhetApplication simulation = (PhetApplication) constructor.newInstance( new Object[] { args } );
                    simulation.startApplication();
                }
                catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );

    }
}
