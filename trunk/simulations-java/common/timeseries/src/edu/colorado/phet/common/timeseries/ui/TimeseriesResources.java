package edu.colorado.phet.common.timeseries.ui;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 15, 2007, 8:12:31 PM
 */
public class TimeseriesResources {
    private static final PhetResources instance = PhetResources.forProject( "timeseries" );

    public static PhetResources getInstance() {
        return instance;
    }

    public static String getString( String s ) {
        return instance.getLocalizedString( s );
    }

    public static BufferedImage loadBufferedImage( String s ) {
        return instance.getImage( s );
    }
}
