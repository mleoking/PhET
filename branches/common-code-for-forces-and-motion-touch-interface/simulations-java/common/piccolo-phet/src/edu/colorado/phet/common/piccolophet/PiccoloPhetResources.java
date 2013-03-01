// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Singleton that provides convenient access to piccolo-phet's JAR resources.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PiccoloPhetResources {

    /* singleton, not intended for instantiation */
    private PiccoloPhetResources() {
    }

    private static PhetResources INSTANCE = new PhetResources( "piccolo-phet" );

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage getImage( String name ) {
        return INSTANCE.getImage( name );
    }
}
