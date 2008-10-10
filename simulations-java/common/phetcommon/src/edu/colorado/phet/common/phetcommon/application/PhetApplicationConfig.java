/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.io.IOException;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.preferences.DefaultTrackingPreferences;
import edu.colorado.phet.common.phetcommon.preferences.DefaultUpdatePreferences;
import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;
import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.Trackable;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.tracking.TrackingInfo;
import edu.colorado.phet.common.phetcommon.updates.AutomaticUpdateDialog;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;
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
public class PhetApplicationConfig implements Trackable, ITrackingInfo {

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
    
    /**
     * Returns a null application, for use in test programs.
     * Use this if you never intend to call launchSim.
     * @deprecated This should go away after we finish refactoring.
     */
    public static class NullApplicationConstructor implements ApplicationConstructor {
        public PhetApplication getApplication( PhetApplicationConfig config ) {
            return null;
        }
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
    
    public void setApplicationConstructor( ApplicationConstructor applicationConstructor ) {
        this.applicationConstructor = applicationConstructor;
    }

    public ApplicationConstructor getApplicationConstructor() {
        return applicationConstructor;
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
    public String getName() {
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

    /**
     * This method is provided to facilitate older simulations reading the version property for this simulation.
     * When all simulations properly use PhetApplicationConfig, this method will be deleted.
     *
     * @param simName the simulation to get the version for.
     * @return the version info.
     * @deprecated Simulations should use PhetApplicationConfig properly.
     */
    public static PhetVersion getVersion( String simName ) {
        return new PhetResources( simName ).getVersion();
    }

    /**
     * This method is provided to facilitate older simulations reading the version property for this simulation.
     * When all simulations properly use PhetApplicationConfig, this method will be deleted.
     *
     * @param simName the simulation to get the version for.
     * @return the credits
     * @deprecated Simulations should use PhetApplicationConfig properly.
     */
    public static String getCredits( String simName ) {
        return new PhetResources( simName ).getCredits();
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
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                getLookAndFeel().initLookAndFeel();
                ApplicationConstructor applicationConstructor = getApplicationConstructor();
                if ( applicationConstructor != null ) {
                    PhetApplication app = applicationConstructor.getApplication( PhetApplicationConfig.this );
                    app.startApplication();
                    if ( isTrackingEnabled() && isTrackingAllowed() ) {
                        new Tracker( PhetApplicationConfig.this ).startTracking();
                    }
                    if ( isUpdatesEnabled() && isUpdatesAllowed() && hasEnoughTimePassedSinceAskMeLater() ) {
                        autoCheckForUpdates( app );
                    }
                }
                else {
                    new RuntimeException( "No applicationconstructor specified" ).printStackTrace();
                }
            }
        } );
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
    
    private boolean isUpdatesAllowed() {
        //todo: perhaps we should use PhetPreferences.isUpdatesEnabled(String,String)
        boolean enabledForSelection = new DefaultUpdatePreferences().isEnabled();
//        System.out.println( "updates allowed = " + enabledForSelection );
        return enabledForSelection;
    }

    private boolean isTrackingAllowed() {
        //todo: perhaps we should use PhetPreferences.isTrackingEnabled(String,String)
        boolean trackingAllowed = new DefaultTrackingPreferences().isEnabled();
//        System.out.println( "trackingAllowed = " + trackingAllowed );
        return trackingAllowed;
    }

    private boolean hasEnoughTimePassedSinceAskMeLater() {
        long lastTimeUserPressedAskMeLaterForAnySim = PhetPreferences.getInstance().getAskMeLater( getProjectName(), getFlavor() );
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTimeUserPressedAskMeLaterForAnySim;
        int millisecondsDelayBeforeAskingAgain = 1000 * 60 * 60 * 24;
//        System.out.println( "elapsedTime/1000.0 = " + elapsedTime / 1000.0+" sec" );
        return elapsedTime > millisecondsDelayBeforeAskingAgain || lastTimeUserPressedAskMeLaterForAnySim == 0;
    }

    private void autoCheckForUpdates( final PhetApplication app ) {
        final UpdateManager updateManager = new UpdateManager( getProjectName(), getVersion() );
        updateManager.addListener( new UpdateManager.Listener() {
            public void discoveredRemoteVersion( PhetVersion remoteVersion ) {
            }

            public void newVersionAvailable( PhetVersion currentVersion, final PhetVersion remoteVersion ) {
                int remoteVersionSVN = remoteVersion.getRevisionAsInt();
                int requestedSkipSVN = PhetPreferences.getInstance().getSkipUpdate( getProjectName(), getFlavor() );
//                System.out.println( "remoteVersionSVN = " + remoteVersionSVN + ", requestedSkipSVN=" + requestedSkipSVN );
                if ( remoteVersionSVN > requestedSkipSVN )
                //show UI in swing thread after new thread has found a new version
                {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            new AutomaticUpdateDialog( app, remoteVersion ).setVisible( true );
                        }
                    } );
                }
            }

            public void exceptionInUpdateCheck( IOException e ) {
            }

            public void noNewVersionAvailable( PhetVersion currentVersion, PhetVersion remoteVersion ) {
            }
        } );

        //do check in new thread
        Thread t = new Thread( new Runnable() {
            public void run() {
                updateManager.checkForUpdates();
            }
        } );
        t.start();
    }

    public boolean isDev() {
        return Arrays.asList( commandLineArgs ).contains( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    public boolean isTrackingEnabled() {
        return Arrays.asList( commandLineArgs ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

    public  boolean isUpdatesEnabled() {
        return Arrays.asList( commandLineArgs ).contains( "-updates" ) && !PhetServiceManager.isJavaWebStart();
    }

}
