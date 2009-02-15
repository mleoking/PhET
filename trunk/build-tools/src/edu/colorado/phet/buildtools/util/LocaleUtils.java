package edu.colorado.phet.buildtools.util;

import java.util.Locale;


public class LocaleUtils {

    /* not intended for instantiation */
    private LocaleUtils() {
    }

    /**
     * Gets the suffix for a localization string file.
     *
     * @param locale
     * @return
     */
    public static String getStringsSuffix( Locale locale ) {
        assert ( locale != null );
        String suffix = null;
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if ( language.equals( "" ) ) {
            suffix = "";
        }
        else if ( language.equals( "en" ) && country.equals( "" ) ) {
            suffix = "";
        }
        else {
            // tempting to use locale.toString here, but don't do it.
            if ( country.equals( "" ) ) {
                suffix = "_" + language;
            }
            else {
                suffix = "_" + language + "_" + country;
            }
        }
        return suffix;
    }

    /**
     * Gets the suffix for a JNLP file.
     *
     * @param locale
     * @return
     */
    public static String getJNLPSuffix( Locale locale ) {
        return getStringsSuffix( locale );
    }

    /**
     * Converts a file suffix to a Locale.
     *
     * @param suffix
     * @return
     */
    public static Locale suffixToLocale( String suffix ) {
        String language = "";
        String country = "";
        if ( suffix != null ) {
            int languageIndexSeparator = suffix.indexOf( '_' );
            if ( languageIndexSeparator != -1 ) {
                int countryIndexSeparator = suffix.indexOf( '_', languageIndexSeparator );
                if ( countryIndexSeparator != -1 ) {
                    language = suffix.substring( languageIndexSeparator + 1 );
                }
                else {
                    language = suffix.substring( languageIndexSeparator + 1, countryIndexSeparator );
                    country = suffix.substring( countryIndexSeparator + 1 );
                }
            }
        }
        return new Locale( language, country );
    }

    // tests
    public static void main( String[] args ) {

        // getJNLPSuffix
        System.out.println( "\"" + getJNLPSuffix( new Locale( "" ) ) + "\"" );
        System.out.println( "\"" + getJNLPSuffix( new Locale( "en" ) ) + "\"" );
        System.out.println( "\"" + getJNLPSuffix( new Locale( "zh" ) ) + "\"" );
        System.out.println( "\"" + getJNLPSuffix( new Locale( "zh", "CN" ) ) + "\"" );

        // suffixToLocale
        System.out.println( "\"" + suffixToLocale( null ) + "\"" );
        System.out.println( "\"" + suffixToLocale( "" ) + "\"" );
        System.out.println( "\"" + suffixToLocale( "_" ) + "\"" );
        System.out.println( "\"" + suffixToLocale( "_CN" ) + "\"" );
        System.out.println( "\"" + suffixToLocale( "_en" ) + "\"" );
        System.out.println( "\"" + suffixToLocale( "_zh_CN" ) + "\"" );
    }
}
