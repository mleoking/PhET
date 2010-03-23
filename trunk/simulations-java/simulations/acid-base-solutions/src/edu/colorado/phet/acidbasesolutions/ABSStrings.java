/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ABSStrings {
    
    /* not intended for instantiation */
    private ABSStrings() {}
    
    private static final String getString( String key ) {
        return ABSResources.getString( key );
    }
    
    private static final String getCommonString( String key ) {
        return ABSResources.getCommonString( key );
    }
}
