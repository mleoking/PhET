/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Properties;
import java.util.jar.*;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.JARLauncher;
import edu.colorado.phet.translationutility.TUConstants;
import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation supports of Java-based simulations.
 * Java simulations use Java properties files to store localized strings.
 * There is one properties file per language.
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
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public JavaSimulation( String jarFileName ) throws SimulationException {
        super( jarFileName );
    }
    
    //----------------------------------------------------------------------------
    // Public interface
    //----------------------------------------------------------------------------
    
    public void testStrings( Properties properties, String languageCode ) throws SimulationException {
        String propertiesFileName = getPropertiesResourceName( getProjectName(), languageCode );
        createTestJar( getJarFileName(), TEST_JAR, getManifest(), propertiesFileName, properties, languageCode );
        try {
            String[] cmdArray = { "java", 
                    "-D" + PhetCommonConstants.PROPERTY_PHET_LANGUAGE + "=" + languageCode, /* TODO: #1249, delete after IOM */
                    "-Djavaws.phet.locale=" + languageCode, /* TODO: #1249, delete after IOM */
                    "-Duser.language=" + languageCode, /* TODO: #1249, delete after IOM */
                    "-jar", TEST_JAR };
            Command.run( cmdArray, false /* waitForCompletion */ );
        }
        catch ( CommandException e ) {
            throw new SimulationException( e );
        }
    }

    public Properties getStrings( String languageCode ) throws SimulationException {
        
        // Load strings from a resource file.
        String propertiesFileName = getPropertiesResourceName( getProjectName(), languageCode );
        Properties properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        
        // English strings may be in a fallback resource file.
        if ( properties == null && languageCode.equals( TUConstants.ENGLISH_LANGUAGE_CODE ) ) {
            propertiesFileName = getFallbackPropertiesResourceName( getProjectName() );
            properties = readPropertiesFromJar( getJarFileName(), propertiesFileName );
        }
        
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
            String projectName = getActualProjectName( getJarFileName() );
            String projectVersion = getProjectVersion( projectName + TUConstants.RESOURCE_PATH_SEPARATOR + projectName + ".properties" ); // eg, faraday/faraday.properties
            String header = getTranslationFileHeader( file.getName(), projectName, projectVersion );
            PropertiesIO.write( properties, header, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( e );
        }
    }
    
    public String getSubmitBasename( String languageCode ) {
        return getPropertiesResourceBasename( getProjectName(), languageCode );
    }
    
    /*
     * Gets the project name for the simulation, adjusted to handle common strings.
     * <p>
     * PhET common strings are bundled into their own JAR file for use with translation utility.
     * Because the PhET build process is so inflexible, the JAR file must be built & deployed
     * via a dummy sim named "java-common-strings", found in trunk/simulations-java/simulations.
     * If the project name is "java-common-strings", we really want to load the common strings
     * which are in files with basename "phetcommon-strings".  So we use "phetcommon" 
     * as the project name.
     */
    protected String getProjectName( String jarFileName ) throws SimulationException {
        String projectName = getActualProjectName( jarFileName );
        if ( projectName.equals( "java-common-strings" ) ) {
            projectName = "phetcommon";
        }
        return projectName;
    }
    
    /*
     * Gets the project name for the simulation.
     * 
     * @param jarFileName
     */
    private String getActualProjectName( String jarFileName ) throws SimulationException {
        
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
     * Gets the full name of the fallback JAR resource that contains 
     * English strings for a specified project.
     */
    private static String getFallbackPropertiesResourceName( String projectName ) {
        return getPropertiesResourceName( projectName, null /* languageCode */ );
    }
    
    /*
     * Gets the full name of the JAR resource that contains localized strings for 
     * a specified project and language. If language code is null, the fallback resource
     * name is returned. 
     */
    private static String getPropertiesResourceName( String projectName, String languageCode ) {
        String basename = getPropertiesResourceBasename( projectName, languageCode );
        return projectName + TUConstants.RESOURCE_PATH_SEPARATOR + "localization" + TUConstants.RESOURCE_PATH_SEPARATOR + basename;
    }
    
    /*
     * Gets the basename of the JAR resource that contains localized strings for 
     * a specified project and language. For example, faraday-strings_es.properties
     * <p>
     * If language code is null, the basename of the fallback resource is returned.
     * The fallback name does not contain a language code, and contains English strings.
     * For example: faraday-strings.properties
     * <p>
     * NOTE: Support for the fallback name is provided for backward compatibility.
     * All Java simulations should migrate to the convention of including "en" in the 
     * resource name of English localization files.
     * 
     * @param projectName
     * @param languageCode
     * @return
     */
    private static String getPropertiesResourceBasename( String projectName, String languageCode ) {
        String basename = null;
        if ( languageCode == null ) {
            basename = projectName + "-strings" + ".properties"; // fallback basename contains no language code
        }
        else {
            basename = projectName + "-strings_" + languageCode + ".properties";
        }
        return basename;
    }
    
    /*
     * Discovers the name of the project that was used to build the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     * 
     * @param jarFileName
     * @return String
     * @throws SimulationException
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
     * 
     * The original JAR file is not modified.
     * 
     * @param originalJarFileName
     * @param newJarFileName
     * @param manifest
     * @param propertiesFileName
     * @param properties
     * @param languageCode
     */
    private static void createTestJar( 
            String originalJarFileName, String newJarFileName, Manifest manifest, 
            String propertiesFileName, Properties properties, String languageCode ) throws SimulationException {
        
        if ( originalJarFileName.equals( newJarFileName  ) ) {
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
        jarLauncherProperties.setProperty( "language", languageCode );
        
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
        
        // files to skip while copying the JAR
        String[] skipFileNames = {
                JarFile.MANIFEST_NAME,
                propertiesFileName,
                JARLauncher.PROPERTIES_FILE_NAME,
                "locale.properties", "options.properties" /*TODO: #1249, delete after IOM */
        };
        
        // create the test JAR file
        File testFile = new File( newJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, skipping the properties file & manifest
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                
                if ( !contains( skipFileNames, jarEntry.getName() ) ) {
                    
                    testOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        testOutputStream.write( buf, 0, len );
                    }
                    testOutputStream.closeEntry();
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
                // backward compatibility for locale.properties
                Properties localeProperties = new Properties();
                localeProperties.setProperty( "language", languageCode );
                jarEntry = new JarEntry( "locale.properties" );
                testOutputStream.putNextEntry( jarEntry );
                localeProperties.store( testOutputStream, "created by " + JavaSimulation.class.getName() );
                testOutputStream.closeEntry();
                
                // backward compatibility for options.properties
                Properties optionsProperties = new Properties();
                optionsProperties.setProperty( "locale", languageCode );
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
            throw new SimulationException( "cannot add localized strings to jar file: " + newJarFileName, e );
        }
    }
    
    /*
     * Is a specified string in an array of strings?
     */
    private static boolean contains( String[] array, String s ) {
        boolean found = false;
        for ( int i = 0; i < array.length; i++ ) {
            if ( array[i].equals( s ) ) {
                found = true;
                break;
            }
        }
        return found;
    }
}
