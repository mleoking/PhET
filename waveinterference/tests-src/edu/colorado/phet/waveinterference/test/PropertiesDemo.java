package edu.colorado.phet.waveinterference.test;/*
 * Copyright (c) 1995-1998 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 */

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

public class PropertiesDemo {

    static void displayValue( Locale currentLocale, String key ) {

        System.out.println( "HELLO" );
        ResourceBundle labels =
                ResourceBundle.getBundle( "LabelsBundle", currentLocale );
        String value = labels.getString( key );
        System.out.println(
                "Locale = " + currentLocale.toString() + ", " +
                "key = " + key + ", " +
                "value = " + value );

    } // displayValue


    static void iterateKeys( Locale currentLocale ) {

        ResourceBundle labels =
                ResourceBundle.getBundle( "LabelsBundle", currentLocale );

        Enumeration bundleKeys = labels.getKeys();

        while( bundleKeys.hasMoreElements() ) {
            String key = (String)bundleKeys.nextElement();
            String value = labels.getString( key );
            System.out.println( "key = " + key + ", " +
                                "value = " + value );
        }

    } // iterateKeys


    static public void main( String[] args ) {

        Locale[] supportedLocales = {
                Locale.FRENCH,
                Locale.GERMAN,
                Locale.ENGLISH
        };

        for( int i = 0; i < supportedLocales.length; i ++ ) {
            displayValue( supportedLocales[i], "s2" );
        }

        System.out.println();

        iterateKeys( supportedLocales[0] );

    } // main

} // class
