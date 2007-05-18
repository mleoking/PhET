package edu.colorado.phet.balloons;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

import java.awt.image.BufferedImage;

/**
 * Author: Sam Reid
 * May 18, 2007, 11:44:33 AM 
 */
public class BalloonsResources {
    private static PhetResources INSTANCE = PhetResources.forProject( "balloons" );

    public static String getString( String key ) {
        return INSTANCE.getLocalizedString( key );
    }

    public static String getDescription() {
        return INSTANCE.getProjectName();
    }

    public static PhetResources getResourceLoader() {
        return INSTANCE;
    }

    public static BufferedImage getImage( String s ) {
        return INSTANCE.getImage( s );
    }
}
