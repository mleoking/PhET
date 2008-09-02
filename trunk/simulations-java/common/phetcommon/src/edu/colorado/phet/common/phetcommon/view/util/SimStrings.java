/* Copyright 2004-2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.util;

import java.util.Enumeration;
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

    private final PhetProperties localizedStrings;
    private final Vector bundleNames;
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
        localizedStrings = new PhetProperties();
        bundleNames = new Vector();
        locale = PhetResources.readLocale();
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
        // Reload all string files with the new locale
        Vector priorPaths = new Vector( bundleNames );
        bundleNames.clear();
        localizedStrings.clear();
        if ( priorPaths != null ) {
            for ( Iterator i = priorPaths.iterator(); i.hasNext(); ) {
                String path = (String) i.next();
                addStrings( path );
            }
        }
    }

    // TODO: make this private after all simulation use init
    public void addStrings( String bundleName ) {
        
        // if we loaded this bundle previously, do nothing
        if ( bundleNames.contains( bundleName ) ) {
            return;
        }
        
        // load the strings, put them in localizedStrings
        try {
            PhetProperties properties = new DefaultResourceLoader().getProperties( bundleName, this.locale );
            if ( properties != null ) {
                Enumeration keys = properties.propertyNames();
                while ( keys.hasMoreElements() ) {
                    String key = (String) keys.nextElement();
                    String value = properties.getProperty( key );
                    localizedStrings.setProperty( key, value );
                }
                bundleNames.add( bundleName );
            }
            else {
                System.err.println( "WARNING: SimStrings.addStrings failed to load " + bundleName );
            }
        }
        catch( Exception e ) {
            System.out.println( "SimStrings.addStrings: " + e );
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
        if ( localizedStrings == null ) {
            throw new RuntimeException( "Strings not initialized" );
        }
        String value = localizedStrings.getString( key );
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
