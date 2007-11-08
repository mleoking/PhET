/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import java.util.Properties;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersionInfo;
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
    private static final String PROPERTY_VERSION_MAJOR = "version.major";
    private static final String PROPERTY_VERSION_MINOR = "version.minor";
    private static final String PROPERTY_VERSION_DEV = "version.dev";
    private static final String PROPERTY_VERSION_REVISION = "version.revision";
    private static final String PROPERTY_CREDITS = "about.credits";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    // Instance data
    private String[] commandLineArgs;
    private FrameSetup frameSetup;
    private PhetResources resourceLoader;
    private final String flavor;
    private volatile PhetVersionInfo version;

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
    public PhetVersionInfo getVersion() {
        if ( version == null ) {
            String major = getProjectProperty( PROPERTY_VERSION_MAJOR ),
                    minor = getProjectProperty( PROPERTY_VERSION_MINOR ),
                    dev = getProjectProperty( PROPERTY_VERSION_DEV ),
                    rev = getProjectProperty( PROPERTY_VERSION_REVISION );
            version = new PhetVersionInfo( major, minor, dev, rev );
        }
        return version;
    }

    /**
     * Returns the locale credits for a simulation; this is an optional string specified in the simulation properties file
     * that is to be displayed only when using a particular locale.
     * @return the locale credits text
     */
    public String getLocaleCredits(){
        final String localeCreditsKey = PROPERTY_CREDITS + "." + PhetResources.readLocale();
//        System.out.println( "localeCreditsKey = " + localeCreditsKey );
        String localizedCredits = getProjectProperty( localeCreditsKey );
//        System.out.println( "localizedCredits = " + localizedCredits );

        return ( localizedCredits != null ? localizedCredits : ""  );
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
        return getProjectProperty( PROPERTY_CREDITS );
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
    public static PhetVersionInfo getVersion( String simName ) {
        return new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), PhetResources.forProject( simName ) ).getVersion();
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
        return new PhetApplicationConfig( new String[0], new FrameSetup.NoOp(), PhetResources.forProject( simName ) ).getCredits();
    }
}
