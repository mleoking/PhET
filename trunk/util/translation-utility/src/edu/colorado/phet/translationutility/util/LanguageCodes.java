/* Copyright 2007, University of Colorado */

package edu.colorado.phet.translationutility.util;

import java.util.TreeMap;

import edu.colorado.phet.translationutility.TUResources;

/**
 * LanguageCodes is a collection of language codes and their English names.
 * The language codes are read from a resource file, and include both 
 * ISO-standard codes and PhET-assigned codes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LanguageCodes {
    
    private static LanguageCodes _singleton;
    
    private TreeMap _codeMap;
    private TreeMap _nameMap;

    public static LanguageCodes getInstance() {
        if ( _singleton == null ) {
            _singleton = new LanguageCodes();
        }
        return _singleton;
    }
    
    private LanguageCodes() {
        _nameMap = new TreeMap();
        _codeMap = new TreeMap();
        loadCodes();
    }
    
    private void loadCodes() {
        
        // ISO-standard codes
        String[] isoCodes = TUResources.getISOLanguageCodes();
        for ( int i = 0; i < isoCodes.length; i++ ) {
            addEntry( isoCodes[i] );
        }
        
        // PhET-assigned codes
        String[] phetCodes = TUResources.getPhETLanguageCodes();
        for ( int i = 0; i < phetCodes.length; i++ ) {
            String code = phetCodes[i];
            if ( getName( code ) != null ) {
                System.err.println( "LanguageCodes.loadCodes: ignoring PhET-assigned language code " + code + ", it is already used by ISO standard" );
            }
            else {
                addEntry( code );
            }
        }
    }
    
    private void addEntry( String code ) {
        String name = TUResources.getLanguageName( code );
        if ( name == null ) {
            System.err.println( "LanguageCodes.addEntry: missing name for language code " + code );
        }
        else {
            _nameMap.put( name, code );
            _codeMap.put( code, name );
        }
    }
    
    public String getName( final String code ) {
        String name = null;
        Object o = _codeMap.get( code );
        if ( o != null ) {
            name = (String)o;
        }
        return name;
    }
    
    public String getCode( final String name ) {
        String code = null;
        Object o = _nameMap.get( name );
        if ( o != null ) {
            code = (String)o;
        }
        return code;
    }
    
    public String[] getSortedCodes() {
        Object[] keys = _codeMap.keySet().toArray();
        String[] codes = new String[ keys.length ];
        for ( int i = 0; i < keys.length; i++ ) {
            codes[i] = (String) keys[i];
        }
        return codes;
    }
    
    public String[] getSortedNames() {
        Object[] keys = _nameMap.keySet().toArray();
        String[] names = new String[ keys.length ];
        for ( int i = 0; i < keys.length; i++ ) {
            names[i] = (String) keys[i];
        }
        return names;
    }
}
