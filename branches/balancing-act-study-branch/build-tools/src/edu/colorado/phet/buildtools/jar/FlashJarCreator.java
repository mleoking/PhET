/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import edu.colorado.phet.flashlauncher.FlashLauncher;

/**
 * Creates jar files for Flash simulations.
 */
public class FlashJarCreator extends JarCreator {

    /*
    * Reads files from the input JAR that are required by Flash sims.
    */
    protected void readJarEntries( String inputJarName, Locale locale ) {
        // nothing to read
    }

    /*
    * Writes localized strings to the output JAR in XML format.
    */
    protected void writeStringsFile( JarOutputStream jarOutputStream, Locale locale, Properties localizedStrings, String stringsFilePath, String header ) throws IOException {
        JarEntry jarEntry = new JarEntry( stringsFilePath );
        jarOutputStream.putNextEntry( jarEntry );
        try {
            FlashStringsAdapter.writeProperties( localizedStrings, header, jarOutputStream );
        }
        catch ( TransformerException e ) {
            throw new IOException( "error configuring XML parser" );
        }
        catch ( ParserConfigurationException e ) {
            throw new IOException( "error generating XML" );
        }
        jarOutputStream.closeEntry();
    }

    /*
    * Write files to the output JAR that are required for Flash sims.
    * Any files written here must also be excluded via getCopyExclusions.
    */
    protected void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException {
        // flash-launcher-args.txt
        writeFlashLauncherArgs( jarOutputStream, projectName, locale );
    }

    /*
    * Regular expressions for files that should be skipped when copying the jar for a Flash sim.
    * These exclusions are specific to Flash sims, general exclusions are provided by the super class.
    */
    protected String[] getCopyExclusions() {
        return new String[] { FlashLauncher.ARGS_FILENAME };
    }

    /*
     * Writes flash-launcher-args.txt, used by older FlashLauncher.
     * All sims now use simulation.properties instead of flash-launcher-args.txt, but this 
     * we still need to generate flash-launcher-args.txt to support translation deployment for older deployed jars.
     * Delete this method when all sims have been redeployed with simulation.properties. See #2463.
     * @deprecated
     */
    private static void writeFlashLauncherArgs( JarOutputStream outputStream, String projectName, Locale locale ) throws IOException {
        // create the args string
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String args = projectName + " " + language;
        if ( country == null || country.length() == 0 ) {
            args += " " + "null";
        }
        else {
            args += " " + country;
        }
        // write the args string
        JarEntry jarEntry = new JarEntry( FlashLauncher.ARGS_FILENAME );
        outputStream.putNextEntry( jarEntry );
        outputStream.write( args.getBytes() );
        outputStream.closeEntry();
    }

    public String getStringsFileSuffix() {
        return ".xml";
    }

    /**
     * Gets the path to the JAR resource that contains localized strings.
     */
    public String getStringsFilePath( String projectName, Locale locale ) {
        // XML resources are at the top-level of the JAR, so resource path is the same as resource name
        return getStringsFileBasename( projectName, locale );
    }

    /**
     * Gets the basename of of the JAR resource for an XML document.
     * In UNIX parlance, the basename of a file is the final rightmost component of its full path.
     */
    public String getStringsFileBasename( String projectName, Locale locale ) {
        String rootName = getStringsRootName( projectName );
        String format = "{0}-strings_{1}" + getStringsFileSuffix();
        Object[] args = { rootName, locale };
        return MessageFormat.format( format, args );
    }

    /*
    * Gets the root name of the strings file.
    * <p>
    * This is typically the same as the project name, except for common strings.
    * PhET common strings are bundled into their own JAR file for use with translation utility.
    * The JAR file must be built & deployed via a dummy sim named "flash-common-strings",
    * found in trunk/simulations-flash/simulations.  If the project name is "flash-common-strings",
    * we really want to load the common strings which are in files with root name "common".
    * So we use "common" as the project name.
    */
    private String getStringsRootName( String projectName ) {
        String rootName = projectName;
        if ( rootName.equals( "flash-common-strings" ) ) {
            rootName = "common";
        }
        return rootName;
    }
}