/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab;

/**
 * Collection of localized strings used by this simulations.
 * We load all strings statically so that we will be warned at startup time of any missing strings.
 * Otherwise we'd have to visit every part of the sim to test properly.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLStrings {
    
    /* not intended for instantiation */
    private CLStrings() {}
    
    private static final String getString( String key ) {
        return CLResources.getString( key );
    }
    
    private static final String getCommonString( String key ) {
        return CLResources.getCommonString( key );
    }
}
