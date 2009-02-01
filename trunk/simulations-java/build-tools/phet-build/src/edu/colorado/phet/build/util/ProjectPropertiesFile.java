package edu.colorado.phet.build.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.colorado.phet.build.PhetProject;

/**
 * Abstraction of the project properties files (eg, faraday.properties).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProjectPropertiesFile extends PropertiesFile {
    
    private static final String KEY_VERSION_MAJOR = "version.major";
    private static final String KEY_VERSION_MINOR = "version.minor";
    private static final String KEY_VERSION_DEV = "version.dev";
    private static final String KEY_VERSION_REVISION = "version.revision";
    private static final String KEY_VERSION_TIMESTAMP = "version.timestamp";
    
    private static final DecimalFormat FORMAT_VERSION_MAJOR = new DecimalFormat( "0" );
    private static final DecimalFormat FORMAT_VERSION_MINOR = new DecimalFormat( "00" );
    private static final DecimalFormat FORMAT_VERSION_DEV = new DecimalFormat( "00" );
    private static final SimpleDateFormat FORMAT_VERSION_TIMESTAMP = new SimpleDateFormat( "MM-dd-yyyy" );
    
    public ProjectPropertiesFile( PhetProject project ) {
        super( new File( project.getDataDirectory(), project.getName() + ".properties" ) );
    }
    
    public void setMajorVersion( int value ) {
        setProperty( KEY_VERSION_MAJOR, FORMAT_VERSION_MAJOR.format( value ) );
    }
    
    public int getMajorVersion() {
        return getPropertyInt( KEY_VERSION_MAJOR );
    }
    
    public String getMajorVersionString() {
        return getProperty( KEY_VERSION_MAJOR );
    }
    
    public void setMinorVersion( int value ) {
        setProperty( KEY_VERSION_MINOR, FORMAT_VERSION_MINOR.format( value ) );
    }
    
    public int getMinorVersion() {
        return getPropertyInt( KEY_VERSION_MINOR );
    }
    
    public String getMinorVersionString() {
        return getProperty( KEY_VERSION_MINOR );
    }
    
    public void setDevVersion( int value ) {
        setProperty( KEY_VERSION_DEV, FORMAT_VERSION_DEV.format( value ) );
    }
    
    public int getDevVersion() {
        return getPropertyInt( KEY_VERSION_DEV );
    }
    
    public String getDevVersionString() {
        return getProperty( KEY_VERSION_DEV );
    }
    
    public void setSVNVersion( int value ) {
        setProperty( KEY_VERSION_REVISION, String.valueOf( value ) );
    }
    
    public int getSVNVersion() {
        return getPropertyInt( KEY_VERSION_REVISION );
    }
    
    public void setVersionTimestamp( int value ) {
        setProperty( KEY_VERSION_TIMESTAMP, String.valueOf( value ) );
    }
    
    public int getVersionTimestamp() {
        return getPropertyInt( KEY_VERSION_TIMESTAMP );
    }
    
    public String getVersionTimestampString() {
        String s = "?";
        int seconds = getPropertyInt( KEY_VERSION_TIMESTAMP ); // seconds
        if ( seconds > 0 ) {
            Date date = new Date( seconds * 1000L ); // seconds to milliseconds 
            s = FORMAT_VERSION_TIMESTAMP.format( date );
        }
        return s;
    }
    
    public String getFullVersionString() {
        return getMajorVersionString() + "." + getMinorVersionString() + "." + getDevVersionString() + " (" + getSVNVersion() + ") " + getVersionTimestampString();
    }
}
