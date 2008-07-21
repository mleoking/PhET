/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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
 * @author John De Goes / Chris Malley
 */
public class PhetResources {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    private static final String AUDIO_DIR = "audio";
    private static final String IMAGES_DIR = "images";
    private static final String LOCALIZATION_DIR = "localization";
    private static final String LOCALIZATION_FILE_SUFFIX = "-strings";

    // Property used to set the locale from JNLP files.
    // For an untrusted application, system properties set in the JNLP file will 
    // only be set by Java Web Start if property name begins with "jnlp." or "javaws.".
    private static final String PROPERTY_JAVAWS_PHET_LOCALE = "javaws.phet.locale";

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
    * The javaws.phet.locale System property takes precedence, and can be set via the <property> tag in
    * JNLP files.
    *
    * @return Locale
    */
    public static Locale readLocale() {
        Locale locale = Locale.getDefault();
        //if no language is specified, default locale used by phet common should be english, not system default
//        Locale locale = new Locale( "en" );
        String javawsLocale = System.getProperty( PROPERTY_JAVAWS_PHET_LOCALE );
        if ( javawsLocale != null ) {
            locale = new Locale( javawsLocale );
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

}
