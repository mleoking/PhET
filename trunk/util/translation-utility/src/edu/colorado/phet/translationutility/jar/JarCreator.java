/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.*;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.flashlauncher.FlashLauncher;
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
     */
    protected abstract void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException;
    
    /*
     * Regular expressions for files that should be skipped when copying a jar.
     * These exclusions are specific to the subclass, general exclusions are provided by the super class.
     */
    protected abstract String[] getExclusions();

    /**
     * Creates a localized JAR file, based on an existing JAR.
     * 
     * @param inputJarName full path name of the input jar
     * @param outputJarName full path name of the output jar
     * @param locale locale for the output jar
     * @param localizedStrings localized strings, in Properties format
     * @param stringsFilePath full path of the localized strings resource in the output jar
     * @param deleteOnExit should the output jar be automatically deleted when the JVM exits?
     * @throws IOException
     */
    public void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, String stringsFilePath, boolean deleteOnExit ) throws IOException {
        
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
        
        // regular expressions for files to exclude while copying the JAR
        ArrayList<String> list = new ArrayList<String>();
        list.add( JarFile.MANIFEST_NAME );
        list.add( "META-INF/.*\\.SF" );  // signing info
        list.add( "META-INF/.*\\.RSA" ); // signing info
        list.add( "META-INF/.*\\.DSA" ); // signing info
        list.add( SimulationProperties.FILENAME );
        list.add( stringsFilePath );
        String[] subclassExclusions = getExclusions(); // subclass specific exclusions
        for ( String s : subclassExclusions ) {
            list.add( s );
        }
        String[] exclude = (String[])list.toArray(new String[list.size()]); 
        
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
    
    /**
     * Creates jar files for Flash simulations.
     */
    public static class FlashJarCreator extends JarCreator {
        
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
                DocumentAdapter.writeProperties( localizedStrings, header, jarOutputStream );
            }
            catch ( DocumentIO.DocumentIOException e ) {
                throw new IOException( "problem converting strings to XML", e );
            }
            jarOutputStream.closeEntry();
        }
        
        /*
         * Write files to the output JAR that are required for Flash sims.
         *  Any files written here must also be excluded in getExclusions.
         */
        protected void writeJarEntries( JarOutputStream jarOutputStream, String projectName, Locale locale, String header ) throws IOException {
            // flash-launcher-args.txt
            writeFlashLauncherArgs( jarOutputStream, projectName, locale );
        }
        
        /*
         * Regular expressions for files that should be skipped when copying the jar for a Flash sim.
         * These exclusions are specific to Flash sims, general exclusions are provided by the super class.
         */
        protected String[] getExclusions() {
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
    }

    /**
     * Creates jar files for Java simulations.
     */
    public static class JavaJarCreator extends JarCreator {
        
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
         * Any files written here must also be excluded in getExclusions.
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
        protected String[] getExclusions() {
            return new String[] { JARLauncher.PROPERTIES_FILE_NAME };
        }
    }
}
