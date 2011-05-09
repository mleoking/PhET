package com.pixelzoom.util;

import java.util.Locale;


public class PrintLocales {

    public static void main( String[] args ) {
        
        String[] languages = Locale.getISOLanguages();
        for ( int i = 0; i < languages.length; i++ ) {
            System.out.println( languages[i] );
        }
        
        String[] countries = Locale.getISOCountries();
        for ( int i = 0; i < countries.length; i++ ) {
            System.out.println( countries[i] );
        }
        
        Locale targetLocale = new Locale( "zh", "CN" );
        System.out.println( targetLocale.toString() + " " + Locale.getDefault().getDisplayName( targetLocale ) );
        System.out.println( targetLocale.toString() + " " + targetLocale.getDisplayName( targetLocale ) );
    }
}
