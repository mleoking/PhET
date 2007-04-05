/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

/**
 * Represents the configuration and resource information for a PhET project
 * configuration.
 * <p/>
 * The class imposes the following requirements on the configuration info:
 * <p/>
 *      /[dirname]/                                  <- Root of all resources (images, etc.)
 *      /[dirname]/[dirname].properties              <- All project properties
 *      /[dirname]/localization/[dirname]-strings.properties  <- All localized strings
 * <p/>
 * Required localized strings (property names):
 * <p/>
 *      [dirname].name                   <- Name for non-flavored configs
 *      [dirname].description            <- Desc for non-flavored configs
 *      [dirname]-[flavor].name          <- Name for flavored configs
 *      [dirname]-[flavor].description   <- Desc for flavored configs
 * <p/>
 * Required project properties:
 * <p/>
 *      version.major
 *      version.minor
 *      version.dev
 *      version.revision
 * <p/>
 * Example:
 * <p/>
 * <code>
 *      PhetProjectConfig config = PhetProjectConfig.forName( "balloons" );
 * </code>
 */
public class PhetProjectConfig implements PhetResourceLoader, PhetStringSource {
    // Standard localized properties:
    private static final String NAME_PROPERTY_FORMAT = "{0}.name";
    private static final String DESC_PROPERTY_FORMAT = "{0}.description";

    // Standard non-localized properties:
    private static final String VERSION_MAJOR    = "version.major";
    private static final String VERSION_MINOR    = "version.minor";
    private static final String VERSION_DEV      = "version.dev";
    private static final String VERSION_REVISION = "version.revision";

    // Other
    private static final String LOCALIZATION_DIR = "localization/";

    // Class data:
    private final boolean isFlavored;
    private final String dirname;
    private final String flavor;
    private final Locale locale;
    private final PhetStringSource strings;
    private final PhetResourceLoader loader;
    private final String dataRoot;

    private volatile PhetProjectVersion version;
    private volatile Properties properties;

    /**
     * Constructs a new PhetProjectConfig for the specified dirname. The
     * configuration is not flavored. The default locale is assumed.
     *
     * @param dirname The dirname.
     *
     * @return The new PhetProjectConfig corresponding to the dirname.
     */
    public static PhetProjectConfig forProject( String dirname ) {
        return forProject( dirname, dirname, Locale.getDefault() );
    }

    /**
     * Constructs a new PhetProjectConfig for the specified dirname and flavor.
     * The default locale is assumed.
     *
     * @param dirname The dirname.
     * @param flavor  The flavor.
     *
     * @return The new PhetProjectConfig corresponding to the dirname and
     *         flavor.
     */
    public static PhetProjectConfig forProject( String dirname, String flavor ) {
        return forProject( dirname, flavor, Locale.getDefault() );
    }

    /**
     * Constructs a new PhetProjectConfig for the specified dirname, flavor,
     * and locale.
     *
     * @param dirname The dirname.
     * @param flavor  The flavor.
     * @param locale  The locale.
     *
     * @return The new PhetProjectConfig corresponding to the dirname, flavor,
     *         and locale.
     */
    public static PhetProjectConfig forProject( String dirname, String flavor, Locale locale ) {
        String bundleName = dirname + "/" + LOCALIZATION_DIR + "/" + dirname + "-strings";

        SimStrings strings = new SimStrings();

        strings.setLocale( locale );
        strings.addStrings( LOCALIZATION_DIR + "CommonStrings" );
        strings.init( bundleName );

        return new PhetProjectConfig( dirname, flavor, locale, strings, new DefaultResourceLoader() );
    }

    /*
     * This constructor is not private only for testing purposes. It should
     * not be used. Clients should use the static forProject() methods to
     * construct new PhetProjectConfig objects.
     */
    protected PhetProjectConfig( String dirname, String flavor, Locale locale, PhetStringSource strings, PhetResourceLoader loader ) {
        this.isFlavored = isConfigFlavored( flavor, dirname );
        this.dirname    = dirname;
        this.flavor     = flavor == null ? dirname : flavor;
        this.locale     = locale;
        this.strings    = strings;
        this.loader     = loader;
        this.dataRoot   = "/" + dirname + "/";
    }

    /**
     * Returns the root where all data is stored for the project config.
     *
     * @return The root where data is stored.
     */
    public String getDataRoot() {
        return dataRoot;
    }

    /**
     * Determines if the configuration is flavored. A configuration is
     * flavoried if the client specified a non-null flavor that differs from
     * the name of the project.
     *
     * @return <code>true</code> if the configuration is flavored,
     *         <code>false</code> otherwise.
     */
    public boolean isFlavored() {
        return isFlavored;
    }

    /**
     * Retrieves the dirname of the project.
     *
     * @return The dirname.
     */
    public String getDirname() {
        return dirname;
    }

    /**
     * Retrieves the flavor of the configuration.
     *
     * @return The flavor of the configuration, or the dirname, if the
     *         configuration is not flavored.
     */
    public String getFlavor() {
        return flavor;
    }

    /**
     * Retrieves the localized name of the project.
     *
     * @return The localized name of the project.
     */
    public String getName() {
        return getNameFlavorDependentString( NAME_PROPERTY_FORMAT );
    }

    /**
     * Retrieves the localized description of the project.
     *
     * @return The localized description of the project.
     */
    public String getDescription() {
        return getNameFlavorDependentString( DESC_PROPERTY_FORMAT );
    }

    /**
     * Retrieves the version of the project. Presently, this is project- but
     * not flavor-specific.
     *
     * @return The version of the project.
     */
    public PhetProjectVersion getVersion() {
        if( version == null ) {
            String major = getProperties().getProperty( VERSION_MAJOR ),
                   minor = getProperties().getProperty( VERSION_MINOR ),
                   dev   = getProperties().getProperty( VERSION_DEV ),
                   rev   = getProperties().getProperty( VERSION_REVISION );

            version = new PhetProjectVersion( major, minor, dev, rev );
        }

        return version;
    }

    /**
     * Retrieves the non-localized, project-specific properties. The method
     * assumes the property file has the following location:
     * <p/>
     * /[name]/[name].properties
     * <p/>
     * This property file should only be used to store configuration information
     * and other data that do not require localization.
     *
     * @return The project-specific properties.
     */
    public Properties getProperties() {
        if( properties == null ) {
            properties = new Properties();

            String propertiesResource = dirname + ".properties";

            try {
                properties.load( getResourceAsStream( propertiesResource ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        return properties;
    }

    /**
     * Retrieves the localized string associated with the specified property
     * name.
     *
     * @param propertyName The property name.
     *
     * @return The localized string.
     */
    public String getString( String propertyName ) {
        return strings.getString( propertyName );
    }

    /**
     * Retrieves the image having the specified resource location.
     *
     * @param resource The location of the resource.
     *
     * @return The image having the specified resource location.
     */
    public BufferedImage getImage( String resource ) {
        return loader.getImage( dataRoot + resource );
    }

    /**
     * Retrieves a byte array of the resource file having the specified
     * location.
     *
     * @param resource The location of the resource.
     *
     * @return The byte array of the resource file.
     */
    public byte[] getResource( String resource ) {
        return loader.getResource( dataRoot + resource );
    }

    /**
     * Retrieves an input stream to the resource having the specified location.
     *
     * @param resource The location of the resource.
     *
     * @return The input stream to the resource having the specified location.
     */
    public InputStream getResourceAsStream( String resource ) {
        return loader.getResourceAsStream( dataRoot + resource );
    }

    /**
     * Retrieves a localized string that is tailored to the project name and
     * flavor. The client must specify a message format string, which is used
     * to create the property name based on the name of the project.
     * <p/>
     * Example: If [name] is the dirname of the project, and nameFormat equals
     *          "{0}.name", then the property will be resolved as "[name].name".
     *          If the configuration were flavored, the property would be
     *          resolved as "[name]-[flavor].name".
     *
     * @param nameFormat The format used in creating the property name.
     *
     * @return The localized string.
     */
    public String getNameFlavorDependentString( String nameFormat ) {
        String property;

        if( isFlavored ) {
            String nameFlavorFormat = nameFormat.replaceAll( "\\{0\\}", "{0}-{1}" );

            property = MessageFormat.format( nameFlavorFormat, new Object[]{dirname, flavor} );
        }
        else {
            property = MessageFormat.format( nameFormat, new Object[]{dirname} );
        }

        return getString( property );
    }

    // Object methods
    public boolean equals( Object o ) {
        if( this == o ) {
            return true;
        }
        if( o == null || getClass() != o.getClass() ) {
            return false;
        }

        PhetProjectConfig that = (PhetProjectConfig)o;

        return dirname.equals( that.dirname ) && flavor.equals( that.flavor ) && locale.equals( that.locale );

    }

    public int hashCode() {
        int result;
        result = dirname.hashCode();
        result = 31 * result + flavor.hashCode();
        result = 31 * result + locale.hashCode();
        return result;
    }

    public String toString() {
        return "PhetProjectConfig[isFlavored=" + isFlavored +
               ", dirname=" + dirname +
               ", flavor=" + flavor +
               ", locale=" + locale +
               ", strings=" + strings +
               ", loader=" + loader +
               ", version=" + getVersion() +
               ", properties=" + getProperties() + "]";
    }

    // Private methods
    private static boolean isConfigFlavored( String flavor, String dirname ) {
        return flavor != null && !flavor.equals( dirname );
    }
}
