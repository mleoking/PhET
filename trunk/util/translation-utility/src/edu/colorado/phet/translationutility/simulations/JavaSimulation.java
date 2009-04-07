/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Locale;
import java.util.Properties;
import java.util.jar.*;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.common.phetcommon.util.LocaleUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.TULogger;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per locale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation extends AbstractSimulation {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // regular expression that matches localization string files
    private static final String REGEX_LOCALIZATION_FILES = ".*-strings.*\\.properties";
    
    // project properties file and properties
    private static final String PROJECT_NAME_PROPERTY = "project.name"; //TODO: #1249, delete after IOM
    
    private static final String COMMON_STRINGS_PROJECT = "java-common-strings";
    private static final String COMMON_STRINGS_BASENAME = "phetcommon";
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public JavaSimulation( String jarFileName ) throws SimulationException {
        super( jarFileName );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public void testStrings( Properties properties, Locale locale ) throws SimulationException {
        String testJarFileName = createTestJar( properties, locale );
        String localeString = LocaleUtils.localeToString( locale );
        try {
            String[] cmdArray = { "java", 
                    "-D" + PhetCommonConstants.PROPERTY_PHET_LANGUAGE + "=" + localeString, /* TODO: #1249, delete after IOM */
                    "-Djavaws.phet.locale=" + localeString, /* TODO: #1249, delete after IOM */
                    "-Duser.language=" + localeString, /* TODO: #1249, delete after IOM */
                    "-jar", testJarFileName };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( Locale locale ) throws SimulationException {
        
        // Load strings from a resource file.
        String propertiesFileName = getStringsPath( getProjectName(), locale );
        Properties properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        
        // English strings may be in a fallback resource file.
        if ( properties == null && locale.equals( TUConstants.ENGLISH_LOCALE ) ) {
            propertiesFileName = getFallbackStringsPath( getProjectName() );
            properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        }
        
        TULogger.log( "JavaSimulation: loaded strings from " + propertiesFileName );
        return properties;
    }

    public Properties loadStrings( File file ) throws SimulationException {
        Properties properties = null;
        try {
            properties = PropertiesIO.read( file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
        return properties;
    }

    public void saveStrings( Properties properties, File file ) throws SimulationException {
        try {
            String projectName = getProjectName();
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + ".properties" ); // eg, faraday/faraday.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            PropertiesIO.write( properties, header, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getSubmitBasename( Locale locale ) {
        return getStringsName( getProjectName(), locale );
    }
    
    /*
     * Gets the project name for the simulation.
     */
    protected String getProjectName( String jarFileName ) throws SimulationException {
        
        String projectName = null;
        Properties projectProperties = null;

        //TODO: #1249, delete after IOM
        projectProperties = readPropertiesFromJar( jarFileName, "project.properties" );
        if ( projectProperties != null ) {
            projectName = projectProperties.getProperty( PROJECT_NAME_PROPERTY );
        }

        // The project name is identified in the properties file read by JARLauncher
        projectProperties = readPropertiesFromJar( jarFileName, JARLauncher.PROPERTIES_FILE_NAME );
        if ( projectProperties != null ) {
            projectName = projectProperties.getProperty( PROJECT_NAME_PROPERTY );
        }
        
        //TODO: #1249, delete after IOM
        if ( projectName == null ) {
            // For older sims (or if PROJECT_NAME_PROPERTY is missing), discover the project name
            projectName = discoverProjectName( jarFileName );
        }
        
        if ( projectName == null ) {
            throw new SimulationException( "could not determine this simulation's project name: " + jarFileName );
        }
        
        return projectName;
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    /*
     * Gets the path to the fallback JAR resource that contains 
     * English strings for a specified project.
     */
    private static String getFallbackStringsPath( String projectName ) {
        return getStringsPath( projectName, null /* locale */ );
    }
    
    /*
     * Gets the path to the JAR resource that contains localized strings for 
     * a specified project and locale. If locale is null, the fallback resource
     * path is returned. 
     */
    private static String getStringsPath( String projectName, Locale locale ) {
        String dirName = getStringsBasename( projectName );
        String fileName = getStringsName( projectName, locale );
        return dirName + TUConstants.RESOURCE_PATH_SEPARATOR + "localization" + TUConstants.RESOURCE_PATH_SEPARATOR + fileName;
    }
    
    /*
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
    private static String getStringsName( String projectName, Locale locale ) {
        String stringsBasename = getStringsBasename( projectName );
        String basename = null;
        if ( locale == null ) {
            basename = stringsBasename + "-strings" + ".properties"; // fallback basename contains no language code
        }
        else {
            String localeString = LocaleUtils.localeToString( locale );
            basename = stringsBasename + "-strings_" + localeString + ".properties";
        }
        return basename;
    }
    
    /*
     * Gets the basename of the resource that contains localized strings.
     * <p>
     * This is typically the same as the project name, except for common strings.
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * The JAR file must be built & deployed via a dummy sim named COMMON_STRINGS_PROJECT, 
     * found in trunk/simulations-flash/simulations.  If the project name is COMMON_STRINGS_PROJECT,
     * we really want to load the common strings which are in files with basename COMMON_STRINGS_BASENAME.
     * So we use COMMON_STRINGS_BASENAME as the project name.
     */
    private static String getStringsBasename( String projectName ) {
        String basename = projectName;
        if ( basename.equals( COMMON_STRINGS_PROJECT ) ) {
            basename = COMMON_STRINGS_BASENAME;
        }
        return basename;
    }
    
    /*
     * Discovers the name of the project that was used to build the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     */
    private static String discoverProjectName( String jarFileName ) throws SimulationException {
        
        String[] commonProjectNames = TUResources.getCommonProjectNames();
        
        String projectName = null;
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties files
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                String jarEntryName = jarEntry.getName();
                if ( jarEntryName.matches( REGEX_LOCALIZATION_FILES ) ) {
                    boolean commonMatch = false;
                    for ( int i = 0; i < commonProjectNames.length; i++ ) {
                        String commonProjectFileName = ".*" + commonProjectNames[i] + "-strings.*\\.properties";
                        if ( jarEntryName.matches( commonProjectFileName ) ) {
                            commonMatch = true;
                            break;
                        }
                    }
                    if ( !commonMatch ) {
                        int index = jarEntryName.indexOf( '/' );
                        projectName = jarEntryName.substring( 0, index );
                        break;
                    }
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            jarInputStream.close();
        }
        catch ( IOException e ) {
            throw new SimulationException( "error reading jar file: " + jarFileName, e );
        }
        
        return projectName;
    }
    
    /*
     * Creates a test JAR by doing the following:
     * 1. copies the original JAR file
     * 2. adds or replaces a properties file containing localized strings
     * 3. adds or replaces a properties file used by JARLauncher to set the locale
     * 4. removes files related to digital signatures
     * 
     * The original JAR file is not modified.
     */
    private String createTestJar( Properties properties, Locale locale ) throws SimulationException {

        final String testJarFileName = TEST_JAR;
        final String originalJarFileName = getJarFileName();
        
        if ( originalJarFileName.equals( testJarFileName ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
        }
        
        // read the JARLaucher properties file from the original JAR
        Properties jarLauncherProperties = null;
        try {
            jarLauncherProperties = readPropertiesFromJar( originalJarFileName, JARLauncher.PROPERTIES_FILE_NAME );
        }
        catch ( SimulationException e ) {  //TODO: #1249, delete after IOM, all JARs must contain jar-launcher.properties
            System.err.println( "WARNING: old-style simulation does not contain " + JARLauncher.PROPERTIES_FILE_NAME );
        }
        //TODO: #1249, delete this block after IOM
        if ( jarLauncherProperties == null ) {
            jarLauncherProperties = new Properties();
        }
        
        // set the properties related to locale
        jarLauncherProperties.setProperty( "language", locale.getLanguage() );
        String country = locale.getCountry();
        if ( country != null && country.length() > 0 ) {
            jarLauncherProperties.setProperty( "country", country );
        }
        
        // open the original JAR file
        File jarFile = new File( originalJarFileName );
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( "jar file not found: " + originalJarFileName, e );
        }
        
        // regular expressions for files to exclude while copying the JAR
        String propertiesFileName = getStringsPath( getProjectName(), locale );
        String[] exclude = {
                JarFile.MANIFEST_NAME,
                "META-INF/.*\\.SF", "META-INF/.*\\.RSA", "META-INF/.*\\.DSA", /* signing information */
                propertiesFileName,
                JARLauncher.PROPERTIES_FILE_NAME,
                "locale.properties", "options.properties" /*TODO: #1249, delete after IOM */
        };
        
        // create the test JAR file
        File testFile = new File( testJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            Manifest manifest = getManifest();
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, excluding some entries
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                
                if ( !StringUtil.matches( exclude, jarEntry.getName() ) ) {
                    
                    testOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        testOutputStream.write( buf, 0, len );
                    }
                    testOutputStream.closeEntry();
                }
                else {
                    TULogger.log( "JavaSimulation: copying jar, skipping " + jarEntry.getName() );
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            // add properties file to output
            jarEntry = new JarEntry( propertiesFileName );
            testOutputStream.putNextEntry( jarEntry );
            properties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
            testOutputStream.closeEntry();
            
            // add JARLauncher properties to output
            jarEntry = new JarEntry( JARLauncher.PROPERTIES_FILE_NAME );
            testOutputStream.putNextEntry( jarEntry );
            jarLauncherProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
            testOutputStream.closeEntry();
            
            //TODO: #1249, delete after IOM
            {
                String localeString = LocaleUtils.localeToString( locale );
                
                // backward compatibility for locale.properties
                Properties localeProperties = new Properties();
                localeProperties.setProperty( "language", localeString );
                jarEntry = new JarEntry( "locale.properties" );
                testOutputStream.putNextEntry( jarEntry );
                localeProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
                testOutputStream.closeEntry();
                
                // backward compatibility for options.properties
                Properties optionsProperties = new Properties();
                optionsProperties.setProperty( "locale", localeString );
                jarEntry = new JarEntry( "options.properties" );
                testOutputStream.putNextEntry( jarEntry );
                optionsProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
                testOutputStream.closeEntry();
            }

            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            testFile.delete();
            e.printStackTrace();
            throw new SimulationException( "cannot add localized strings to jar file: " + testJarFileName, e );
        }
        
        return testJarFileName;
    }
}
