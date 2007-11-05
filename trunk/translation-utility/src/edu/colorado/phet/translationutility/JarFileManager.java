/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility;

import java.io.*;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

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
        Properties properties = new Properties();
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        try {
            // input comes from the JAR file
            InputStream inputStream = new FileInputStream( _jarFileName ); // throws FileNotFoundException
            JarInputStream jarInputStream = new JarInputStream( inputStream ); // throws IOException
            
            // look for the properties file
            JarEntry jarEntry = jarInputStream.getNextJarEntry();
            boolean found = false;
            while ( jarEntry != null ) {
                if ( jarEntry.getName().equals( propertiesFileName ) ) {
                    found = true;
                    break;
                }
                else {
                    jarEntry = jarInputStream.getNextJarEntry();
                }
            }
            if ( !found ) {
                throw new FileNotFoundException( "properties file not contained in JAR: " + propertiesFileName );
            }
            properties.load( jarInputStream ); // throws IOException
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
     * Writes the properties containing the localized strings for a specified country code.
     * This reads the entire JAR file and adds (or replaces) a properties file for the localized strings provided.
     * 
     * @param properties
     * @param countryCode
     */
    public void writeProperties( Properties properties, String countryCode ) {
        String projectName = getProjectName();
        String propertiesFileName = getPropertiesFileName( projectName, countryCode );
        String tempFileName = _jarFileName + "." + System.currentTimeMillis() + ".tmp";
        File jarFile = new File( _jarFileName );
        File tempFile = new File( tempFileName );
        try {
            // input comes from the original JAR file
            InputStream inputStream = new FileInputStream( jarFile ); // throws FileNotFoundException
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
        catch ( FileNotFoundException e ) {
            e.printStackTrace();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        // if everything went OK, move temp file to JAR file
        tempFile.renameTo( jarFile );
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
