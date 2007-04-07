/* Copyright 2004-2007, University of Colorado */

package edu.colorado.phet.common.view.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * SimStrings
 * <p/>
 * Manages strings for simulations so that they can be localized.
 *
 */
public class SimStrings extends AbstractPhetPropertySource {
    private Vector localizedStrings;
    private Vector stringsPaths;
    private Locale localizedLocale;

    private static SimStrings INSTANCE = new SimStrings();
    
    public static SimStrings getInstance() {
        return INSTANCE;
    }

    public void init( String bundleName ) {
        init(new String[0], bundleName);
    }

    /**
     * Initialize application localization.
     *
     * @param args       the commandline arguments that were passed to main
     * @param bundleName the base name of the resource bundle containing localized strings
     */
    public void init( String[] args, String bundleName ) {
        // Get the default locale from property javaws.locale.
        String applicationLocale = System.getProperty( "javaws.locale" );
        if( applicationLocale != null && !applicationLocale.equals( "" ) ) {
            setLocale( new Locale( applicationLocale ) );
        }

        // Override default locale using "user.language=" command line argument.
        String argsKey = "user.language=";
        for( int i = 0; i < args.length; i++ ) {
            if( args[i].startsWith( argsKey ) ) {
                String locale = args[i].substring( argsKey.length(), args[i].length() );
                setLocale( new Locale( locale ) );
                break;
            }
        }

        // Initialize simulation strings using resource bundle for the locale.
        addStrings( bundleName );
    }

    // TODO: make this private after all simulation use init
    public void setLocale( Locale locale ) {
        this.localizedLocale = locale;
        // Reload all existing string resources with the new locale
        Vector priorPaths = this.stringsPaths;
        this.stringsPaths = null;
        this.localizedStrings = null;
        if( priorPaths != null ) {
            for( Iterator i = priorPaths.iterator(); i.hasNext(); ) {
                String path = (String)i.next();
                addStrings( path );
            }
        }
    }

    // TODO: make this private after all simulation use init
    public void addStrings( String stringsPath ) {
        if( this.localizedStrings == null ) {
            this.localizedStrings = new Vector();
            this.stringsPaths = new Vector();
        }
        if( this.stringsPaths.contains( stringsPath ) ) {
            return;
        }
        try {
            if( this.localizedLocale == null ) {
                this.localizedLocale = Locale.getDefault();
            }
            ResourceBundle rb = ResourceBundle.getBundle( stringsPath, this.localizedLocale );
            if( rb != null ) {
                this.localizedStrings.add( rb );
                this.stringsPaths.add( stringsPath );
            }
        }
        catch( Exception x ) {
            System.out.println( "SimStrings.setStrings: " + x );
        }
    }

    /**
     * Gets a string value from the localization resource file.
     * If key's value is null, then key is returned.
     * @param key
     * @return String
     */
    public String getString( String key ) {
        if( this.localizedStrings == null ) {
            throw new RuntimeException( "Strings not initialized" );
        }

        String value = null;

        for( Iterator i = this.localizedStrings.iterator(); value == null && i.hasNext(); ) {
            try {
                ResourceBundle rb = (ResourceBundle)i.next();

                value = rb.getString( key );
            }
            catch( Exception x ) {
                value = null;
            }
        }

        if( value == null ) {
            System.err.println( "SimStrings.get: key not found, key = \"" + key + "\"" );
            value = key;
        }

        return value;
    }

}
