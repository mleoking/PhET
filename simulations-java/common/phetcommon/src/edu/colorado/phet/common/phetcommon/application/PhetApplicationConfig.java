/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.util.Arrays;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.tracking.Trackable;
import edu.colorado.phet.common.phetcommon.tracking.Tracker;
import edu.colorado.phet.common.phetcommon.tracking.TrackingInfo;
import edu.colorado.phet.common.phetcommon.updates.ConsoleViewForUpdates;
import edu.colorado.phet.common.phetcommon.updates.UpdateManager;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * PhetApplicationConfig encapsulates the information required to configure
 * a PhetApplication, including transparent access to the project's
 * properties file.
 * <p/>
 * Some terminology:
 * <ul>
 * <li>A project is a directory name in the PhET source code repository.
 * <li>More than one simulation may live under a project directory, be built
 * from the project's source code, and use the project's resources.
 * Each of these simulations is referred to as a flavor.
 * <li>If a flavor name is not specified, it defaults to the project name.
 * <li>A project has project properties that contains non-localized properties.
 * <li>A project has localization properties that contains localized properties.
 * <li>Properties may be flavored or unflavored. Flavored properties allow
 * identical properties to coexist for multiple flavors.
 * </ul>
 * <p/>
 * Some standard property names are described below.
 * <p/>
 * Property names for standard localized strings:
 * <ul>
 * <li>[flavor].name : simulation name (required)
 * <li>[flavor].description : simulation description (required)
 * </ul>
 * <p/>
 * Property names for standard non-localized strings:
 * <ul>
 * <li>version.major : major version number (required)
 * <li>version.minor : minor version number (required)
 * <li>version.dev : development version number (required)
 * <li>version.revision : repository revision number (required)
 * <li>about.credits : development team credits (optional)
 * </ul>
 *
 * @author John De Goes / Chris Malley
 */
public class PhetApplicationConfig {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Standard localized properties:
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "description";

    // Standard non-localized properties:
    public static final String PROPERTY_VERSION_MAJOR = "version.major";
    public static final String PROPERTY_VERSION_MINOR = "version.minor";
    public static final String PROPERTY_VERSION_DEV = "version.dev";
    public static final String PROPERTY_VERSION_REVISION = "version.revision";
    public static final String PROPERTY_CREDITS = "about.credits";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Instance data
    private String[] commandLineArgs;
    private FrameSetup frameSetup;
    private PhetResources resourceLoader;
    private final String flavor;
    private volatile PhetVersion version;
    private Tracker tracker;
    private UpdateManager updateManager;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor where the flavor defaults to the project name associated with the resource loader.
     *
     * @param commandLineArgs
     * @param resourceLoader
     */
    public PhetApplicationConfig( String[] commandLineArgs, FrameSetup frameSetup, PhetResources resourceLoader ) {
        this( commandLineArgs, frameSetup, resourceLoader, resourceLoader.getProjectName() );
    }

    /**
     * Constructor where a flavor is specified.
     *
     * @param commandLineArgs
     * @param resourceLoader
     * @param flavor
     */
    public PhetApplicationConfig( String[] commandLineArgs, FrameSetup frameSetup, PhetResources resourceLoader, String flavor ) {
        if ( frameSetup == null ) {
            throw new NullPointerException( "frameSetup is null" );
        }
        if ( resourceLoader == null ) {
            throw new NullPointerException( "resourceLoader is null" );
        }
        if ( flavor == null ) {
            throw new NullPointerException( "flavor is null" );
        }
        this.commandLineArgs = commandLineArgs;
        this.frameSetup = frameSetup;
        this.resourceLoader = resourceLoader;
        this.flavor = flavor;
        if ( isTrackingEnabled() ) {
            tracker = new Tracker( new Trackable() {
                public TrackingInfo getTrackingInformation() {
                    return new TrackingInfo( PhetApplicationConfig.this );
                }
            } );
        }
        if ( isUpdatesEnabled() ) {
            updateManager = new UpdateManager( getProjectName(), getVersion() );

            //for debugging only
            updateManager.addListener( new ConsoleViewForUpdates() );
        }
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the command line args.
     *
     * @return String[], possibly null or empty
     */
    public String[] getCommandLineArgs() {
        return commandLineArgs;
    }

    /**
     * Gets the FrameSetup, used to size and position the application's main frame.
     *
     * @return FrameSetup
     */
    public FrameSetup getFrameSetup() {
        return frameSetup;
    }

    /**
     * Gets the flavor for this configuration.
     * If a flavor was not specified in the constructor, the flavor default to the project name.
     *
     * @return flavor, always non-null.
     */
    public String getFlavor() {
        return flavor;
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
        return getFlavoredLocalizedProperty( PROPERTY_NAME );
    }

    /**
     * Gets the localized simulation description.
     *
     * @return description
     */
    public String getDescription() {
        return getFlavoredLocalizedProperty( PROPERTY_DESCRIPTION );
    }

    /**
     * Retrieves the object that encapsulates the project's version information.
     *
     * @return PhetProjectVersion
     */
    public PhetVersion getVersion() {
        if ( version == null ) {
            String major = getProjectProperty( PROPERTY_VERSION_MAJOR ),
                    minor = getProjectProperty( PROPERTY_VERSION_MINOR ),
                    dev = getProjectProperty( PROPERTY_VERSION_DEV ),
                    rev = getProjectProperty( PROPERTY_VERSION_REVISION );
            version = new PhetVersion( major, minor, dev, rev );
        }
        return version;
    }

    /**
     * Returns the locale credits for a simulation; this is an optional string specified in the simulation properties file
     * that is to be displayed only when using a particular locale.
     *
     * @return the locale credits text
     */
    public String getLocaleCredits() {
        final String localeCreditsKey = PROPERTY_CREDITS + "." + PhetResources.readLocale();
//        System.out.println( "localeCreditsKey = " + localeCreditsKey );
        String localizedCredits = getProjectProperty( localeCreditsKey );
//        System.out.println( "localizedCredits = " + localizedCredits );

        return ( localizedCredits != null ? localizedCredits : "" );
    }

    /**
     * Gets the simulation credits.
     * Credits are not localized because of the translation effort involved.
     * Credits are not flavored because the same team typically works on all
     * flavors of a simulation.
     *
     * @return credits, possibly null
     */
    public String getCredits() {
        return getLocalizedProperty( PROPERTY_CREDITS );
    }

    //----------------------------------------------------------------------------
    // Flavored and unflavored properties
    //----------------------------------------------------------------------------

    /**
     * Gets an unflavored property from the project properties.
     *
     * @param propertyName
     * @return String, null if the property doesn't exist
     */
    public String getProjectProperty( String propertyName ) {
        return resourceLoader.getProjectProperties().getProperty( propertyName );
    }

    /**
     * Gets an unflavored property from the localized properties.
     *
     * @param propertyName
     * @return String, null if the property doesn't exist
     */
    public String getLocalizedProperty( String propertyName ) {
        return resourceLoader.getLocalizedProperties().getProperty( propertyName );
    }

    /**
     * Gets a flavored property from the project properties.
     * The specified propertyName will be internally converted to
     * the proper key required to access the flavored propery.
     *
     * @param propertyName
     * @return String, null if the property doesn't exist
     */
    public String getFlavoredProjectProperty( String propertyName ) {
        return getFlavoredProperty( resourceLoader.getProjectProperties(), propertyName, flavor );
    }

    /**
     * Gets a flavored property from the localized properties.
     * The specified propertyName will be internally converted to
     * the proper key required to access the flavored propery.
     *
     * @param propertyName
     * @return String, null if the property doesn't exist
     */
    public String getFlavoredLocalizedProperty( String propertyName ) {
        return getFlavoredProperty( resourceLoader.getLocalizedProperties(), propertyName, flavor );
    }

    /*
    * Gets a flavored property.
    * This method encapsulates the syntax of a flavored property.
    * Subclasses can use this to get flavored properties out of a simulation-specific Properties.
    *
    * @param properties
    * @param propertyName
    * @param flavor
    */
    protected static String getFlavoredProperty( Properties properties, String propertyName, String flavor ) {
        String key = flavor + "." + propertyName;
        return properties.getProperty( key );
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
        return new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), new PhetResources( simName ) ).getVersion();
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
        return new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), new PhetResources( simName ) ).getCredits();
    }


    /*
    * The following class fields & methods are to solve the following problems:
    * 1. Consolidate (instead of duplicate) launch code
    * 2. Make sure that all PhetSimulations launch in the Swing Event Thread
    *        Note: The application main class should not invoke any unsafe Swing operations outside of the Swing thread.
    * 3. Make sure all PhetSimulations instantiate and use a PhetLookAndFeel, which is necessary to enable font support for many laungages.
    *
    *  This implementation uses ApplicationConstructor instead of reflection to ensure compile-time checking (at the expense of slightly more complicated subclass implementations).
    */
    private ApplicationConstructor applicationConstructor;//used to create the PhetApplication
    private PhetLookAndFeel phetLookAndFeel = new PhetLookAndFeel();//the specified look and feel to be inited in launchSim

    public String getProjectName() {
        return resourceLoader.getProjectName();
    }

    public Tracker getTracker() {
        return tracker;
    }

    public static interface ApplicationConstructor {
        PhetApplication getApplication( PhetApplicationConfig config );
    }

    public void setApplicationConstructor( ApplicationConstructor applicationConstructor ) {
        this.applicationConstructor = applicationConstructor;
    }

    public void setLookAndFeel( PhetLookAndFeel phetLookAndFeel ) {
        this.phetLookAndFeel = phetLookAndFeel;
    }

    public ApplicationConstructor getApplicationConstructor() {
        return applicationConstructor;
    }

    public PhetLookAndFeel getPhetLookAndFeel() {
        return phetLookAndFeel;
    }

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
                PhetLookAndFeel lookAndFeel = getPhetLookAndFeel();
                if ( lookAndFeel != null ) {
                    lookAndFeel.initLookAndFeel();
                }
                else {
                    new RuntimeException( "No Phetlookandfeel specified" ).printStackTrace();
                }
                ApplicationConstructor applicationConstructor = getApplicationConstructor();
                if ( applicationConstructor != null ) {
                    PhetApplication app = applicationConstructor.getApplication( PhetApplicationConfig.this );
                    app.startApplication();
                }
                else {
                    new RuntimeException( "No applicationconstructor specified" ).printStackTrace();
                }
                if ( isTrackingEnabled() ) {
                    tracker.startTracking();
                }
                if ( isUpdatesEnabled() ) {
                    updateManager.checkForUpdates();
                }
            }
        } );
    }

    public boolean isDev() {
        return Arrays.asList( commandLineArgs ).contains( PhetApplication.DEVELOPER_CONTROLS_COMMAND_LINE_ARG );
    }

    private boolean isTrackingEnabled() {
        return Arrays.asList( commandLineArgs ).contains( "-tracking" ) && !PhetServiceManager.isJavaWebStart();
    }

    private boolean isUpdatesEnabled() {
        return Arrays.asList( commandLineArgs ).contains( "-updates" ) && !PhetServiceManager.isJavaWebStart();
    }

}
