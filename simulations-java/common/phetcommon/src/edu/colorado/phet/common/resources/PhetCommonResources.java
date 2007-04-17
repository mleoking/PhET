/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.resources;

/**
 * PhetCommonResources is a singleton that provides access to phetcommon's JAR resources.
 */
public class PhetCommonResources {
    
    private static PhetResources INSTANCE = PhetResources.forProject( "phetcommon" );

    /* not intended for instantiation */
    private PhetCommonResources() {}
    
    public static PhetResources getInstance() {
        return INSTANCE;
    }
}
