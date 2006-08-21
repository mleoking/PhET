package edu.colorado.phet.theramp;

import edu.colorado.phet.common.view.util.SimStrings;

import java.util.ResourceBundle;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 2:21:41 PM
 * Copyright (c) Aug 21, 2006 by Sam Reid
 */

public class TheRampStrings {
    private static String bundleName = "localization/TheRampStrings";

    public static void init( String[]args ) {
        SimStrings.init( args, bundleName );
    }

    public static String getString( String s ) {
        return SimStrings.get( s );
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle( bundleName, SimStrings.getLocalizedLocale() );
    }
}
