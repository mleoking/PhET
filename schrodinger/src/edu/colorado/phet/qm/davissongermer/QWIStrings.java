package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.util.SimStrings;

import java.util.ResourceBundle;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 1:16:01 PM
 * Copyright (c) Aug 21, 2006 by Sam Reid
 */

public class QWIStrings {
    private static String bundleName = "localization/QWIStrings";

    public static String getString( String s ) {
        return SimStrings.get( s );
    }

    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle( bundleName, SimStrings.getLocalizedLocale() );
    }

    public static void init( String[] args ) {
        SimStrings.init( args, bundleName );
    }
}
