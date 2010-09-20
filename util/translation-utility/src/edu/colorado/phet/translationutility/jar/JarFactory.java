/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
public abstract class JarFactory {

    private static final Logger LOGGER = Logger.getLogger( JarFactory.class.getCanonicalName() );

    public abstract void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, String stringsFilePath, boolean deleteOnExit ) throws IOException;
    
    /**
     * Creates jar files for Flash simulations.
     */
    public static class FlashJarFactory extends JarFactory {

        /**
         * Creates a localized jar file.
         */
        public void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, String stringsFilePath, boolean deleteOnExit ) throws IOException {

            if ( inputJarName.equals( outputJarName ) ) {
                throw new IllegalArgumentException( "inputJarName and outputJarName must be different" );
            }
            
            // read the jar manifest
            Manifest manifest = JarUtils.getManifest( inputJarName );

            // read simulation.properties, change the properties related to locale
            SimulationProperties simulationProperties = JarUtils.readSimulationProperties( inputJarName );
            String projectName = simulationProperties.getProject();
            simulationProperties.setLocale( locale );
            
            // regular expressions for files to exclude while copying the JAR
            String[] exclude = {
                    JarFile.MANIFEST_NAME,
                    "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                    FlashLauncher.ARGS_FILENAME,
                    SimulationProperties.FILENAME,
                    stringsFilePath
            };
         
            // header for files that we create
            String header = "created by " + FlashJarFactory.class.getName();
            
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

            // add string properties file to output
            jarEntry = new JarEntry( stringsFilePath );
            jarOutputStream.putNextEntry( jarEntry );
            try {
                DocumentAdapter.writeProperties( localizedStrings, header, jarOutputStream );
            }
            catch ( DocumentIO.DocumentIOException e ) {
                throw new IOException( "problem converting strings to XML", e );
            }
            jarOutputStream.closeEntry();

            // add flash-launcher-args.txt
            addFlashLauncherArgsFile( jarOutputStream, projectName, locale );

            // add simulation.properties, see #2463
            jarEntry = new JarEntry( SimulationProperties.FILENAME );
            jarOutputStream.putNextEntry( jarEntry );
            simulationProperties.store( jarOutputStream, header );
            jarOutputStream.closeEntry();

            // close the streams
            jarInputStream.close();
            jarOutputStream.close();
        }
        
        /*
         * Add flash-launcher-args.txt file, used by older FlashLauncher.
         * Delete this method when all sims have been redeployed with simulation.properties.
         * See #2463.
         * @deprecated
         */
        private static void addFlashLauncherArgsFile( JarOutputStream outputStream, String projectName, Locale locale ) throws IOException {
            JarEntry jarEntry = new JarEntry( FlashLauncher.ARGS_FILENAME );
            outputStream.putNextEntry( jarEntry );
            String args = createFlashLauncherArgsString( projectName, locale );
            outputStream.write( args.getBytes() );
            outputStream.closeEntry();
        }
        
        /*
         * Creates the contents of the flash-launcher-args.txt file.
         * Format: projectName language country
         * If country doesn't have a value, use "null".
         */
        private static String createFlashLauncherArgsString( String projectName, Locale locale ) {
            String language = locale.getLanguage();
            String country = locale.getCountry();
            String s = projectName + " " + language;
            if ( country == null || country.length() == 0 ) {
                s += " " + "null";
            }
            else {
                s += " " + country;
            }
            return s;
        }
    }

    /**
     * Creates jar files for Java simulations.
     */
    public static class JavaJarFactory extends JarFactory {
        
        /**
         * Creates a localized jar file.
         */
        public void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, String stringsFilePath, boolean deleteOnExit ) throws IOException {

            if ( inputJarName.equals( outputJarName ) ) {
                throw new IllegalArgumentException( "inputJarName and outputJarName must be different" );
            }
            
            // read the jar manifest
            Manifest manifest = JarUtils.getManifest( inputJarName );

            // read simulation.properties, change the properties related to locale
            SimulationProperties simulationProperties = JarUtils.readSimulationProperties( inputJarName );
            simulationProperties.setLocale( locale );

            // read jar-launcher.properties, change the properties related to locale
            Properties jarLauncherProperties = JarUtils.readProperties( inputJarName, JARLauncher.PROPERTIES_FILE_NAME );
            if ( jarLauncherProperties == null ) {
                throw new IOException( "Cannot read " + JARLauncher.PROPERTIES_FILE_NAME + ". Are you using the most current simulation JAR from the PhET website?" );
            }
            jarLauncherProperties.setProperty( "language", locale.getLanguage() );
            String country = locale.getCountry();
            if ( country != null && country.length() > 0 ) {
                jarLauncherProperties.setProperty( "country", country );
            }
            
            // regular expressions for files to exclude while copying the input jar to the output jar
            String[] exclude = { 
                    JarFile.MANIFEST_NAME, 
                    "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                    JARLauncher.PROPERTIES_FILE_NAME,
                    SimulationProperties.FILENAME,
                    stringsFilePath, 
                    };
            
            // header for files that we create
            String header = "created by " + JavaJarFactory.class.getName();

            // open the input jar
            JarInputStream jarInputStream = JarUtils.openJar( inputJarName );

            // create the output jar
            File outputFile = new File( outputJarName );
            if ( deleteOnExit ) {
                outputFile.deleteOnExit();
            }
            OutputStream outputStream = new FileOutputStream( outputFile );
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

            // add jar-lanucher.properties
            jarEntry = new JarEntry( JARLauncher.PROPERTIES_FILE_NAME );
            jarOutputStream.putNextEntry( jarEntry );
            jarLauncherProperties.store( jarOutputStream, header );
            jarOutputStream.closeEntry();

            // add simulation.properties
            jarEntry = new JarEntry( SimulationProperties.FILENAME );
            jarOutputStream.putNextEntry( jarEntry );
            simulationProperties.store( jarOutputStream, header );
            jarOutputStream.closeEntry();
            
            // add localized strings file
            jarEntry = new JarEntry( stringsFilePath );
            jarOutputStream.putNextEntry( jarEntry );
            localizedStrings.store( jarOutputStream, header );
            jarOutputStream.closeEntry();

            // close the streams
            jarInputStream.close();
            jarOutputStream.close();
        }
    }
}
