/**
 * Class: SimStrings
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 24, 2004
 */
package edu.colorado.phet.common.view.util;

import java.util.ResourceBundle;

public class SimStrings {

    private static ResourceBundle localizedStrings;

    public static void setStrings( String stringsPath ) {
        localizedStrings = ResourceBundle.getBundle( stringsPath );
    }

    public static String get( String key ) {
        if( localizedStrings == null ) {
            throw new RuntimeException( "Strings not initialized" );
        }
        return localizedStrings.getString( key );
    }
}
