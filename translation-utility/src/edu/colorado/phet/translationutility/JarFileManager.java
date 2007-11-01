/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.*;
import java.util.Properties;

/**
 * JarFileManager handles operations on the simulation's JAR file.
 * This includes reading/writing properties files from/to the JAR,
 * and running the JAR file.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JarFileManager {

    private static final char FILE_SEPARATOR = System.getProperty( "file.separator" ).charAt( 0 );
    
    private final String _jarFileName;
    
    /**
     * Constructor.
     * 
     * @param jarFileName
     */
    public JarFileManager( String jarFileName ) {
        _jarFileName = new String( jarFileName );
        System.out.println( "FILE_SEPARATOR=" + FILE_SEPARATOR );//XXX
        System.out.println( "_jarFileName=" + _jarFileName );//XXX
    }
    
    /**
     * Gets the project name.
     * By PhET convention, this is the JAR file name without the ".jar" suffix.
     * @return
     */
    public String getProjectName() {
        int suffixIndex = _jarFileName.indexOf( ".jar" );
        int pathSeparatorIndex = _jarFileName.lastIndexOf( FILE_SEPARATOR );
        return _jarFileName.substring( pathSeparatorIndex + 1, suffixIndex );
    }
    
    /**
     * Reads the properties file that contains the localized strings for a specified country code.
     * Extracts the properties file from the JAR and creates a Properties object.
     * 
     * @param countryCode
     * @return
     */
    public Properties readProperties( String countryCode ) {
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        extractFileFromJar( propertiesFileName );
        Properties properties = null;
        try {
            File inFile = new File( propertiesFileName );
            if ( inFile.exists() ) {
                InputStream inStream = new FileInputStream( inFile );
                properties = new Properties();
                properties.load( inStream );
                inStream.close();
            }
        }
        catch ( FileNotFoundException fnfe ) {
            fnfe.printStackTrace();
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        //XXX remove extracted properties file and any directories we created from file system
        return properties;
    }
    
    /**
     * Writes the properties containing the localized strings for a specified country code.
     * Creates a file from the Properties object and inserts the file into the JAR.
     * 
     * @param properties
     * @param countryCode
     */
    public void writeProperties( Properties properties, String countryCode ) {
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        try {
            File outFile = new File( propertiesFileName );
            outFile.createNewFile();
            OutputStream outStream = new FileOutputStream( outFile );
            String header = propertiesFileName;
            properties.store( outStream, header );
            outStream.close();
        }
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        addFileToJar( propertiesFileName );
        //XXX remove properties file from file system
    }
    
    /**
     * Runs the JAR file for a specified country code.
     * 
     * @param countryCode
     */
    public void runJarFile( String countryCode ) {
        Command.run( "java -jar -Duser.language=" + countryCode + " " + _jarFileName, false /* waitForCompletion */ );
    }
    
    /*
     * Extracts a file from the JAR.
     */
    private void extractFileFromJar( String propertiesFileName ) {
        String command = "jar xf " + _jarFileName + " " + propertiesFileName;
        Command.run( command, true /* waitForCompletion */ );
    }
    
    /*
     * Inserts a file into the JAR.
     */
    private void addFileToJar( String propertiesFileName ) {
        String command = "jar uf " + _jarFileName + " " + propertiesFileName;
        Command.run( command, true /* waitForCompletion */ );
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
