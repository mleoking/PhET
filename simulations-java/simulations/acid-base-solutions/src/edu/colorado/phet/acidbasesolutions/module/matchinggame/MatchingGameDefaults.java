/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import edu.colorado.phet.acidbasesolutions.persistence.MatchingGameConfig;


/**
 * MatchingGameDefaults contains default settings for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameDefaults {

    private static MatchingGameDefaults INSTANCE = new MatchingGameDefaults();
    
    public static MatchingGameDefaults getInstance() {
        return INSTANCE;
    }
    
    private final MatchingGameConfig config;
    
    private MatchingGameDefaults() {
        config = new MatchingGameConfig();
        //XXX call config setters
    }
    
    public MatchingGameConfig getConfig() {
        return config;
    }
}
