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

    private final String _jarFileName;
    
    /**
     * Constructor.
     * 
     * @param jarFileName
     */
    public JarFileManager( String jarFileName ) {
        _jarFileName = jarFileName;
    }
    
    /**
     * Gets the name of the JAR file.
     * @return
     */
    public String getJarFileName() {
        return _jarFileName;
    }
    
    /**
     * Gets the project name.
     * By PhET convention, this is the JAR file name without the ".jar" suffix.
     * @return
     */
    public String getProjectName() {
        int suffixIndex = _jarFileName.indexOf( ".jar" );
        return _jarFileName.substring( 0, suffixIndex );
    }
    
    /**
     * Reads the properties file that contains the localized strings for the source language.
     * Extract the properties file from the JAR and creates a Properties object.
     * 
     * @return
     */
    public Properties readSourceProperties() {
        String projectName = getProjectName();
        String propertiesFileName = getDefaultPropertiesFileName( projectName );
        extractFileFromJar( _jarFileName, propertiesFileName );
        Properties properties = new Properties();
        try {
            InputStream inStream = new FileInputStream( propertiesFileName );
            properties.load( inStream );
            inStream.close();
        }
        catch ( FileNotFoundException fnfe ) {
            fnfe.printStackTrace();
        }
        catch ( IOException ioe ) {
            ioe.printStackTrace();
        }
        return properties;
    }
    
    /**
     * Writes the properties containing the localized strings for the target language.
     * Creates a file from the Properties object and inserts the file into the JAR.
     * 
     * @param properties
     * @param countryCode
     */
    public void writeTargetProperties( Properties properties, String countryCode ) {
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
        addFileToJar( _jarFileName, propertiesFileName );
    }
    
    /**
     * Runs the JAR file for a specified country code.
     * 
     * @param countryCode
     */
    public void runJarFile( String countryCode ) {
        Command.run( "/usr/bin/java -jar -Duser.language=" + countryCode + " " + _jarFileName, false /* waitForCompletion */ );
    }
    
    /*
     * Extracts a file from the JAR.
     */
    private static void extractFileFromJar( String jarFileName, String propertiesFileName ) {
        String command = "/usr/bin/jar xf " + jarFileName + " " + propertiesFileName;
        Command.run( command, true /* waitForCompletion */ );
    }
    
    /*
     * Inserts a file into the JAR.
     */
    private static void addFileToJar( String jarFileName, String propertiesFileName ) {
        String command = "/usr/bin/jar uf " + jarFileName + " " + propertiesFileName;
        Command.run( command, true /* waitForCompletion */ );
    }
    
    /*
     * Gets the name of the properties file that contains the default (English) localized strings.
     */
    private static String getDefaultPropertiesFileName( String projectName ) {
        return getPropertiesFileName( projectName, null );
    }
    
    /*
     * Gets the name of the properties file that contains localized strings for a specified country code.
     * If the country code is null, the default localization file (English) is returned.
     */
    private static String getPropertiesFileName( String projectName, String countryCode ) {
        String name = projectName + "/localization/" + projectName + "-strings";
        if ( countryCode != null ) {
            name = name + "_" + countryCode;
        }
        name = name + ".properties";
        return name;
    }
}
