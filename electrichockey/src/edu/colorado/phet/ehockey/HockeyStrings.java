package edu.colorado.phet.ehockey;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class HockeyStrings {

    private static ResourceBundle localizedStrings = null;
    private static Locale         currentLocale    = null;
    private static String         currentLanguage  = null;
    private static String         currentCountry   = null;


    public String toString() {

        return "Hockey Locale:"
            + "  Language=" + (( currentLanguage == null ) ? "NotSet" : currentLanguage)
            + "  Country="  + (( currentCountry  == null ) ? "NotSet" : currentCountry);
    }


    public static Enumeration getKeys() {

        if ( localizedStrings == null ) {
            throw new RuntimeException( "localizedStrings not initialized for getKeys()" );
        }

        return localizedStrings.getKeys();
    }


    public static void setLocale( String language, String country ) {

        if ( currentLocale != null ) {
            throw new RuntimeException( "currentLocale already initialized" );
        }

        currentLanguage = language;
        currentCountry  = country;

        currentLocale = new Locale( language, country );
    }


    public static void setStrings( String stringsPath ) {

        if ( currentLocale == null ) {
            throw new RuntimeException( "currentLocale not initialized" );
        }

        try {
            localizedStrings = ResourceBundle.getBundle( stringsPath, currentLocale );
        }
        catch ( java.util.MissingResourceException e ) {

            throw new RuntimeException( "localizedStrings resource bundle not found" );
        }
    }


    public static String get( String key ) {

        if ( localizedStrings == null ) {
            throw new RuntimeException( "localizedStrings not initialized for get()" );
        }

        return localizedStrings.getString( key );
    }

} // end of public class HockeyStrings
