/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * SimStrings
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimStrings {

    private static Vector localizedStrings;
    private static Vector stringsPaths;
    private static Locale localizedLocale;

    static {
        SimStrings.setStrings( "localization/CommonStrings" );
    }

    public static void setLocale( Locale locale ) {
        localizedLocale = locale;
        // Reload all existing string resources with the new locale
        Vector priorPaths = stringsPaths;
        stringsPaths = null;
        localizedStrings = null;
        if( priorPaths != null ) {
            for( Iterator i = priorPaths.iterator(); i.hasNext(); ) {
                String path = (String)i.next();
                setStrings( path );
            }
        }
    }

    public static void setStrings( String stringsPath ) {
        if( localizedStrings == null ) {
            localizedStrings = new Vector();
            stringsPaths = new Vector();
        }
        if( stringsPaths.contains( stringsPath ) ) {
            return;
        }
        try {
            if( localizedLocale == null ) {
                localizedLocale = Locale.getDefault();
            }
            ResourceBundle rb = ResourceBundle.getBundle( stringsPath, localizedLocale );
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

        if( value == null ) {
            System.err.println( "SimStrings: key not found, key = \"" + key + "\"" );
            value = key;
        }

        return value;
    }
}
