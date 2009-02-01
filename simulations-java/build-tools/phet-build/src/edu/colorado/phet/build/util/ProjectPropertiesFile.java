package edu.colorado.phet.build.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Properties;

import edu.colorado.phet.build.PhetProject;

/**
 * Abstraction of the project properties files (eg, faraday.properties).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ProjectPropertiesFile {
    
    private PhetProject project;
    
    public ProjectPropertiesFile( PhetProject project ) {
        this.project = project;
    }

    public void setMajorVersion( int value ) {
        saveProperty( "version.major", String.valueOf( value ) );
    }
    
    public void setMinorVersion( int value ) {
        saveProperty( "version.minor", new DecimalFormat( "00" ).format( value ) );
    }
    
    public void setDevVersion( int value ) {
        saveProperty( "version.dev", new DecimalFormat( "00" ).format( value ) );
    }
    
    public void setSVNVersion( int value ) {
        saveProperty( "version.revision", String.valueOf( value ) );
    }
    
    public void setVersionTimestamp( int value ) {
        saveProperty( "version.timestamp", String.valueOf( value ) );
    }
    
    private void saveProperty( String name, String value ) {
        Properties prop = new Properties();
        try {
            prop.load( new FileInputStream( getVersionFile() ) );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        prop.setProperty( name, value );
        try {
            prop.store( new FileOutputStream( getVersionFile() ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
    
    private File getVersionFile() {
        String fs = System.getProperty( "file.separator" );
        return new File( project.getProjectDir(), "data" + fs + project.getName() + fs + project.getName() + ".properties" );
    }
}
