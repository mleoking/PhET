// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * Apr 24, 2007, 4:54:35 AM
 */
public class RotationResources {
    private static PhetResources INSTANCE = new PhetResources( "rotation" );

    /* not intended for instantiation */
    private RotationResources() {
    }

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage loadBufferedImage( String url ) throws IOException {
        return INSTANCE.getImage( url );
    }
}
