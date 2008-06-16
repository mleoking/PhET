/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.io.*;
import java.util.Properties;

import edu.colorado.phet.translationutility.TUResources;

/**
 * PropertiesIO handles input/output of properties files.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertiesIO {

    private static final String ERROR_CANNOT_READ_PROPERTIES_FILE = TUResources.getString( "error.cannotReadPropertiesFile" );
    private static final String ERROR_CANNOT_WRITE_PROPERTIES_FILE = TUResources.getString( "error.cannotWritePropertiesFile" );

    /**
     * All exceptions caught by PropertiesIO will be mapped to PropertiesIOException. 
     */
    public static class PropertiesIOException extends Exception {
        public PropertiesIOException( String message ) {
            super( message );
        }
        public PropertiesIOException( String message, Throwable cause ) {
            super( message, cause );
        }
    }
    
    /* not intended for instantiation */
    public PropertiesIO() {}
    
    /**
     * Write properties to a file.
     * 
     * @param properties
     * @param file
     * @throws PropertiesIOException
     */
    public static void write( Properties properties, File file ) throws PropertiesIOException {
        try {
            OutputStream outputStream = new FileOutputStream( file );
            String header = file.getName() + " created using PhET Translation Utility " + TUResources.getVersion();
            properties.store( outputStream, header );
            outputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new PropertiesIOException( ERROR_CANNOT_WRITE_PROPERTIES_FILE + " : " + file.getAbsolutePath(), e );
        }
    }
    
    /**
     * Reads properties from a file.
     * 
     * @param properties
     * @param file
     * @throws PropertiesIOException
     */
    public static Properties read( File file ) throws PropertiesIOException {
        Properties properties = new Properties();
        try {
            InputStream inStream = new FileInputStream( file );
            properties.load( inStream );
            inStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
            throw new PropertiesIOException( ERROR_CANNOT_READ_PROPERTIES_FILE + " : " + file.getAbsolutePath(), e );
        }
        return properties;
    }
}
