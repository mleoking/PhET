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
    private static final String ERROR_CANNOT_READ_JAR = TUResources.getString( "error.cannotReadJar" );
    private static final String ERROR_CANNOT_LOAD_PROPERTIES_FILE = TUResources.getString( "error.cannotLoadPropertiesFile" );
    private static final String ERROR_CANNOT_ADD_PROPERTIES_FILE = TUResources.getString( "error.cannotAddPropertiesFile" );
    private static final String ERROR_CANNOT_DETERMINE_PROJECT_NAME = TUResources.getString( "error.cannotDetermineProjectName" );

    private static final char FILE_SEPARATOR = System.getProperty( "file.separator" ).charAt( 0 );
    
    // general form: project-name/localization/project-name.properties
    private static final String ENGLISH_PROPERTIES_FILE_PATTERN = ".*" + FILE_SEPARATOR + "localization" + FILE_SEPARATOR + ".*-strings.properties";
    
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
                        String commonProjectFileName = commonProjectNames[i] + FILE_SEPARATOR + "localization" + FILE_SEPARATOR + commonProjectNames[i] + "-strings.properties";
                        if ( jarEntryName.matches( commonProjectFileName ) ) {
                            commonMatch = true;
                            break;
                        }
                    }
                    if ( !commonMatch ) {
                        int index = jarEntryName.indexOf( FILE_SEPARATOR );
                        projectName = jarEntryName.substring( 0, index );
                        break;
                    }
                }
                jarEntry = jarInputStream.getNextJarEntry();
            }
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
        
        System.out.println( "JarFileManager.discoverProjectName projectName=" + projectName );//XXX
        return projectName;
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
                jarInputStream.close();
            }
            catch ( IOException e ) {
                e.printStackTrace();
                throw new JarIOException( ERROR_CANNOT_LOAD_PROPERTIES_FILE + " : " + propertiesFileName );
            }
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
        String tempFileName = _jarFileName + "." + System.currentTimeMillis() + ".tmp";
        File jarFile = new File( _jarFileName );
        File tempFile = new File( tempFileName );
        
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream( jarFile );
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_OPEN_JAR + " : " + _jarFileName );
        }
        
        try {
            // input comes from the original JAR file
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            Manifest manifest = jarInputStream.getManifest();
            
            // output goes to a temp JAR file
            OutputStream outputStream = new FileOutputStream( tempFile );
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
            
            // close the output
            jarOutputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new JarIOException( ERROR_CANNOT_ADD_PROPERTIES_FILE + " : " + _jarFileName );
        }
        
        // if everything went OK, move temp file to JAR file
        tempFile.renameTo( jarFile );
    }
    
    /**
     * Runs the JAR file for a specified country code.
     * 
     * @param countryCode
     */
    public void runJarFile( String countryCode ) throws CommandException {
        Command.run( "java -jar -Duser.language=" + countryCode + " " + _jarFileName, false /* waitForCompletion */ );
    }
    
    /*
     * Gets the name of the properties file that contains localized strings for a specified country code.
     * If the country code is null, the default localization file (English) is returned.
     */
    private static String getPropertiesFileName( String projectName, String countryCode ) {
        String name = projectName + FILE_SEPARATOR + "localization" + FILE_SEPARATOR + projectName + "-strings";
        if ( countryCode != null && countryCode != "en" ) {
            name = name + "_" + countryCode;
        }
        name = name + ".properties";
        return name;
    }
}
