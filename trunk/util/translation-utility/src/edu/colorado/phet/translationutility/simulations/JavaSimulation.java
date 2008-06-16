/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility.simulations;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.TUResources;
import edu.colorado.phet.translationutility.util.Command;
import edu.colorado.phet.translationutility.util.PropertiesIO;
import edu.colorado.phet.translationutility.util.Command.CommandException;
import edu.colorado.phet.translationutility.util.PropertiesIO.PropertiesIOException;

/**
 * JavaSimulation is a Java-based simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JavaSimulation extends Simulation {
    
    private static final String TEST_JAR = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.pathSeparator" ) + "phet-test-translation.jar"; // temporary JAR file used to test translations
    
    private static final String ERROR_CANNOT_OPEN_JAR = TUResources.getString( "error.cannotOpenJar" );
    private static final String ERROR_CANNOT_CLOSE_JAR = TUResources.getString( "error.cannotCloseJar" );
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );
    private static final String ERROR_CANNOT_EXTRACT_PROPERTIES_FILE = TUResources.getString( "error.cannotExtractPropertiesFile" );
    private static final String ERROR_CANNOT_INSERT_PROPERTIES_FILE = TUResources.getString( "error.cannotInsertPropertiesFile" );
    private static final String ERROR_CANNOT_DETERMINE_PROJECT_NAME = TUResources.getString( "error.cannotDetermineProjectName" );
    private static final String ERROR_MISSING_MANIFEST = TUResources.getString( "error.missingManifest" );
    private static final String ERROR_EXPORT = TUResources.getString( "error.export" );
    private static final String ERROR_IMPORT = TUResources.getString( "error.import" );
    private static final String ERROR_RUN_JAR = TUResources.getString( "error.runJar" );
    
    private final String _jarFileName;
    private final String _projectName;

    public JavaSimulation( String jarFileName ) throws SimulationException {
        super();
        _jarFileName = jarFileName;
        
        String[] commonProjectNames = TUResources.getCommonProjectNames();
        _projectName = getSimulationProjectName( jarFileName, commonProjectNames );
    }
    
    public String getProjectName() {
        return _projectName;
    }
    
    public void test( Properties properties, String languageCode ) throws SimulationException {
        setLocalizedStrings( properties, languageCode );
        try {
            runJar( TEST_JAR, languageCode );
        }
        catch ( CommandException e ) {
            throw new SimulationException( ERROR_RUN_JAR + " : " + TEST_JAR, e );
        }
    }

    public Properties getLocalizedStrings( String languageCode ) throws SimulationException {
        return readPropertiesFromJar( _jarFileName, _projectName, languageCode );
    }

    public void setLocalizedStrings( Properties properties, String languageCode ) throws SimulationException {
        String propertiesFileName = getPropertiesResourceName( _projectName, languageCode );
        copyJarAndAddProperties( _jarFileName, TEST_JAR, propertiesFileName, properties );
    }
    
    public Properties importLocalizedStrings( File file ) throws SimulationException {
        Properties properties = null;
        try {
            properties = PropertiesIO.readPropertiesFromFile( file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( ERROR_IMPORT + " : " + file.getAbsolutePath(), e );
        }
        return properties;
    }

    public void exportLocalizedStrings( Properties properties, File file ) throws SimulationException {
        try {
            PropertiesIO.writePropertiesToFile( properties, file );
        }
        catch ( PropertiesIOException e ) {
            throw new SimulationException( ERROR_EXPORT + " : " + file.getAbsolutePath(), e );
        }
    }
    
    public String getExportFileBasename( String languageCode ) {
        return getPropertiesFileBaseName( _projectName, languageCode );
    }
    
    /*
     * Gets the name of the simulation project used to create the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     * 
     * @param jarFileName
     * @param commonProjectNames
     * @return String
     * @throws JarIOException
     */
    private static String getSimulationProjectName( String jarFileName, String[] commonProjectNames ) throws SimulationException {
        
        String projectName = null;
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + jarFileName, e );
        }
        
        JarInputStream jarInputStream = null;
        String localizationWildcard = getLocalizationResourceName( ".*" /* match for any project name */ );
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties files
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                String jarEntryName = jarEntry.getName();
                if ( jarEntryName.matches( localizationWildcard ) ) {
                    boolean commonMatch = false;
                    for ( int i = 0; i < commonProjectNames.length; i++ ) {
                        // for example, phetcommon/localization/phetcommon-strings.properties
                        String commonProjectFileName = getLocalizationResourceName( commonProjectNames[i] );
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
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_READ_JAR + " : " + jarFileName, e );
        }
        
        if ( projectName == null ) {
            throw new SimulationException( ERROR_CANNOT_DETERMINE_PROJECT_NAME + " : " + jarFileName );
        }
        
        return projectName;
    }
    
    /*
     * Creates the JAR entry name for a project's English localization file.
     * By PhET convention the form is: projectName/localization/projectName-strings.properties
     * Note that JAR entries use '/' as the file separator, rather than the platform-specific separator.
     */
    private static String getLocalizationResourceName( String projectName ) {
        return projectName + "/localization/" + projectName + "-strings.properties";
    }
    
    /*
     * Gets the name of the properties resource that contains localized strings for a specified language code.
     * If the language code is null, the default localization file (English) is returned.
     */
    private static String getPropertiesResourceName( String projectName, String languageCode ) {
        return projectName + "/localization/" + getPropertiesFileBaseName( projectName, languageCode );
    }
    
    /*
     * Gets the base name of the localized properties file for a specified project and language.
     * 
     * @param projectName
     * @param languageCode
     * @return
     */
    private static String getPropertiesFileBaseName( String projectName, String languageCode ) {
        String baseName = projectName + "-strings";
        if ( languageCode != null && languageCode != "en" ) {
            baseName = baseName + "_" + languageCode;
        }
        baseName = baseName + ".properties";
        return baseName;
    }

    /*
     * Reads a properties file from the specified JAR file.
     * The properties file contains localized strings.
     * 
     * @param jarFileName
     * @param projectName
     * @param languageCode
     * @return Properties
     * @throws JarIOException
     */
    private static Properties readPropertiesFromJar( String jarFileName, String projectName, String languageCode ) throws SimulationException {
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + jarFileName, e );
        }
        
        String propertiesFileName = getPropertiesResourceName( projectName, languageCode );
        JarInputStream jarInputStream = null;
        boolean found = false;
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( jarEntry.getName().equals( propertiesFileName ) ) {
                    found = true;
                    break;
                }
                else {
                    jarEntry = jarInputStream.getNextJarEntry();
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_READ_JAR + " : " + jarFileName, e );
        }
        
        Properties properties = null;
        if ( found ) {
            properties = new Properties();
            try {
                properties.load( jarInputStream );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                throw new SimulationException( ERROR_CANNOT_EXTRACT_PROPERTIES_FILE + " : " + propertiesFileName, e );
            }
        }
        
        try {
            jarInputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_CLOSE_JAR + " : " + jarFileName, e );
        }
    
        return properties;
    }
    
    /*
     * Copies a JAR file and adds (or replaces) a properties file.
     * The properties file contains localized strings.
     * The original JAR file is not modified.
     * 
     * @param originalJarFileName
     * @param newJarFileName
     * @param propertiesFileName
     * @param properties
     * @throws JarIOException
     */
    private static void copyJarAndAddProperties( String originalJarFileName, String newJarFileName, String propertiesFileName, Properties properties ) throws SimulationException {
        
        System.out.println( "copying " + originalJarFileName + " to " + newJarFileName );//XXX
        
        if ( originalJarFileName.equals( newJarFileName  ) ) {
            throw new IllegalArgumentException( "originalJarFileName and newJarFileName must be different" );
        }
        
        File jarFile = new File( originalJarFileName );
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_OPEN_JAR + " : " + originalJarFileName, e );
        }
        
        File testFile = new File( newJarFileName );
        testFile.deleteOnExit(); // temporary file, delete when the VM exits
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            if ( manifest == null ) {
                throw new SimulationException( ERROR_MISSING_MANIFEST + " : " + originalJarFileName );
            }
            
            // output goes to test JAR file
            OutputStream outputStream = new FileOutputStream( testFile );
            JarOutputStream testOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, skipping the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( !jarEntry.getName().equals( propertiesFileName ) ) {
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
            String header = propertiesFileName;
            properties.store( testOutputStream, header );
            testOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            testOutputStream.close();
        }
        catch ( IOException e ) {
            testFile.delete();
            e.printStackTrace();
            throw new SimulationException( ERROR_CANNOT_INSERT_PROPERTIES_FILE + " : " + newJarFileName, e );
        }
    }
    
    /*
     * Runs the JAR file for a specified language code.
     * 
     * @param languageCode
     */
    private static void runJar( String jarFileName, String languageCode ) throws CommandException {
        String languageArg = "-Duser.language=" + languageCode;
        String[] cmdArray = { "java", "-jar", languageArg, jarFileName };
        Command.run( cmdArray, false /* waitForCompletion */ );
    }

}
