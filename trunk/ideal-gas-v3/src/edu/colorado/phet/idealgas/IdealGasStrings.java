/**
 * Class: IdealGasStrings
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 24, 2004
 */
package edu.colorado.phet.idealgas;

import java.util.ResourceBundle;

public class IdealGasStrings {

    private static ResourceBundle localizedStrings;
    static {
        localizedStrings = ResourceBundle.getBundle( IdealGasConfig.localizedStringsPath );
    }

    public static String get( String key ) {
        return localizedStrings.getString( key );
    }
}
