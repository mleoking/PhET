/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.defaults;

import edu.colorado.phet.titration.persistence.AdvancedConfig;

/**
 * Default settings for the "Advanced Titration" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedDefaults {
    
    private static AdvancedDefaults INSTANCE = new AdvancedDefaults();
    
    public static AdvancedDefaults getInstance() {
        return INSTANCE;
    }
    
    private final AdvancedConfig config; // default configuration

    /* singleton */
    private AdvancedDefaults() {
        config = new AdvancedConfig();
    }
    
    public AdvancedConfig getConfig() {
        return config;
    }
}
