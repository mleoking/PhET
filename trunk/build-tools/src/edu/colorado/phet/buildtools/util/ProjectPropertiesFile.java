package edu.colorado.phet.buildtools.util;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.colorado.phet.buildtools.PhetProject;
import edu.colorado.phet.common.phetcommon.util.AbstractPropertiesFile;

/**
 * Abstraction of the project properties files (eg, faraday.properties).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProjectPropertiesFile extends AbstractPropertiesFile {

    private static final String FILE_HEADER = "created by " + ProjectPropertiesFile.class.getName();

    // keys for all properties in this file
    private static final String KEY_VERSION_MAJOR = "version.major";
    private static final String KEY_VERSION_MINOR = "version.minor";
    private static final String KEY_VERSION_DEV = "version.dev";
    private static final String KEY_VERSION_REVISION = "version.revision";
    private static final String KEY_VERSION_TIMESTAMP = "version.timestamp";
    private static final String KEY_DISTRIBUTION_TAG = "distribution.tag";

    private static final DecimalFormat FORMAT_VERSION_MAJOR = new DecimalFormat( "0" );
    private static final DecimalFormat FORMAT_VERSION_MINOR = new DecimalFormat( "00" );
    private static final DecimalFormat FORMAT_VERSION_DEV = new DecimalFormat( "00" );
    private static final SimpleDateFormat FORMAT_VERSION_TIMESTAMP = new SimpleDateFormat( "MMM d, yyyy" ); // eg, Feb 2, 2009

    public ProjectPropertiesFile( PhetProject project ) {
        super( new File( project.getDataDirectory(), project.getName() + ".properties" ) );
        setHeader( FILE_HEADER );
    }

    public void setMajorVersion( int value ) {
        setProperty( KEY_VERSION_MAJOR, FORMAT_VERSION_MAJOR.format( value ) );
    }

    public int getMajorVersion() {
        return getPropertyInt( KEY_VERSION_MAJOR, -1 );
    }

    public String getMajorVersionString() {
        return getProperty( KEY_VERSION_MAJOR );
    }

    public void setMinorVersion( int value ) {
        setProperty( KEY_VERSION_MINOR, FORMAT_VERSION_MINOR.format( value ) );
    }

    public int getMinorVersion() {
        return getPropertyInt( KEY_VERSION_MINOR, -1 );
    }

    public String getMinorVersionString() {
        return getProperty( KEY_VERSION_MINOR );
    }

    public void setDevVersion( int value ) {
        setProperty( KEY_VERSION_DEV, FORMAT_VERSION_DEV.format( value ) );
    }

    public int getDevVersion() {
        return getPropertyInt( KEY_VERSION_DEV, -1 );
    }

    public String getDevVersionString() {
        return getProperty( KEY_VERSION_DEV );
    }

    public void setSVNVersion( int value ) {
        setProperty( KEY_VERSION_REVISION, value );
    }

    public int getSVNVersion() {
        return getPropertyInt( KEY_VERSION_REVISION, -1 );
    }

    public void setVersionTimestamp( long value ) {
        setProperty( KEY_VERSION_TIMESTAMP, value );
    }

    public int getVersionTimestamp() {
        return getPropertyInt( KEY_VERSION_TIMESTAMP, -1 );
    }

    public String getVersionTimestampString() {
        String s = "?";
        int seconds = getPropertyInt( KEY_VERSION_TIMESTAMP, -1 ); // seconds
        if ( seconds > 0 ) {
            Date date = new Date( seconds * 1000L ); // seconds to milliseconds 
            s = FORMAT_VERSION_TIMESTAMP.format( date );
        }
        return s;
    }

    public String getFullVersionString() {
        return getMajorVersionString() + "." + getMinorVersionString() + "." + getDevVersionString() + " (" + getSVNVersion() + ") " + getVersionTimestampString();
    }

    public void setDistributionTag( String value ) {
        setProperty( KEY_DISTRIBUTION_TAG, value );
    }

    public String getDistributionTag() {
        return getProperty( KEY_DISTRIBUTION_TAG );
    }
}
