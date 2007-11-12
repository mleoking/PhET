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
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarFileManager {
    
    private static final String ERROR_CANNOT_OPEN_JAR = TUResources.getString( "error.cannotOpenJar" );
    private static final String ERROR_CANNOT_CLOSE_JAR = TUResources.getString( "error.cannotCloseJar" );
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );
    private static final String ERROR_CANNOT_DELETE_JAR = TUResources.getString( "error.cannotDeleteJar" );
    private static final String ERROR_CANNOT_EXTRACT_PROPERTIES_FILE = TUResources.getString( "error.cannotExtractPropertiesFile" );
    private static final String ERROR_CANNOT_INSERT_PROPERTIES_FILE = TUResources.getString( "error.cannotInsertPropertiesFile" );
    private static final String ERROR_CANNOT_WRITE_PROPERTIES_FILE = TUResources.getString( "error.cannotWritePropertiesFile" );
    private static final String ERROR_CANNOT_DETERMINE_PROJECT_NAME = TUResources.getString( "error.cannotDetermineProjectName" );
    private static final String ERROR_CANNOT_RENAME_TMP_FILE = TUResources.getString( "error.cannotRenameTmpFile" );

    private static final char FILE_SEPARATOR = System.getProperty( "file.separator" ).charAt( 0 );
    
    // general form: project-name/localization/project-name.properties
    private static final String ENGLISH_PROPERTIES_FILE_PATTERN = ".*/localization/.*-strings.properties";
    
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
        try {
            jarInputStream = new JarInputStream( inputStream );
            
            // look for the properties files
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                String jarEntryName = jarEntry.getName();
                if ( jarEntryName.matches( ENGLISH_PROPERTIES_FILE_PATTERN ) ) {
                    boolean commonMatch = false;
                    for ( int i = 0; i < commonProjectNames.length; i++ ) {
                        // for example, phetcommon/localization/phetcommon-strings.properties
                        String commonProjectFileName = commonProjectNames[i] + "/localization/" + commonProjectNames[i] + "-strings.properties";
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
            JarIOException e = new JarIOException( ERROR_CANNOT_DETERMINE_PROJECT_NAME + " : " + jarFileName );
            e.printStackTrace();
            throw e;
        }
        
        return projectName;
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
     * 
     * @return
     */
    public String getJarDirName() {
        String dirName = "";
        int index = _jarFileName.lastIndexOf( FILE_SEPARATOR );
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
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        
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
     */
    public void writeProperties( Properties properties, String countryCode ) throws JarIOException {
        
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        File jarFile = new File( _jarFileName );
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_OPEN_JAR + " : " + _jarFileName );
        }
        
        String tmpFileName = _jarFileName + ".tmp";
        File tmpFile = new File( tmpFileName );
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            
            // output goes to a temp JAR file
            OutputStream outputStream = new FileOutputStream( tmpFile );
            JarOutputStream jarOutputStream = new JarOutputStream( outputStream, manifest );
            
            // copy all entries from input to output, skipping the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            while ( jarEntry != null ) {
                if ( !jarEntry.getName().equals( propertiesFileName ) ) {
                    jarOutputStream.putNextEntry( jarEntry );
                    byte[] buf = new byte[1024];
                    int len;
                    while ( ( len = jarInputStream.read( buf ) ) > 0 ) {
                        jarOutputStream.write( buf, 0, len );
                    }
                    jarOutputStream.closeEntry();
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
            
            // add properties file to output
            jarEntry = new JarEntry( propertiesFileName );
            jarOutputStream.putNextEntry( jarEntry );
            String header = propertiesFileName;
            properties.store( jarOutputStream, header );
            jarOutputStream.closeEntry();
            
            // close the streams
            jarInputStream.close();
            jarOutputStream.close();
        }
        catch ( IOException e ) {
            tmpFile.delete();
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_INSERT_PROPERTIES_FILE + " : " + _jarFileName );
        }
        
        // if everything went OK, move tmp file to JAR file
        boolean renameSuccess = tmpFile.renameTo( jarFile );
        if ( !renameSuccess ) {
            JarIOException e = new JarIOException( ERROR_CANNOT_RENAME_TMP_FILE + " : " + tmpFileName + " -> " + _jarFileName );
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Save properties to a localized string file.
     * The file is put in the same directory as the JAR file.
     * 
     * @param properties
     * @param countryCode
     * @throws JarIOException
     * @return name of the saved file
     */
    public String savePropertiesToFile( Properties properties, String countryCode ) throws JarIOException {
        
        // create the filename, using same directory as JAR file
        String dirName = getJarDirName();
        String baseName = _projectName + "-strings_" + countryCode + ".properties";
        String propertiesFileName = null;
        if ( dirName != null && dirName.length() > 0 ) {
            propertiesFileName = dirName + FILE_SEPARATOR + baseName;
        }
        else {
            propertiesFileName = baseName;
        }
        
        // write the properties to the file
        try {
            File outFile = new File( propertiesFileName );
            OutputStream outputStream = new FileOutputStream( outFile );
            String header = propertiesFileName + " (" + _jarFileName + ")";
            properties.store( outputStream, header );
            outputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_WRITE_PROPERTIES_FILE + " : " + propertiesFileName );
        }
        
        return propertiesFileName;
    }
    
    /**
     * Runs the JAR file for a specified country code.
     * 
     * @param countryCode
     */
    public void runJarFile( String countryCode ) throws CommandException {
        String languageArg = "-Duser.language=" + countryCode;
        String[] cmdArray = { "java", "-jar", languageArg, _jarFileName };
        Command.run( cmdArray, false /* waitForCompletion */ );
    }
    
    /*
     * Gets the name of the properties file that contains localized strings for a specified country code.
     * If the country code is null, the default localization file (English) is returned.
     */
    private static String getPropertiesFileName( String projectName, String countryCode ) {
        String name = projectName + "/localization/" + projectName + "-strings";
        if ( countryCode != null && countryCode != "en" ) {
            name = name + "_" + countryCode;
        }
        name = name + ".properties";
        return name;
    }
}
