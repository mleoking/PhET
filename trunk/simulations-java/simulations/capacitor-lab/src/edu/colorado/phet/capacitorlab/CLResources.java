// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Wrapper around the PhET resource loader.
 * If we decide to use a different technique to load resources in the
 * future, all changes will be encapsulated here.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CLResources {

    private static final PhetResources RESOURCES = new PhetResources( CLConstants.PROJECT_NAME );

    /* not intended for instantiation */
    private CLResources() {
    }

    public static String getString( String name ) {
        return RESOURCES.getLocalizedString( name );
    }

    public static BufferedImage getBufferedImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
