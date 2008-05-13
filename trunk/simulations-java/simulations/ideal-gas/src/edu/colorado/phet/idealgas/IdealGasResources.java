package edu.colorado.phet.idealgas;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * May 18, 2007, 12:58:55 PM
 */
public class IdealGasResources {
    private static PhetResources INSTANCE = new PhetResources( "ideal-gas" );

    public static String getLocalizedString( String key ) {
        return INSTANCE.getLocalizedString( key );
    }

    public static BufferedImage getImage( String name ) {
        return INSTANCE.getImage( name );
    }

    public static String getString( String s ) {
        return getLocalizedString( s );
    }
}
