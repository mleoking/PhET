package edu.colorado.phet.bernoulli;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * May 18, 2007, 11:12:58 PM
 */
public class BernoulliResources {
    private static final PhetResources INSTANCE = new PhetResources( "bernoulli" );

    public static BufferedImage getImage( String s ) {
        return INSTANCE.getImage( s );
    }

    public static String getString( String s ) {
        return INSTANCE.getLocalizedString( s );
    }
}
