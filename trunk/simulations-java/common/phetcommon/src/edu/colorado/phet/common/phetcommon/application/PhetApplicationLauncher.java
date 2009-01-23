package edu.colorado.phet.common.phetcommon.application;

import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.tracking.SessionMessage;
import edu.colorado.phet.common.phetcommon.tracking.StatisticsManager;
import edu.colorado.phet.common.phetcommon.updates.UpdatesManager;

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
            splashWindow.setVisible( true );
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

    public void launchSim( String[] commandLineArgs, String project, final Class phetApplicationClass ) {
        launchSim( commandLineArgs, project, new ReflectionApplicationConstructor( phetApplicationClass ) );
    }

    public static class ReflectionApplicationConstructor implements ApplicationConstructor {
        private Class phetApplicationClass;

        public ReflectionApplicationConstructor( Class phetApplicationClass ) {
            this.phetApplicationClass = phetApplicationClass;
        }

        public PhetApplication getApplication( PhetApplicationConfig config ) {
            try {
                return (PhetApplication) phetApplicationClass.getConstructor( new Class[]{config.getClass()} ).newInstance( new Object[]{config} );
            }
            catch( Exception e ) {
                throw new RuntimeException( e );
            }
        }

    }

    public void launchSim( String[] commandLineArgs, String project, String flavor, final Class phetApplicationClass ) {
        launchSim( commandLineArgs, project, flavor, new ReflectionApplicationConstructor( phetApplicationClass ) );
    }

    public void launchSim( String[] commandLineArgs, String project, ApplicationConstructor applicationConstructor ) {
        launchSim( new PhetApplicationConfig( commandLineArgs, project ), applicationConstructor );
    }

    public void launchSim( String[] commandLineArgs, String project, String flavor, ApplicationConstructor applicationConstructor ) {
        launchSim( new PhetApplicationConfig( commandLineArgs, project, flavor ), applicationConstructor );
    }

    public void launchSim( final PhetApplicationConfig config, final Class phetApplicationClass ) {
        launchSim( config, new ReflectionApplicationConstructor( phetApplicationClass ) );
    }

    public void launchSim( final PhetApplicationConfig config, final ApplicationConstructor applicationConstructor ) {
        /*
         * Wrap the body of main in invokeAndWait, so that all initialization occurs
         * in the event dispatch thread. Sun now recommends doing all Swing init in
         * the event dispatch thread. And the Piccolo-based tabs in TabbedModulePanePiccolo
         * seem to cause startup deadlock problems if they aren't initialized in the
         * event dispatch thread. Since we don't have an easy way to separate Swing and
         * non-Swing init, we're stuck doing everything in invokeAndWait.
         */
        try {

            //Use invoke and wait since many older PhET simulations
            //require the existence of a reference to the PhetApplication as soon as launchSim exits
            //
            //If/when these references have been changed/removed, we can change this to invokeLater()
            SwingUtilities.invokeAndWait( new Runnable() {
                public void run() {

                    config.getLookAndFeel().initLookAndFeel();
                    if ( applicationConstructor != null ) {

                        showSplashWindow( config.getName() );
                        PhetApplication app = applicationConstructor.getApplication( config );
                        app.startApplication();
                        disposeSplashWindow();

                        if ( config.isStatisticsEnabled() ) {
                            SoftwareAgreementManager.validate( app.getPhetFrame(), config );
                        }

                        long applicationLaunchFinishedAt = System.currentTimeMillis();
                        config.setApplicationLaunchFinishedAt( applicationLaunchFinishedAt );

                        StatisticsManager.initInstance( config );
                        if ( StatisticsManager.isStatisticsEnabled() ) {
                            StatisticsManager.postMessage( new SessionMessage( config ) );
                        }
                        UpdatesManager.initInstance( config ).applicationStarted( app.getPhetFrame(), app.getStatistics() );//todo: due to threading, sometimes this event arrives at server first
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
