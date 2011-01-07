// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.advancedacidbasesolutions.module.matchinggame;

import edu.colorado.phet.advancedacidbasesolutions.persistence.MatchingGameConfig;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * MatchingGameDefaults contains default settings for MatchingGameModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameDefaults {
    
    // default sizes
    public static final PDimension BEAKER_SIZE = new PDimension( 360, 400 );
    public static final PDimension CONCENTRATION_GRAPH_OUTLINE_SIZE = new PDimension( 320, 350 );

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
