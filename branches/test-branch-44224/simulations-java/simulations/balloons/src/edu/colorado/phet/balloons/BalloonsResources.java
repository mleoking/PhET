package edu.colorado.phet.balloons;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 18, 2007, 11:44:33 AM
 */
public class BalloonsResources {
    private static PhetResources INSTANCE = new PhetResources( "balloons" );

    public static String getString( String key ) {
        return INSTANCE.getLocalizedString( key );
    }

    public static BufferedImage getImage( String s ) {
        return INSTANCE.getImage( s );
    }
}
