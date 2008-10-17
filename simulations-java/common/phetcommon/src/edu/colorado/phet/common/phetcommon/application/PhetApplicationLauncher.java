package edu.colorado.phet.common.phetcommon.application;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

/**
 * This launcher solves the following problems:
 * 1. Consolidate (instead of duplicate) launch code
 * 2. Make sure that all PhetSimulations launch in the Swing Event Thread
 * Note: The application main class should not invoke any unsafe Swing operations outside of the Swing thread.
 * 3. Make sure all PhetSimulations instantiate and use a PhetLookAndFeel, which is necessary to enable font support for many laungages.
 * <p/>
 * This implementation uses ApplicationConstructor instead of reflection to ensure compile-time checking (at the expense of slightly more complicated subclass implementations).
 */
public class PhetApplicationLauncher {

    //for splash window
    private AWTSplashWindow splashWindow;
    private Frame splashWindowOwner;

    private void showSplashWindow( String title ) {
        if ( splashWindow == null ) {
            // PhetFrame doesn't exist when this is called, so create and manage the window's owner.
            splashWindowOwner = new Frame();
            splashWindow = new AWTSplashWindow( splashWindowOwner, title );
            splashWindow.show();
        }
    }

    private void disposeSplashWindow() {
        if ( splashWindow != null ) {
            splashWindow.dispose();
            splashWindow = null;
            // Clean up the window's owner that we created in showSplashWindow.
            splashWindowOwner.dispose();
            splashWindowOwner = null;
        }
    }

    public void launchSim( String[] commandLineArgs, String project, final Class applicationClass ) {
        launchSim( commandLineArgs, project, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    return createApplication( config, applicationClass );
                }
                catch( Exception e ) {
                    throw new RuntimeException( e );
                }
            }
        } );
    }

    public void launchSim( String[] commandLineArgs, String project, String flavor, final Class applicationClass ) {
        launchSim( commandLineArgs, project, flavor, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                try {
                    return createApplication( config, applicationClass );
                }
                catch( Exception e ) {
                    throw new RuntimeException( e );
                }
            }
        } );
    }

    private PhetApplication createApplication( PhetApplicationConfig config, Class applicationClass ) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return (PhetApplication) applicationClass.getConstructor( new Class[]{config.getClass()} ).newInstance( new Object[]{config} );
    }

    public void launchSim( String[] commandLineArgs, String project, ApplicationConstructor applicationConstructor ) {
        launchSim( new PhetApplicationConfig( commandLineArgs, null, project ), applicationConstructor );
    }

    public void launchSim( String[] commandLineArgs, String project, String flavor, ApplicationConstructor applicationConstructor ) {
        launchSim( new PhetApplicationConfig( commandLineArgs, null, project, flavor ), applicationConstructor );
    }

    public void launchSim( final PhetApplicationConfig config, final ApplicationConstructor applicationConstructor ) {
        /*
         * Wrap the body of main in invokeLater, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeLater.
         */
        try {
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {

                    config.getLookAndFeel().initLookAndFeel();
                    if ( applicationConstructor != null ) {

                        showSplashWindow( config.getName() );
                        PhetApplication app = applicationConstructor.getApplication( config );
                        app.startApplication();
                        disposeSplashWindow();
                        long applicationLaunchFinishedAt = System.currentTimeMillis();
                        config.setApplicationLaunchFinishedAt( applicationLaunchFinishedAt );

                        new TrackingApplicationManager( config ).applicationStarted( app );
                        new UpdateApplicationManager( config ).applicationStarted( app );
                    }
                    else {
                        new RuntimeException( "No applicationconstructor specified" ).printStackTrace();
                    }
                }
            } );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        catch( InvocationTargetException e ) {
            e.printStackTrace();
        }
    }
}
