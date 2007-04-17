/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.resources.PhetVersionInfo;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * PhetApplicationConfig encapsulates the information required to configure
 * a PhetApplication, including transparent access to the project's 
 * properties file.
 * <p>
 * Some terminology:
 * <ul>
 * <li>A project is a directory name in the PhET source code repository.
 * <li>More than one simulation may live under a project directory, be built
 * from the project's source code, and use the project's resources.
 * Each of these simulations is referred to as a flavor.
 * <li>A project has a properties file (called the "project properties" file)
 * that contains non-localized properties.
 * <il>A PhetApplicationConfig can be either flavored or non-flavored,
 * as specified in the constructor.
 * <li>Project and flavor determine the keys used to access Properties.
 * For non-flavored configurations, the project name determines property names.
 * For flavored configurations, the flavor name determines property names.
 * This allows the properties for several flavors of a project to exist
 * in the same properties file.
 * </ul>
 * <p>
 * Some standard property names are described below.
 * <p>
 * Property names for standard localized strings; 
 * if using flavors, replace [projectName] with the flavor name:
 * <ul>
 * <li>[projectName].name : simulation name (required)
 * <li>[projectName].description : simulation description (required)
 * </ul>
 * <p>
 * Property names for standard non-localized strings;
 * if using flavors, prefix these with the flavor name:
 * <ul>
 * <li>version.major : major version number (required)
 * <li>version.minor : minor version number (required)
 * <li>version.dev : development version number (required)
 * <li>version.revision : repository revision number (required)
 * <li>about.credits : development team credits (optional)
 * </ul>
 */
public class PhetApplicationConfig {
    
    // Standard localized properties:
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "description";

    // Standard non-localized properties:
    private static final String PROPERTY_VERSION_MAJOR    = "version.major";
    private static final String PROPERTY_VERSION_MINOR    = "version.minor";
    private static final String PROPERTY_VERSION_DEV      = "version.dev";
    private static final String PROPERTY_VERSION_REVISION = "version.revision";
    private static final String PROPERTY_CREDITS          = "about.credits";
    
    // Instance data
    private String[] commandLineArgs;
    private PhetResources resourceLoader;
    private FrameSetup frameSetup;
    private final String flavor;
    private volatile PhetVersionInfo version;

    /**
     * 
     * @param commandLineArgs
     * @param resourceLoader
     */
    public PhetApplicationConfig( String[] commandLineArgs, FrameSetup frameSetup, PhetResources resourceLoader ) {
        this( commandLineArgs, frameSetup, resourceLoader, null );
    }

    /**
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
        this.commandLineArgs = commandLineArgs;
        this.frameSetup = frameSetup;
        this.resourceLoader = resourceLoader;
        this.flavor = flavor;
    }

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
     * 
     * @return flavor, null if this is not a flavored configuration
     */
    public String getFlavor() {
        return flavor;
    }
    
    /**
     * Is this configuration flavored?
     * 
     * @return true or false
     */
    public boolean isFlavored() {
        return flavor != null;
    }
    
    /**
     * Gets the localized simulation name.
     *
     * @return name
     */
    public String getName() {
        return getStandardLocalizedProperty( PROPERTY_NAME );
    }

    /**
     * Gets the localized simulation description.
     *
     * @return description
     */
    public String getDescription() {
        return getStandardLocalizedProperty( PROPERTY_DESCRIPTION );
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
        return resourceLoader.getProjectProperties().getProperty( PROPERTY_CREDITS );
    }

    /**
     * Retrieves the object that encapsulates the project's version information.
     *
     * @return PhetProjectVersion
     */
    public PhetVersionInfo getVersion() {
        if ( version == null ) {
            String major = getStandardProjectProperty( PROPERTY_VERSION_MAJOR ),
                   minor = getStandardProjectProperty( PROPERTY_VERSION_MINOR ),
                   dev   = getStandardProjectProperty( PROPERTY_VERSION_DEV ),
                   rev   = getStandardProjectProperty( PROPERTY_VERSION_REVISION );
            version = new PhetVersionInfo( major, minor, dev, rev );
        }
        return version;
    }

    /*
     * Gets a property (possibly flavored) from the project properties.
     * Standard project properties have a prefix only if they are flavored.
     */
    private String getStandardProjectProperty( String propertyName ) {
        String key = propertyName;
        if ( flavor != null ) {
            key = flavor + "." + propertyName;
        }
        String value = resourceLoader.getProjectProperties().getProperty( key );
        return value;
   }
    
    /*
     * Gets a standard property (possibly flavored) from the localized properties.
     * Standard localized properties all begin with either the project name or 
     * the flavor name.
     */
    private String getStandardLocalizedProperty( String propertyName ) {
        String key = null;
        if ( flavor != null ) {
            key = flavor + "." + propertyName;
        }
        else {
            key = resourceLoader.getProjectName() + "." + propertyName;
        }
        String value = resourceLoader.getLocalizedProperties().getProperty( key );
        return value;
    }
}
