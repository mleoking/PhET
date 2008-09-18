package edu.colorado.phet.batteryvoltage;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 18, 2007, 10:56:26 PM
 */
public class BatteryVoltageResources {
    private static final PhetResources INSTANCE = new PhetResources( "battery-voltage" );

    public static String getString( String key ) {
        return INSTANCE.getLocalizedString( key );
    }

    public static BufferedImage getImage( String name ) {
        return INSTANCE.getImage( name );
    }

    public static PhetResources getResourceLoader() {
        return INSTANCE;
    }
}
