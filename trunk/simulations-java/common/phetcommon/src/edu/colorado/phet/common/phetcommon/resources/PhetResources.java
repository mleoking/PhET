/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import edu.colorado.phet.common.phetcommon.util.logging.ConsoleLogger;
import edu.colorado.phet.common.phetcommon.util.logging.ILogger;
import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;

/**
 * PhetResources provides the facilities for accessing JAR resources.
 * <p/>
 * The implementation of this class assumes that the resources are in specific locations.
 * If [projectName] is the name of the simulation's directory in the repository, then
 * the resources are excepted to exist at these locations in the JAR file:
 * <p/>
 * <ul>
 * <li>/[projectName]/ : the root of all resources
 * <li>/[projectName]/[projectName].properties : the project properties file, nonlocalized properties
 * <li>/[projectName]/localization/[projectName]-strings.properties : English strings
 * <li>/[projectName]/localization/[projectName]-strings_[countryCode].properties : localized strings for a specific country code
 * <li>/[projectName]/images/ : images
 * <li>/[projectName]/audio/ : audio
 * </ul>
 * <p/>
 * The project's properties file and string localization file are loaded
 * when this object is instantiated.
 *
 * @author Chris Malley
 * @author John De Goes
 */
public class PhetResources {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final ILogger LOGGER = new ConsoleLogger(); // use NullLogger to turn off, ConsoleLogger to turn on
    
    // Standard localized properties:
    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "description";

    // Standard non-localized properties:
    public static final String PROPERTY_VERSION_MAJOR = "version.major";
    public static final String PROPERTY_VERSION_MINOR = "version.minor";
    public static final String PROPERTY_VERSION_DEV = "version.dev";
    public static final String PROPERTY_VERSION_REVISION = "version.revision";
    public static final String PROPERTY_CREDITS = "about.credits";

    private static final String AUDIO_DIR = "audio";
    private static final String IMAGES_DIR = "images";
    private static final String LOCALIZATION_DIR = "localization";
    private static final String LOCALIZATION_FILE_SUFFIX = "-strings";
    
    // Properties used to set the locale via JNLP files.
    // For an untrusted application, system properties set in the JNLP file will 
    // only be set by Java Web Start if property name begins with "jnlp." or "javaws.".
    public static final String PROPERTY_JAVAWS_USER_LANGUAGE = "javaws.user.language";
    public static final String PROPERTY_JAVAWS_USER_COUNTRY = "javaws.user.country";

    private static final char PATH_SEPARATOR = '/';

    private static final String PROPERTIES_SUFFIX = ".properties";

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final String projectName;
    private final Locale locale;
    private final PhetProperties localizedProperties;
    private final PhetProperties projectProperties;
    private final IResourceLoader resourceLoader;
    private final String rootDirectoryName;
    private volatile PhetVersion version;

    //----------------------------------------------------------------------------
    // Constructors & initializers
    //----------------------------------------------------------------------------

    /**
     * Constructor, default locale and default resource loader.
     *
     * @param projectName
     */
    public PhetResources( String projectName ) {
        this( projectName, readLocale(), new DefaultResourceLoader() );
    }

    /**
     * Constructor.
     *
     * @param projectName
     * @param locale
     * @param resourceLoader
     */
    public PhetResources( String projectName, Locale locale, IResourceLoader resourceLoader ) {

        this.projectName = projectName;
        this.rootDirectoryName = projectName;
        this.locale = locale;
        this.resourceLoader = resourceLoader;

        // Load the project's properties file, if it exists
        String projectPropertiesBundleName = projectName + PROPERTIES_SUFFIX;
        if ( resourceLoader.exists( rootDirectoryName + PATH_SEPARATOR + projectPropertiesBundleName ) ) {
            this.projectProperties = getProperties( projectPropertiesBundleName );
        }
        else {
            this.projectProperties = new PhetProperties();
        }

        // Load the localized strings
        String localizedPropertiesBundleName = LOCALIZATION_DIR + PATH_SEPARATOR + projectName + LOCALIZATION_FILE_SUFFIX;
        this.localizedProperties = getProperties( localizedPropertiesBundleName );
    }

    /*
    * Read the locale that was specified for the application.
    * The default locale is the value of the user.language System property, as read by Locale.getDefault.
    * PhET-specific properties can be used to override the default.
    *
    * @return Locale
    */
    public static Locale readLocale() {
        
        Locale locale = Locale.getDefault();
        LOGGER.log( "PhetResources.readLocale: default locale=" + locale.toString() );
        
        // deprecated, included for backward compatibility
        Locale javawsPhetLocale = readJavawsPhetLocale();
        if ( javawsPhetLocale != null ) {
            locale = javawsPhetLocale;
        }
        
        String language = System.getProperty( PROPERTY_JAVAWS_USER_LANGUAGE );
        String country = System.getProperty( PROPERTY_JAVAWS_USER_COUNTRY ); // optional, may be null
        if ( language != null ) {
            if ( country != null ) {
                LOGGER.log( "PhetResources.readLocale: overriding locale via " + PROPERTY_JAVAWS_USER_LANGUAGE + "=" + language + " " + PROPERTY_JAVAWS_USER_COUNTRY + "=" + country );
                locale = new Locale( language, country );
            }
            else {
                LOGGER.log( "PhetResources.readLocale: overriding locale via " + PROPERTY_JAVAWS_USER_LANGUAGE + "=" + language );
                locale = new Locale( language );
            }
        }
        else if ( country != null ) {
            System.err.println( "PhetResources.locale: ignoring locale properties, they are in an illegal state, country specified without language" );
        }
        
        LOGGER.log( "PhetResources.readLocale: returning locale=" + locale.toString() );
        return locale;
    }
    
    /**
     * @deprecated included for backward compatibility, should be deleted after all sims are redeployed
     */
    private static Locale readJavawsPhetLocale() {
        Locale locale = null;
        final String propertyName = "javaws.phet.locale"; // constant is included here so it goes away when this deprecated method is deleted
        String language = System.getProperty( propertyName );
        if ( language != null ) {
            LOGGER.log( "PhetResources.readLocale: overriding locale via " + propertyName + "=" + language );
            locale = new Locale( language );
        }
        return locale;
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the name of the project.
     *
     * @return String
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the name of the root directory where all resources are stored.
     *
     * @return directory name
     */
    public String getRootDirectoryName() {
        return rootDirectoryName;
    }

    /**
     * Gets the locale used to load the resources.
     *
     * @return Locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the localized properties.
     *
     * @return PhetProperties
     */
    public PhetProperties getLocalizedProperties() {
        return localizedProperties;
    }

    /**
     * Gets the project properties.
     *
     * @return PhetProperties
     */
    public PhetProperties getProjectProperties() {
        return projectProperties;
    }
    
    //----------------------------------------------------------------------------
    // Resource accessors
    //----------------------------------------------------------------------------

    /**
     * Gets the audio having the specified resource location.
     *
     * @param resourceName
     * @return PhetAudioClip
     */
    public PhetAudioClip getAudioClip( String resourceName ) {
        return resourceLoader.getAudioClip( rootDirectoryName + PATH_SEPARATOR + AUDIO_DIR + PATH_SEPARATOR + resourceName );
    }

    /**
     * Gets the image having the specified resource location.
     *
     * @param resourceName
     * @return BufferedImage
     */
    public BufferedImage getImage( String resourceName ) {
        return resourceLoader.getImage( rootDirectoryName + PATH_SEPARATOR + IMAGES_DIR + PATH_SEPARATOR + resourceName );
    }

    /**
     * Gets a byte array of the resource file having the specified location.
     *
     * @param resourceName
     * @return byte[]
     */
    public byte[] getResource( String resourceName ) throws IOException {
        return resourceLoader.getResource( rootDirectoryName + PATH_SEPARATOR + resourceName );
    }

    /**
     * Gets an input stream to the resource having the specified location.
     *
     * @param resourceName
     * @return InputStream
     */
    public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return resourceLoader.getResourceAsStream( rootDirectoryName + PATH_SEPARATOR + resourceName );
    }
    
    /**
     * Returns the contents of a resource as a String.
     *  
     * @param resourceName
     * @return String
     */
    public String getResourceAsString( String resourceName ) throws IOException {
        InputStream in = getResourceAsStream( resourceName );
        StringBuffer out = new StringBuffer();
        byte[] b = new byte[4096];
        for ( int n; ( n = in.read( b ) ) != -1; ) {
            out.append( new String( b, 0, n ) );
        }
        return out.toString();
    }

    /**
     * Gets a properties resource.
     * Use this if you need to load a simulation-specific properties file.
     *
     * @param resourceName
     * @return PhetProperties
     */
    public PhetProperties getProperties( String resourceName ) {
        return resourceLoader.getProperties( rootDirectoryName + PATH_SEPARATOR + resourceName, locale );
    }

    //----------------------------------------------------------------------------
    // Convenience methods
    //----------------------------------------------------------------------------

    public String getProjectProperty( String key ) {
        return projectProperties.getProperty( key );
    }

    public String getLocalizedString( String key ) {
        return DummyConstantStringTester.getString(localizedProperties.getString( key ));
    }

    public char getLocalizedChar( String key, char defaultValue ) {
        return localizedProperties.getChar( key, defaultValue );
    }

    public int getLocalizedInt( String key, int defaultValue ) {
        return localizedProperties.getInt( key, defaultValue );
    }

    public double getLocalizedDouble( String key, double defaultValue ) {
        return localizedProperties.getDouble( key, defaultValue );
    }
    
    //----------------------------------------------------------------------------
    // Properties that are common to all sims
    //----------------------------------------------------------------------------
    
    /**
     * Gets the localized name of the sim (required property).
     */
    public String getName( String flavor ) {
        return localizedProperties.getProperty( flavor + "." + PROPERTY_NAME );
    }
    
    /**
     * Gets the localized description of the sim (required property).
     */
    public String getDescription( String flavor ) {
        return localizedProperties.getProperty( flavor + "." + PROPERTY_DESCRIPTION );
    }
    
    /**
     * Gets the localized credits for the sim (optional property).
     */
    public String getCredits() {
        return localizedProperties.getProperty( PROPERTY_CREDITS );
    }
    
    /**
     * Gets the object that encapsulates the project's version information.
     * Involves using a number of required project properties.
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

}
