/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import edu.colorado.phet.translationutility.Command.CommandException;

/**
 * JarFileManager handles operations on the simulation's JAR file.
 * This includes reading/writing properties files from/to the JAR,
 * and running the JAR file.
 * <p>
 * Notes:
 * <ul>
 * <li>file I/O uses the platform-specific file separator character in all file names
 * <li>JAR entries ignore the platform-specific file separator and always use '/'
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarFileManager {
    
    private static final String TEST_JAR_NAME = "phet-test-translation.jar";
    
    private static final String ERROR_CANNOT_OPEN_JAR = TUResources.getString( "error.cannotOpenJar" );
    private static final String ERROR_CANNOT_CLOSE_JAR = TUResources.getString( "error.cannotCloseJar" );
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );
    private static final String ERROR_CANNOT_EXTRACT_PROPERTIES_FILE = TUResources.getString( "error.cannotExtractPropertiesFile" );
    private static final String ERROR_CANNOT_INSERT_PROPERTIES_FILE = TUResources.getString( "error.cannotInsertPropertiesFile" );
    private static final String ERROR_CANNOT_READ_PROPERTIES_FILE = TUResources.getString( "error.cannotReadPropertiesFile" );
    private static final String ERROR_CANNOT_WRITE_PROPERTIES_FILE = TUResources.getString( "error.cannotWritePropertiesFile" );
    private static final String ERROR_CANNOT_DETERMINE_PROJECT_NAME = TUResources.getString( "error.cannotDetermineProjectName" );
    private static final String ERROR_MISSING_MANIFEST = TUResources.getString( "error.missingManifest" );
    
    private final String _jarFileName;
    private final String[] _commonProjectNames;
    private String _projectName;
    
    public static class JarIOException extends Exception {
        public JarIOException( String message ) {
            super( message );
        }
    }
    
    /**
     * Constructor.
     * 
     * @param jarFileName
     * @param commonProjectNames
     */
    public JarFileManager( String jarFileName, String[] commonProjectNames ) {
        _jarFileName = new String( jarFileName );
        _commonProjectNames = commonProjectNames;
        try {
            _projectName = discoverProjectName( _jarFileName, _commonProjectNames );
        }
        catch ( JarIOException e ) {
            ExceptionHandler.handleFatalException( e );
        }
    }
    
    /*
     * Discovers the name of the simulation project used to create the JAR file.
     * We search for localization files in the JAR file.
     * The first localization file that does not belong to a common project is assumed
     * to belong to the simulation, and we extract the project name from the localization file name.
     * 
     * @param jarFileName
     * @param commonProjectNames
     * @return
     * @throws JarIOException
     */
    private static String discoverProjectName( String jarFileName, String[] commonProjectNames ) throws JarIOException {
        
        String projectName = null;
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_OPEN_JAR + " : " + jarFileName );
        }
        
        JarInputStream jarInputStream = null;
        String localizationWildcard = getJarEntryNameForLocalization( ".*" /* match for any project name */ );
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
                        String commonProjectFileName = getJarEntryNameForLocalization( commonProjectNames[i] );
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
            throw new JarIOException( ERROR_CANNOT_READ_JAR + " : " + jarFileName );
        }
        
        if ( projectName == null ) {
            throw new JarIOException( ERROR_CANNOT_DETERMINE_PROJECT_NAME + " : " + jarFileName );
        }
        
        return projectName;
    }
    
    /*
     * Creates the JAR entry name for a project's English localization file.
     * By PhET convention the form is: projectName/localization/projectName-strings.properties
     * Note that JAR entries use '/' as the file separator, rather than the platform-specific separator.
     */
    private static String getJarEntryNameForLocalization( String projectName ) {
        return projectName + "/localization/" + projectName + "-strings.properties";
    }
    
    /**
     * Gets the JAR file name.
     * 
     * @return
     */
    public String getJarFileName() {
        return _jarFileName;
    }
    
    /**
     * Gets the directory portion of the JAR file name.
     * For example, if JAR filename is /usr/home/cmalley/foo.jar,
     * then this method returns /usr/home/cmalley.
     * This method assumes that the platform-specific file separator character
     * is used to specify the JAR file name.
     * 
     * @return
     */
    public String getJarDirName() {
        String dirName = "";
        int index = _jarFileName.lastIndexOf( File.separatorChar );
        if ( index != -1 ) {
            dirName = _jarFileName.substring( 0, index );
        }
        return dirName;
    }
    
    /**
     * Gets the project name.
     * @return
     */
    public String getProjectName() {
        return _projectName;
    }
    
    /**
     * Gets the names of the common projects that the JAR file contains.
     * @return
     */
    public String[] getCommonProjectNames() {
        return _commonProjectNames;
    }
    
    /**
     * Reads the properties file that contains the localized strings for a specified country code.
     * Extracts the properties file from the JAR and creates a Properties object.
     * 
     * @param countryCode
     * @throws JarIOException if there is a problem reading the properties file from the JAR
     * @return Properties, null if properties file does not exist
     */
    public Properties readProperties( String countryCode ) throws JarIOException {
        
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesResourceName( projectName, countryCode );
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( _jarFileName );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_OPEN_JAR + " : " + _jarFileName );
        }
        
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
            throw new JarIOException( ERROR_CANNOT_READ_JAR + " : " + _jarFileName );
        }
        
        Properties properties = null;
        if ( found ) {
            properties = new Properties();
            try {
                properties.load( jarInputStream );
            }
            catch ( IOException e ) {
                e.printStackTrace();
                throw new JarIOException( ERROR_CANNOT_EXTRACT_PROPERTIES_FILE + " : " + propertiesFileName );
            }
        }
        
        try {
            jarInputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_CLOSE_JAR + " : " + _jarFileName );
        }
    
        return properties;
    }
    
    /**
     * Writes the properties containing the localized strings for a specified country code.
     * This reads the entire JAR file and adds (or replaces) a properties file for the localized strings provided.
     * 
     * @param properties
     * @param countryCode
     * @throws JarIOException if the properties cannot be written to the JAR file
     * @return new JAR file name
     */
    public String writeProperties( Properties properties, String countryCode ) throws JarIOException {
        
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesResourceName( projectName, countryCode );
        File jarFile = new File( _jarFileName );
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_OPEN_JAR + " : " + _jarFileName );
        }
        
        String testFileName = getJarDirName() + File.separatorChar + TEST_JAR_NAME;
        File testFile = new File( testFileName );
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            if ( manifest == null ) {
                throw new JarIOException( ERROR_MISSING_MANIFEST + " : " + _jarFileName );
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
            throw new JarIOException( ERROR_CANNOT_INSERT_PROPERTIES_FILE + " : " + _jarFileName );
        }
        
        return testFileName;
    }

    /**
     * Saves properties to a file.
     * 
     * @param properties
     * @param file
     * @throws JarIOException
     */
    public static void savePropertiesToFile( Properties properties, File file ) throws JarIOException {
        try {
            OutputStream outputStream = new FileOutputStream( file );
            String header = file.getCanonicalPath();
            properties.store( outputStream, header );
            outputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_WRITE_PROPERTIES_FILE + " : " + file.getAbsolutePath() );
        }
    }
    
    /**
     * Reads properties from a file.
     * 
     * @param properties
     * @param file
     * @throws JarIOException
     */
    public static Properties readPropertiesFromFile( File file ) throws JarIOException {
        Properties properties = new Properties();
        try {
            InputStream inStream = new FileInputStream( file );
            properties.load( inStream );
            inStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_READ_PROPERTIES_FILE + " : " + file.getAbsolutePath() );
        }
        return properties;
    }
    
    /**
     * Runs the JAR file for a specified country code.
     * 
     * @param countryCode
     */
    public static void runJarFile( String jarFileName, String countryCode ) throws CommandException {
        String languageArg = "-Duser.language=" + countryCode;
        String[] cmdArray = { "java", "-jar", languageArg, jarFileName };
        Command.run( cmdArray, false /* waitForCompletion */ );
    }
    
    /*
     * Gets the name of the properties resource that contains localized strings for a specified country code.
     * If the country code is null, the default localization file (English) is returned.
     */
    private static String getPropertiesResourceName( String projectName, String countryCode ) {
        return projectName + "/localization/" + getPropertiesFileBaseName( projectName, countryCode );
    }
    
    /**
     * Gets the base name of the localized properties file for a specified project and language.
     * 
     * @param projectName
     * @param countryCode
     * @return
     */
    public static String getPropertiesFileBaseName( String projectName, String countryCode ) {
        String baseName = projectName + "-strings";
        if ( countryCode != null && countryCode != "en" ) {
            baseName = baseName + "_" + countryCode;
        }
        baseName = baseName + ".properties";
        return baseName;
    }
}
