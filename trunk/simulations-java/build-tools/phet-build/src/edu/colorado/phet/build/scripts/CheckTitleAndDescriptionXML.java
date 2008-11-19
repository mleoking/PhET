package edu.colorado.phet.build.scripts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Author: Sam Reid
 * Jul 27, 2007, 12:54:47 PM
 */
public class CheckTitleAndDescriptionXML {
    public static void main( String[] args ) {
        File simRoot = new File( "C:\\phet\\subversion\\trunk\\simulations-java\\simulations" );
        for ( int i = 0; i < simRoot.listFiles().length; i++ ) {
            File simDir = simRoot.listFiles()[i];
            String name = simDir.getName();
            File localizationDir = new File( simDir, "data/" + name + "/localization" );
            if ( localizationDir.exists() ) {
                checkTitleAndDescriptionXML( name, localizationDir );
            }
        }
    }

    private static void checkTitleAndDescriptionXML( String name, File localizationDIR ) {
        File[] localizationFiles = localizationDIR.listFiles();
        for ( int i = 0; i < localizationFiles.length; i++ ) {
            File localizationFile = localizationFiles[i];
            if ( localizationFile.getName().startsWith( name ) && localizationFile.getName().endsWith( ".properties" ) ) {
                checkLocalizationFile( localizationFile );
            }
        }
    }

    private static void checkLocalizationFile( File localizationFile ) {
        Properties prop = new Properties();
        try {
            prop.load( new FileInputStream( localizationFile ) );
            Enumeration propNames = prop.propertyNames();
            while ( propNames.hasMoreElements() ) {
                String key = (String) propNames.nextElement();

                if ( !Character.isUpperCase( key.charAt( 0 ) ) && !key.startsWith( "expressions" ) && ( key.endsWith( ".name" ) || key.endsWith( "description" ) ) ) {
                    String value = prop.getProperty( key );
                    String escapedValue = StringEscapeUtils.escapeHtml( value );
                    if ( !value.equals( escapedValue ) ) {
                        System.out.println( "File=" + localizationFile.getName() + ", key = " + key + ", \nvalue=" + value + ", \nescape=" + escapedValue );
                        System.out.println( "" );
                    }
                }
            }
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

}
