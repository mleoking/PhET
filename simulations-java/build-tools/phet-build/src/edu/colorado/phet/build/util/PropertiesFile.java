package edu.colorado.phet.build.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Properties;

/**
 * PropertiesFile is the abstraction of a properties file.
 * Setting a value stores the value in the file immediately.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PropertiesFile {
    
    private final File propertiesFile;
    private final Properties properties;

    public PropertiesFile( String filename ) {
        this( new File( filename ) );
    }
    
    public PropertiesFile( File file ) {
        this.propertiesFile = file;
        this.properties = loadProperties( file );
    }

    /*
     * Loads the properties from the file, if it exists.
     */
    private static Properties loadProperties( File file ) {
        Properties properties = new Properties();
        if ( file.exists() ) {
            try {
                properties.load( new BufferedInputStream( new FileInputStream( file ) ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }
        return properties;
    }
    
    /**
     * Gets the names of all properties in the file.
     * @return
     */
    public Enumeration getPropertyNames() {
        return properties.propertyNames();
    }
    
    /**
     * Setting a property stores it immediately.
     */
    public void setProperty( String key, String value ) {
        properties.setProperty( key, value );
        try {
            properties.store( new FileOutputStream( propertiesFile ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    /**
     * Gets a property value as a String.
     * @param key
     * @return String value
     */
    public String getProperty( String key ) {
        return properties.getProperty( key );
    }
    
    /**
     * Gets a property as an integer value.
     * If the property value can't be converted to an integer, -1 is returned.
     * @param key
     * @return int value
     */
    public int getPropertyInt( String key ) {
        int i = -1;
        String s = getProperty( key );
        if ( s == null ) {
            System.err.println( "PropertiesFile.getPropertyInt: " + key + " is missing from file " + propertiesFile.getAbsolutePath() );
        }
        else {
            try {
                i = Integer.parseInt( s );
            }
            catch ( NumberFormatException e ) {
                System.err.println( "PropertiesFile.getPropertyInt: " + key + " is not an integer in file " + propertiesFile.getAbsolutePath() );
            }
        }
        return i;
    }
    
    public String toString() {
        return properties.toString();
    }
}
