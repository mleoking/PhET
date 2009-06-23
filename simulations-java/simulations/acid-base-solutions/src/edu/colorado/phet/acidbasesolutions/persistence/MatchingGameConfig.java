/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.persistence;


/**
 * MatchingGameConfig is a Java Bean compliant configuration of MatchingGameModule.
 * There is no persistent state that is specific to this module.
 * But we need a config to handle state that is common to all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameConfig extends AbstractModuleConfig {
    
    /**
     * Zero-argument constructor for Java Bean compliance.
     */
    public MatchingGameConfig() {}
}
