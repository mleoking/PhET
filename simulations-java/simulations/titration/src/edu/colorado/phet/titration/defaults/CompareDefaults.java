/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.defaults;

import edu.colorado.phet.titration.persistence.CompareConfig;

/**
 * Default settings for the "Compare Titrations" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CompareDefaults {

    private static CompareDefaults INSTANCE = new CompareDefaults();
    
    public static CompareDefaults getInstance() {
        return INSTANCE;
    }
    
    private final CompareConfig config; // default configuration

    /* singleton */
    private CompareDefaults() {
        config = new CompareConfig();
    }
    
    public CompareConfig getConfig() {
        return config;
    }
}
