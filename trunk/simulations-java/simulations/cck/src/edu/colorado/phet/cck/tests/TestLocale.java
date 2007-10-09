package edu.colorado.phet.cck.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Oct 9, 2007
 * Time: 11:25:32 AM
 */
public class TestLocale {
    public static void main( String[] args ) throws IOException {
        File localizationFile = new File( "C:\\reid\\phet\\svn\\trunk\\simulations-java\\simulations\\cck\\data\\cck\\localization\\cck-strings_ja.properties" );
        Properties properties = new Properties();
        properties.load( new FileInputStream( localizationFile ) );
        Enumeration k = properties.keys();
        while ( k.hasMoreElements() ) {
            Object o = k.nextElement();
            Object value = properties.get( o );
            System.out.println( "key = " + o + ", value=" + value );
        }
    }
}
