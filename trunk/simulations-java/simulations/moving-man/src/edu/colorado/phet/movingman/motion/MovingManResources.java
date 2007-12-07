package edu.colorado.phet.movingman.motion;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * Apr 24, 2007, 4:54:35 AM
 */
public class MovingManResources {
    private static PhetResources INSTANCE = PhetResources.forProject( "moving-man" );

    /* not intended for instantiation */
    private MovingManResources() {
    }

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage loadBufferedImage( String url ) throws IOException {
        return INSTANCE.getImage( url );
    }

    public static String getString( String s ) {
        return INSTANCE.getLocalizedString( s );
    }
}
