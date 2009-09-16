/* Copyright 2007-2009, University of Colorado */

package edu.colorado.phet.translationutility;

import java.util.*;
import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;


/**
 * TULocales is a collection of locale codes and their English names.
 * This information is read from a resource file.
 * Locales are based on ISO-standard language and country codes.
 *
 * Updated to be used by the Wicket-based translation area
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TULocales implements Serializable {

    private static final String PROPERTIES_RESOURCE_NAME = "locales.properties";
    private static final String LOCALES_SEPARATOR = ",";

    private static TULocales _singleton;

    private HashMap _localeToNameMap; // Locale -> String
    private HashMap _nameToLocaleMap; // String -> Locale

    /* singleton */
    public static TULocales getInstance() {
        if ( _singleton == null ) {
            _singleton = new TULocales();
        }
        return _singleton;
    }

    // modified to accept a properties file from another location
    // this makes it so we don't have to add a J2EE dependency to translation utility
    public static TULocales getInstance( Properties p ) {
        if ( _singleton == null ) {
            _singleton = new TULocales( p );
        }
        return _singleton;
    }

    /* singleton */
    private TULocales() {
        _nameToLocaleMap = new HashMap();
        _localeToNameMap = new HashMap();
        loadCodes( TUResources.getProperties( PROPERTIES_RESOURCE_NAME ) );
    }

    private TULocales( Properties p ) {
        _nameToLocaleMap = new HashMap();
        _localeToNameMap = new HashMap();
        loadCodes( p );
    }

    private void loadCodes( Properties p ) {
        // get the set of locale codes
        String[] isoCodes = p.getProperty( "locales" ).split( LOCALES_SEPARATOR );
        // build mappings between locales and display names
        for ( int i = 0; i < isoCodes.length; i++ ) {
            Locale locale = LocaleUtils.stringToLocale( isoCodes[i] );
            if ( getName( locale ) != null ) {
                System.err.println( getClass().getName() + ": ignoring duplicate locale=" + locale );
            }
            else {
                String name = p.getProperty( "name." + locale ); // eg, name.zh_CN
                if ( name == null ) {
                    System.err.println( getClass().getName() + ": missing display name for locale=" + locale );
                }
                else {
                    addEntry( locale, name );
                }
            }
        }
    }

    private void addEntry( Locale locale, String name ) {
        _localeToNameMap.put( locale, name );
        _nameToLocaleMap.put( name, locale );
    }

    public String getName( Locale locale ) {
        String name = null;
        Object o = _localeToNameMap.get( locale );
        if ( o != null ) {
            name = (String) o;
        }
        return name;
    }

    public Locale getLocale( String name ) {
        Locale locale = null;
        Object o = _nameToLocaleMap.get( name );
        if ( o != null ) {
            locale = (Locale) o;
        }
        return locale;
    }

    public String[] getSortedNames() {
        List list = new ArrayList( _nameToLocaleMap.keySet() );
        Collections.sort( list );
        return (String[]) list.toArray( new String[list.size()] );
    }
}
