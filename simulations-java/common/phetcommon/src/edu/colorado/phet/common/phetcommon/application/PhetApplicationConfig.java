/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.Trackable;
import edu.colorado.phet.common.phetcommon.tracking.TrackingInfo;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * PhetApplicationConfig encapsulates the information required to configure
 * a PhetApplication, including transparent access to the project's
 * properties file. It is also responsible for launching an application.
 * <p/>
 * Some terminology:
 * <ul>
 * <li>A project is a directory name in the PhET source code repository.
 * <li>More than one simulation may live under a project directory, be built
 * from the project's source code, and use the project's resources.
 * Each of these simulations is referred to as a flavor.
 * <li>If a flavor name is not specified, it defaults to the project name.
 * </ul>
 *
 * @author John De Goes / Chris Malley
 */
public class PhetApplicationConfig implements Trackable, ITrackingInfo, ISimInfo {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    public static final FrameSetup DEFAULT_FRAME_SETUP = new FrameSetup.CenteredWithSize( 1024, 768 );

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // immutable
    private final String[] commandLineArgs;
    private final String flavor;
    private PhetResources resourceLoader;

    // mutable
    private ApplicationConstructor applicationConstructor;
    private FrameSetup frameSetup;
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel(); // the look and feel to be initialized in launchSim

    //----------------------------------------------------------------------------
    // Interfaces
    //----------------------------------------------------------------------------

    /**
     * We need one of these to start the simulation.
     */
    public static interface ApplicationConstructor {
        PhetApplication getApplication( PhetApplicationConfig config );
    }

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor with smallest number of args for the case where project & flavor names are identical.
     */
    public PhetApplicationConfig( String[] commandLineArgs, ApplicationConstructor applicationConstructor, String project ) {
        this( commandLineArgs, applicationConstructor, project, project );
    }

    /**
     * Constructor with smallest number of args for a flavor.
     */
    public PhetApplicationConfig( String[] commandLineArgs, ApplicationConstructor applicationConstructor, String project, String flavor ) {
        this.commandLineArgs = commandLineArgs;
        this.flavor = flavor;
        this.resourceLoader = new PhetResources( project );
        this.applicationConstructor = applicationConstructor;
        this.frameSetup = DEFAULT_FRAME_SETUP;
        this.phetLookAndFeel = new PhetLookAndFeel();
    }

    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------

    public String[] getCommandLineArgs() {
        return commandLineArgs;
    }

    public void setFrameSetup( FrameSetup frameSetup ) {
        this.frameSetup = frameSetup;
    }

    public FrameSetup getFrameSetup() {
        return frameSetup;
    }

    public String getFlavor() {
        return flavor;
    }

    public void setLookAndFeel( PhetLookAndFeel phetLookAndFeel ) {
        this.phetLookAndFeel = phetLookAndFeel;
    }

    public PhetLookAndFeel getLookAndFeel() {
        return phetLookAndFeel;
    }

    public PhetResources getResourceLoader() {
        return resourceLoader;
    }

    public String getProjectName() {
        return resourceLoader.getProjectName();
    }

    //----------------------------------------------------------------------------
    // Standard properties
    //----------------------------------------------------------------------------

    /**
     * Gets the localized simulation name.
     *
     * @return name
     */
    public String getTitle() {
        return resourceLoader.getName( flavor );
    }

    /**
     * Gets the localized simulation description.
     *
     * @return description
     */
    public String getDescription() {
        return resourceLoader.getDescription( flavor );
    }

    public String getVersionForTitleBar() {
        return getVersion().formatForTitleBar();
    }

    /**
     * Gets the simulation credits.
     *
     * @return credits, possibly null
     */
    public String getCredits() {
        return resourceLoader.getCredits();
    }

    /**
     * Retrieves the object that encapsulates the project's version information.
     *
     * @return PhetProjectVersion
     */
    public PhetVersion getVersion() {
        return resourceLoader.getVersion();
    }

    //----------------------------------------------------------------------------
    // Launcher
    //----------------------------------------------------------------------------

    /*
     * This method solves the following problems:
     * 1. Consolidate (instead of duplicate) launch code
     * 2. Make sure that all PhetSimulations launch in the Swing Event Thread
     *    Note: The application main class should not invoke any unsafe Swing operations outside of the Swing thread.
     * 3. Make sure all PhetSimulations instantiate and use a PhetLookAndFeel, which is necessary to enable font support for many laungages.
     *
     *  This implementation uses ApplicationConstructor instead of reflection to ensure compile-time checking (at the expense of slightly more complicated subclass implementations).
     */

    public void launchSim() {
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

                    getLookAndFeel().initLookAndFeel();
                    if ( applicationConstructor != null ) {

                        showSplashWindow( getTitle() );
                        PhetApplication app = applicationConstructor.getApplication( PhetApplicationConfig.this );
                        app.startApplication();
                        disposeSplashWindow();
                        
                        new TrackingApplicationManager( PhetApplicationConfig.this ).applicationStarted( app );
                        new UpdateApplicationManager( PhetApplicationConfig.this ).applicationStarted( app );
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


    //----------------------------------------------------------------------------
    // Updates and Tracking stuff
    //----------------------------------------------------------------------------

    public TrackingInfo getTrackingInformation() {
        return new TrackingInfo( this );
    }

    public String getHumanReadableTrackingInformation() {
        return getTrackingInformation().toHumanReadable();
    }

    public boolean isDev() {
        return Arrays.asList( commandLineArgs ).contains( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public boolean isUpdatesEnabled() {
        return new UpdateApplicationManager( this ).isUpdatesEnabled();
    }

    public boolean isTrackingEnabled() {
        return new TrackingApplicationManager( this ).isTrackingEnabled();
    }
}
