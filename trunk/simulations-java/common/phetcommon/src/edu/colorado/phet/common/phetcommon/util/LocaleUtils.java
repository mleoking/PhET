package edu.colorado.phet.common.phetcommon.util;

import java.util.Locale;
import java.util.StringTokenizer;


public class LocaleUtils {

    /* not intended for instantiation */
    private LocaleUtils() {
    }

    /**
     * Returns strings like "_ja" or "_en_CA" or "" for English.
     *
     * @param locale
     * @return
     */
    public static String getTranslationFileSuffix( Locale locale ) {
        assert locale != null;
        if ( locale.equals( new Locale( "en" ) ) ) {
            return "";
        }
        else {
            return "_" + localeToString( locale );
        }
    }

    /**
     * Gets the suffix for a localization string file.
     *
     * @param locale
     * @return
     */
    public static String localeToString( Locale locale ) {
        assert ( locale != null );
        // tempting to use locale.toString here, but don't do it.
        if ( locale.getCountry().equals( "" ) ) {
            return locale.getLanguage();
        }
        else {
            return locale.getLanguage() + "_" + locale.getCountry();
        }
    }

    //returns a Locale given a string like en_CA or ja
    //TODO: throw exception when localeString has incorrect form, such as ____en____CA__
    public static Locale stringToLocale( String localeString ) {
        assert localeString != null;
        StringTokenizer stringTokenizer = new StringTokenizer( localeString, "_" );
        if ( stringTokenizer.countTokens() == 1 ) {
            return new Locale( stringTokenizer.nextToken() );
        }
        else if ( stringTokenizer.countTokens() == 2 ) {
            return new Locale( stringTokenizer.nextToken(), stringTokenizer.nextToken() );
        }
        else {
            throw new RuntimeException( "Locale string should have language OR language_COUNTRY" );
        }
    }

    // tests
    public static void main( String[] args ) {

        // getJNLPSuffix
        System.out.println( "\"" + localeToString( new Locale( "" ) ) + "\"" );
        System.out.println( "\"" + localeToString( new Locale( "en" ) ) + "\"" );
        System.out.println( "\"" + localeToString( new Locale( "zh" ) ) + "\"" );
        System.out.println( "\"" + localeToString( new Locale( "zh", "CN" ) ) + "\"" );

        // suffixToLocale
        System.out.println( "\"" + stringToLocale( "CN" ) + "\"" );
        System.out.println( "\"" + stringToLocale( "en" ) + "\"" );
        System.out.println( "\"" + stringToLocale( "zh_CN" ) + "\"" );
    }
}
