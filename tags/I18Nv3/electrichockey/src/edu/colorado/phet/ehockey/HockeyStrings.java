package edu.colorado.phet.ehockey;

import java.util.Locale;
import java.util.ResourceBundle;

public class HockeyStrings {

    private static ResourceBundle localizedStrings = null;

    public static void setStrings( String stringsPath ) {

        try {
            localizedStrings = ResourceBundle.getBundle( stringsPath );
        }
        catch ( java.util.MissingResourceException e ) {

            throw new RuntimeException( "localizedStrings resource bundle not found" );
        }
    }


    public static String get( String key ) {

        if ( localizedStrings == null ) {

            throw new RuntimeException( "Strings not initialized" );
        }

        return localizedStrings.getString( key );
    }

} // end of public class HockeyStrings
