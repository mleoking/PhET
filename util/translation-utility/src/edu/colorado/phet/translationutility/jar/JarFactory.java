/* Copyright 2010, University of Colorado */

package edu.colorado.phet.translationutility.jar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.*;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.flashlauncher.FlashLauncher;
import edu.colorado.phet.flashlauncher.util.SimulationProperties;
import edu.colorado.phet.translationutility.JarUtils;

/**
 * Creates jar files for PhET sims.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarFactory {

    private static final Logger LOGGER = Logger.getLogger( JarFactory.class.getCanonicalName() );

    /**
     * Creates jar files for Flash simulations.
     */
    public static class FlashJarFactory {

        /**
         * Creates a localized jar file.
         */
        public static final void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, boolean deleteOnExit ) throws IOException {

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
            String stringsFileName = getStringsPath( projectName, locale );
            String[] exclude = {
                    JarFile.MANIFEST_NAME,
                    "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                    FlashLauncher.ARGS_FILENAME,
                    SimulationProperties.FILENAME,
                    stringsFileName
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
            jarEntry = new JarEntry( stringsFileName );
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
        
        /**
         * Gets the path to the JAR resource that contains localized strings.
         */
        public static String getStringsPath( String projectName, Locale locale ) {
            // XML resources are at the top-level of the JAR, so resource path is the same as resource name
            return getStringsName( projectName, locale );
        }
        
        /**
         * Gets the name of of the JAR resource for an XML document.
         */
        public static String getStringsName( String projectName, Locale locale ) {
            String stringsBasename = getStringsBasename( projectName );
            String format = "{0}-strings_{1}" + getStringsFileSuffix();  // eg, curve-fit-strings_en.xml
            Object[] args = { stringsBasename, locale };
            return MessageFormat.format( format, args );
        }
        
        /*
         * Gets the basename of the strings file.
         * <p>
         * This is typically the same as the project name, except for common strings.
         * PhET common strings are bundled into their own JAR file for use with translation utility.
         * The JAR file must be built & deployed via a dummy sim named "flash-common-strings", 
         * found in trunk/simulations-flash/simulations.  If the project name is "flash-common-strings",
         * we really want to load the common strings which are in files with basename "common".
         * So we use "common" as the project name.
         */
        private static String getStringsBasename( String projectName ) {
            String basename = projectName;
            if ( basename.equals( "flash-common-strings" ) ) {
                basename = "common";
            }
            return basename;
        }
        
        public static String getStringsFileSuffix() {
            return ".xml";
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
    public static class JavaJarFactory {

        /**
         * Creates a localized jar file.
         */
        public static final void createLocalizedJar( String inputJarName, String outputJarName, Locale locale, Properties localizedStrings, boolean deleteOnExit ) throws IOException {

            if ( inputJarName.equals( outputJarName ) ) {
                throw new IllegalArgumentException( "inputJarName and outputJarName must be different" );
            }
            
            // read the jar manifest
            Manifest manifest = JarUtils.getManifest( inputJarName );

            // read simulation.properties, change the properties related to locale
            SimulationProperties simulationProperties = JarUtils.readSimulationProperties( inputJarName );
            String projectName = simulationProperties.getProject();
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
            String stringsFileName = getStringsPath( projectName, locale );
            String[] exclude = { 
                    JarFile.MANIFEST_NAME, 
                    "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                    JARLauncher.PROPERTIES_FILE_NAME,
                    SimulationProperties.FILENAME,
                    stringsFileName, 
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
            jarEntry = new JarEntry( stringsFileName );
            jarOutputStream.putNextEntry( jarEntry );
            localizedStrings.store( jarOutputStream, header );
            jarOutputStream.closeEntry();

            // close the streams
            jarInputStream.close();
            jarOutputStream.close();
        }

        /**
         * Gets the path to the JAR resource that contains localized strings for 
         * a specified project and locale. If locale is null, the fallback resource
         * path is returned. 
         */
        public static String getStringsPath( String projectName, Locale locale ) {
            String dirName = getStringsBasename( projectName );
            String fileName = getStringsName( projectName, locale );
            return dirName + "/localization/" + fileName;
        }

        /**
         * Gets the name of the JAR resource that contains localized strings for 
         * a specified project and locale. For example, faraday-strings_es.properties
         * <p>
         * If locale is null, the name of the fallback resource is returned.
         * The fallback name does not contain a locale, and contains English strings.
         * For example: faraday-strings.properties
         * <p>
         * NOTE: Support for the fallback name is provided for backward compatibility.
         * All Java simulations should migrate to the convention of including "en" in the 
         * resource name of English localization files.
         */
        public static String getStringsName( String projectName, Locale locale ) {
            String stringsBasename = getStringsBasename( projectName );
            String basename = null;
            if ( locale == null ) {
                basename = stringsBasename + "-strings" + getStringsFileSuffix(); // fallback basename contains no language code
            }
            else {
                String localeString = LocaleUtils.localeToString( locale );
                basename = stringsBasename + "-strings_" + localeString + getStringsFileSuffix();
            }
            return basename;
        }
        
        /*
         * Gets the basename of the resource that contains localized strings.
         * <p>
         * This is typically the same as the project name, except for common strings.
         * PhET common strings are bundled into their own JAR file for use with Translation Utility.
         * The JAR file must be built & deployed via a dummy sim named "java-common-strings", 
         * found in trunk/simulations-flash/simulations.  If the project name is "java-common-strings",
         * we really want to load the common strings which are in files with basename "phetcommon".
         * So we use "phetcommon" as the project name.
         */
        private static String getStringsBasename( String projectName ) {
            String basename = projectName;
            if ( basename.equals( "java-common-strings" ) ) {
                basename = "phetcommon";
            }
            return basename;
        }

        public static String getStringsFileSuffix() {
            return ".properties";
        }
    }
}
