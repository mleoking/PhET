package edu.colorado.phet.build.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by: Sam
 * Aug 5, 2008 at 10:47:00 AM
 */
public class LicenseInfo {
    private String name;
    private String description;

    public LicenseInfo( File propertiesFile ) {
        Properties prop = new Properties();
        try {
            prop.load( new FileInputStream( propertiesFile ) );
            this.name = prop.getProperty( "name" );
            this.description = prop.getProperty( "description" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public String toString() {
        return name + ": " + description;
    }
}
