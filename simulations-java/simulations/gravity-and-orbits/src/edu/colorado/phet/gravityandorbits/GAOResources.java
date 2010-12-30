/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * A wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the
 * future, all changes will be encapsulated here.
 */
public class GAOResources {

    private static final PhetResources RESOURCES = new PhetResources( GravityAndOrbitsApplication.PROJECT_NAME );

    /* not intended for instantiation */
    private GAOResources() {
    }

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
