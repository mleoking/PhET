// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * A wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BCEResources {

    private static final PhetResources RESOURCES = new PhetResources( BCEConstants.PROJECT_NAME );

    /* not intended for instantiation */
    private BCEResources() {}

    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }

    public static final BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
