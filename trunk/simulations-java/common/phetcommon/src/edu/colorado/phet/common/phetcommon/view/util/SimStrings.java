/* Copyright 2004-2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import edu.colorado.phet.common.phetcommon.resources.DefaultResourceLoader;
import edu.colorado.phet.common.phetcommon.resources.DummyConstantStringTester;
import edu.colorado.phet.common.phetcommon.resources.PhetProperties;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * SimStrings
 * <p/>
 * Manages strings for simulations so that they can be localized.
 *
 * @deprecated use PhetResources or PhetApplicationConfig
 */
public class SimStrings {

    private Vector localizedStrings;
    private Vector bundleNames;
    private Locale locale;

    private static SimStrings INSTANCE = new SimStrings();

    /**
     * @deprecated
     */
    public static SimStrings getInstance() {
        return INSTANCE;
    }

    /* intended to be a singleton, use getInstance */
    private SimStrings() {
        locale= PhetResources.readLocale();
    }

    /**
     * Initialize application localization.
     *
     * @param args       the commandline arguments that were passed to main
     * @param bundleName the base name of the resource bundle containing localized strings
     */
    public void init( String[] args, String bundleName ) {
        // Initialize simulation strings using resource bundle for the locale.
        addStrings( bundleName );
    }

    // TODO: make this private after all simulation use init
    public void setLocale( Locale locale ) {
        this.locale = locale;
        // Reload all existing string resources with the new locale
        Vector priorPaths = this.bundleNames;
        this.bundleNames = null;
        this.localizedStrings = null;
        if ( priorPaths != null ) {
            for ( Iterator i = priorPaths.iterator(); i.hasNext(); ) {
                String path = (String) i.next();
                addStrings( path );
            }
        }
    }

    // TODO: make this private after all simulation use init
    public void addStrings( String bundleName ) {
        if ( this.localizedStrings == null ) {
            this.localizedStrings = new Vector();
            this.bundleNames = new Vector();
        }
        if ( this.bundleNames.contains( bundleName ) ) {
            return;
        }
        try {
            if ( this.locale == null ) {
                this.locale = Locale.getDefault();
            }
            PhetProperties properties = new DefaultResourceLoader().getProperties( bundleName, this.locale );
            if ( properties != null ) {
                this.localizedStrings.add( properties );
                this.bundleNames.add( bundleName );
            }
        }
        catch( Exception x ) {
            System.out.println( "SimStrings.addStrings: " + x );
        }
    }

    /**
     * Gets a string value from the localization resource file.
     * If key's value is null, then key is returned.
     *
     * @param key
     * @return String
     */
    public String getString( String key ) {
        if ( this.localizedStrings == null ) {
            throw new RuntimeException( "Strings not initialized" );
        }

        String value = null;

        for ( Iterator i = this.localizedStrings.iterator(); value == null && i.hasNext(); ) {
            try {
                PhetProperties rb = (PhetProperties) i.next();
                value = rb.getString( key );
            }
            catch( Exception x ) {
                value = null;
            }
        }

        if ( value == null ) {
            System.err.println( "SimStrings.get: key not found, key = \"" + key + "\"" );
            value = key;
        }

//            return value;
        return DummyConstantStringTester.getString( value );
    }

    /**
     * @param s
     * @return
     * @deprecated use getString()
     */
    public static String get( String s ) {
        return DummyConstantStringTester.getString( INSTANCE.getString( s ) );
    }

    /**
     * @deprecated use addStrings
     */
    public static void setStrings( String s ) {
        INSTANCE.addStrings( s );
    }


}
