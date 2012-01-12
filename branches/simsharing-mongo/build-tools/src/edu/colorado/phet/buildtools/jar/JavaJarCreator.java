/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

/**
 * Creates jar files for Java simulations, and provides utility methods that encapsulate
 * the conventions related to localized string file names.
 */
public class JavaJarCreator extends JarCreator {

    Properties jarLauncherProperties;

    /*
    * Reads files from the input JAR that are required by Java sims.
    */
    protected void readJarEntries( String inputJarName, Locale locale ) throws IOException {
        // read jar-launcher.properties
        jarLauncherProperties = JarUtils.readProperties( inputJarName, JARLauncher.PROPERTIES_FILE_NAME );
        if ( jarLauncherProperties == null ) {
            throw new IOException( "Cannot read " + JARLauncher.PROPERTIES_FILE_NAME + ". Are you using the most current simulation JAR from the PhET website?" );
        }
    }

    /*
    * Writes localized strings to the output JAR in Properties format.
    */
    protected void writeStringsFile( JarOutputStream jarOutputStream, Locale locale, Properties localizedStrings, String stringsFilePath, String header ) throws IOException {
        JarEntry jarEntry = new JarEntry( stringsFilePath );
        jarOutputStream.putNextEntry( jarEntry );
        localizedStrings.store( jarOutputStream, header );
        jarOutputStream.closeEntry();
    }

    /*
    * Write files to the output JAR that are required for Java sims.
    * Any files written here must also be excluded via getCopyExclusions.
    */
    protected void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException {
        // add jar-lanucher.properties, after changing locale
        jarLauncherProperties.setProperty( "language", locale.getLanguage() );
        String country = locale.getCountry();
        if ( country != null && country.length() > 0 ) {
            jarLauncherProperties.setProperty( "country", country );
        }
        JarEntry jarEntry = new JarEntry( JARLauncher.PROPERTIES_FILE_NAME );
        jarOutputStream.putNextEntry( jarEntry );
        jarLauncherProperties.store( jarOutputStream, header );
        jarOutputStream.closeEntry();
    }

    /*
    * Regular expressions for files that should be skipped when copying the jar for a Java sim.
    * These exclusions are specific to Java sims, general exclusions are provided by the super class.
    */
    protected String[] getCopyExclusions() {
        return new String[] { JARLauncher.PROPERTIES_FILE_NAME };
    }

    public String getStringsFileSuffix() {
        return ".properties";
    }

    /**
     * Gets the path to the JAR resource that contains localized strings for
     * a specified project and locale. If locale is null, the fallback resource
     * path is returned.
     */
    public String getStringsFilePath( String projectName, Locale locale ) {
        String dirName = getStringsRootName( projectName );
        String fileName = getStringsFileBasename( projectName, locale );
        return dirName + "/localization/" + fileName;
    }

    /**
     * Gets the basename of a JAR resource that contains localized strings.
     * In UNIX parlance, the basename of a file is the final rightmost component of its full path.
     * For example: faraday-strings_es.properties.
     * <p/>
     * If locale is null, the name of the fallback resource is returned.
     * The fallback name does not contain a locale, and contains English strings.
     * For example: faraday-strings.properties
     * <p/>
     * NOTE: Support for the fallback name is provided for backward compatibility.
     * All Java simulations should migrate to the convention of including "en" in the
     * resource name of English localization files.
     */
    public String getStringsFileBasename( String projectName, Locale locale ) {
        String rootName = getStringsRootName( projectName );
        String basename = null;
        if ( locale == null ) {
            basename = rootName + "-strings" + getStringsFileSuffix(); // fallback basename contains no language code
        }
        else {
            String localeString = LocaleUtils.localeToString( locale );
            basename = rootName + "-strings_" + localeString + getStringsFileSuffix();
        }
        return basename;
    }

    /*
    * Gets the root name of the resource that contains localized strings.
    * <p>
    * This is typically the same as the project name, except for common strings.
    * PhET common strings are bundled into their own JAR file for use with Translation Utility.
    * The JAR file must be built & deployed via a dummy sim named "java-common-strings",
    * found in trunk/simulations-flash/simulations.  If the project name is "java-common-strings",
    * we really want to load the common strings which are in files with root name "phetcommon".
    * So we use "phetcommon" as the project name.
    */
    private String getStringsRootName( String projectName ) {
        String rootName = projectName;
        if ( rootName.equals( "java-common-strings" ) ) {
            rootName = "phetcommon";
        }
        return rootName;
    }
}