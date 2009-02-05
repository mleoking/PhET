package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LocalProperties {
    private Properties localProperties;

    public LocalProperties( File localPropertiesFile ) {
        localProperties = new Properties();
        if ( localPropertiesFile.exists() ) {
            try {
                localProperties.load( new FileInputStream( localPropertiesFile ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public String getProperty( String s ) {
        return localProperties.getProperty( s );
    }

    public boolean getBoolProperty( String s, boolean defaultValue ) {
        String s2 = localProperties.getProperty( s );
        if ( s2 == null ) {
            return defaultValue;
        }
        else if ( s2.toLowerCase().equals( "true" ) ) {
            return true;
        }
        else if ( s2.toLowerCase().equals( "false" ) ) {
            return false;
        }
        else {
            return defaultValue;
        }
    }
}