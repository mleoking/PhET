/**
 * Class: SimStrings
 * Package: edu.colorado.phet.idealgas
 * Author: Another Guy
 * Date: Sep 24, 2004
 */
package edu.colorado.phet.common.view.util;

import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Vector;

public class SimStrings {

    private static Vector localizedStrings;
    private static Vector stringsPaths;

    public static void setStrings( String stringsPath ) {
        if( localizedStrings == null ) {
            localizedStrings = new Vector();
            stringsPaths = new Vector();
        }
        if ( stringsPaths.contains( stringsPath ) ) {
            System.out.println( "ignoring duplicate strings path: " + stringsPath );
            return;
        }
        try {
            System.out.println( "loading strings path: " + stringsPath );
            ResourceBundle rb = ResourceBundle.getBundle( stringsPath );
            if( rb != null ) {
                localizedStrings.add( rb );
                stringsPaths.add( stringsPath );
            }
        }
        catch( Exception x ) {
            System.out.println( "SimStrings.setStrings: " + x );
        }
    }

    public static String get( String key ) {
        if( localizedStrings == null ) {

            throw new RuntimeException( "Strings not initialized" );
        }

        String value = null;

        for( Iterator i = localizedStrings.iterator(); value == null && i.hasNext(); ) {
            try {
                ResourceBundle rb = (ResourceBundle)i.next();

                value = rb.getString( key );
            }
            catch( Exception x ) {
                value = null;
            }
        }

        return value;
    }
}
