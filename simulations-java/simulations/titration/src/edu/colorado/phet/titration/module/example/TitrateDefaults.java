/* Copyright 2009, University of Colorado */

package edu.colorado.phet.titration.module.example;

import edu.colorado.phet.titration.persistence.TitrateConfig;

/**
 * Default settings for the "Titrate" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TitrateDefaults {

    private static TitrateDefaults INSTANCE = new TitrateDefaults();
    
    public static TitrateDefaults getInstance() {
        return INSTANCE;
    }
    
    private final TitrateConfig config; // default configuration

    /* singleton */
    private TitrateDefaults() {
        config = new TitrateConfig();
    }
    
    public TitrateConfig getConfig() {
        return config;
    }
}
