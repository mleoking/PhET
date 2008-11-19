package edu.colorado.phet.build.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class LocalProperties {
    private Properties localProperties;

    public LocalProperties( File baseDir ) {
        File localPropertiesFile = new File( baseDir, "build-local.properties" );
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
}
