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
 * <p/>
 * Manages strings for simulations so that they can be localized. All methods are static.
 * <p/>
 * Call setStrings() for each resource bundle of strings that a simulation needs to access.
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

    /**
     * Initialize application localization.
     *
     * @param args       the commandline arguments that were passed to main
     * @param bundleName the base name of the resource bundle containing localized strings
     */
    public static void init( String[] args, String bundleName ) {
        // Get the default locale from property javaws.locale.
        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            SimStrings.setLocale( new Locale( applicationLocale ) );
        }

        // Override default locale using "user.language=" command line argument.
        String argsKey = "user.language=";
        for( int i = 0; i < args.length; i++ ) {
            if( args[i].startsWith( argsKey ) ) {
                String locale = args[i].substring( argsKey.length(), args[i].length() );
                SimStrings.setLocale( new Locale( locale ) );
                break;
            }
        }

        // Initialize simulation strings using resource bundle for the locale.
        SimStrings.setStrings( bundleName );
    }

    // TODO: make this private after all simulation use init
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

    // TODO: make this private after all simulation use init
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

    public static Locale getLocalizedLocale() {
        return localizedLocale;
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
