/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildtools.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;

/**
 * Creates jar files for PhET sims.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class JarCreator {

    private static final Logger LOGGER = Logger.getLogger( JarCreator.class.getCanonicalName() );

    /*
    * Reads info from the input jar prior to it being opened as an input stream.
    * Subclasses should use this to read things that they need.
    */
    protected abstract void readJarEntries( String inputJarName, Locale locale ) throws IOException;

    /*
    * Writes localized strings to the output jar.
    * This gives subclasses an opportunity to change the format of the strings file to match the type of sim.
    */
    protected abstract void writeStringsFile( JarOutputStream jarOutputStream, Locale locale, Properties localizedStrings, String stringsFilePath, String header ) throws IOException;

    /*
    * Writes subclass-specific entries to the output jar.
    * Any files written here must also be excluded via getCopyExclusions.
    */
    protected abstract void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException;

    /**
     * Gets the full path to the JAR resource that contains localized strings.
     */
    public abstract String getStringsFilePath( String projectName, Locale locale );

    /**
     * Gets the basename of the string file for a specified locale.
     * In UNIX parlance, the basename of a file is the final rightmost component of its full path.
     *
     * @param locale
     * @return
     */
    public abstract String getStringsFileBasename( String projectName, Locale locale );

    /**
     * Gets the suffix used for string files.
     *
     * @return
     */
    public abstract String getStringsFileSuffix();

    /*
    * Regular expressions for files that should be skipped when copying a jar.
    * These exclusions are specific to the subclass, general exclusions are provided by the super class.
    */
    protected abstract String[] getCopyExclusions();

    /**
     * Creates a localized JAR file, based on an existing JAR.
     *
     * @param inputJarName     full path name of the input jar
     * @param outputJarName    full path name of the output jar
     * @param locale           locale for the output jar
     * @param localizedStrings localized strings, in Properties format
     * @param deleteOnExit     should the output jar be automatically deleted when the JVM exits?
     * @throws IOException
     */
    public void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, boolean deleteOnExit ) throws IOException {

        // don't allow the input jar to be overwritten
        if ( inputJarName.equals( outputJarName ) ) {
            throw new IllegalArgumentException( "inputJarName and outputJarName must be different" );
        }

        // read the jar manifest
        Manifest manifest = JarUtils.getManifest( inputJarName );

        // read simulation.properties, change the properties related to locale
        SimulationProperties simulationProperties = JarUtils.readSimulationProperties( inputJarName );

        // read subclass-specific files
        readJarEntries( inputJarName, locale );

        // path to strings file resource in output JAR
        final String stringsFilePath = getStringsFilePath( simulationProperties.getProject(), locale );

        // regular expressions for files to exclude while copying the JAR
        ArrayList<String> list = new ArrayList<String>();
        list.add( JarFile.MANIFEST_NAME );
        list.add( "META-INF/.*\\.SF" );  // signing info
        list.add( "META-INF/.*\\.RSA" ); // signing info
        list.add( "META-INF/.*\\.DSA" ); // signing info
        list.add( SimulationProperties.FILENAME );
        list.add( stringsFilePath );
        String[] subclassExclusions = getCopyExclusions(); // subclass specific exclusions
        for ( String s : subclassExclusions ) {
            list.add( s );
        }
        String[] exclude = (String[]) list.toArray( new String[list.size()] );

        // open the input jar
        JarInputStream jarInputStream = JarUtils.openJar( inputJarName );

        // create the output jar
        File testFile = new File( outputJarName );
        if ( deleteOnExit ) {
            testFile.deleteOnExit(); // temporary file, delete when the VM exits
        }
        OutputStream outputStream = new FileOutputStream( testFile );
        JarOutputStream jarOutputStream = new JarOutputStream( outputStream, manifest );

        // copy all entries from input to output, skipping excluded files
        JarEntry jarEntry = jarInputStream.getNextJarEntry();
        while ( jarEntry != null ) {
            if ( !StringUtil.matches( exclude, jarEntry.getName() ) ) {
                jarOutputStream.putNextEntry( jarEntry );
                byte[] buf = new byte[1024];
                int len;
                while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                    jarOutputStream.write( buf, 0, len );
                }
                jarOutputStream.closeEntry();
            }
            else {
                LOGGER.info( "copying jar, skipping " + jarEntry.getName() );
            }
            jarEntry = jarInputStream.getNextJarEntry();
        }

        // header for files that we're adding to the output jar
        String header = "created by " + getClass().getName();

        // write simulation.properties, see #2463
        jarEntry = new JarEntry( SimulationProperties.FILENAME );
        jarOutputStream.putNextEntry( jarEntry );
        simulationProperties.setLocale( locale ); // set the locale
        simulationProperties.store( jarOutputStream, header );
        jarOutputStream.closeEntry();

        // write strings file
        writeStringsFile( jarOutputStream, locale, localizedStrings, stringsFilePath, header );

        // write other subclass-specific files
        writeJarEntries( jarOutputStream, simulationProperties.getProject(), locale, header );

        // close the streams
        jarInputStream.close();
        jarOutputStream.close();
    }
}
