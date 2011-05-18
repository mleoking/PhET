package edu.colorado.phet.buildtools.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Author: Sam Reid
 * Jul 27, 2007, 12:54:47 PM
 */
public class IncrementMinorNumbers {
    public static void main( String[] args ) {
        File simRoot = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" );
        for ( int i = 0; i < simRoot.listFiles().length; i++ ) {
            File simDir = simRoot.listFiles()[i];
            String name = simDir.getName();
            File propertyFile = new File( simDir, "data/" + name + "/" + name + ".properties" );
            System.out.println( "propertyFile = " + propertyFile + ", exists=" + propertyFile.exists() );
            if ( propertyFile.exists() && !( propertyFile.getAbsolutePath().indexOf( "all-sims" ) >= 0 ) ) {
                updateVersionNumbers( name, propertyFile );
            }
        }
    }

    private static void updateVersionNumbers( String name, File propertyFile ) {
        try {
            String string = edu.colorado.phet.common.phetcommon.util.FileUtils.loadFileAsString( propertyFile );
            System.out.println( "string = \n" + string );
            Properties properties = new Properties();
            properties.load( new FileInputStream( propertyFile ) );
            String major = properties.getProperty( "version.major" );
            String minor = properties.getProperty( "version.minor" );
            String dev = properties.getProperty( "version.dev" );
            String revision = properties.getProperty( "version.revision" );
            String newMinorNumber = String.valueOf( Integer.parseInt( minor ) + 1 );
            if ( newMinorNumber.length() == 1 ) {
                newMinorNumber = "0" + newMinorNumber;
            }
            if ( major.equals( "0" ) ) {
                major = "1";
            }
            properties.setProperty( "version.revision", "17033" );
            properties.store( new FileOutputStream( propertyFile ), null );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
