/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.polyprotic;

import edu.colorado.phet.titration.persistence.PolyproticConfig;

/**
 * Default settings for the "Polyprotic Acids" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PolyproticDefaults {

    private static PolyproticDefaults INSTANCE = new PolyproticDefaults();
    
    public static PolyproticDefaults getInstance() {
        return INSTANCE;
    }
    
    private final PolyproticConfig config; // default configuration

    /* singleton */
    private PolyproticDefaults() {
        config = new PolyproticConfig();
    }
    
    public PolyproticConfig getConfig() {
        return config;
    }
}
