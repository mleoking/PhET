package edu.colorado.phet.common.timeseries.ui;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 15, 2007, 8:12:31 PM
 */
public class TimeseriesResources {
    private static PhetResources resources = PhetCommonResources.getInstance();

    public static String getString( String s ) {
        return resources.getLocalizedString( s );
    }

    public static BufferedImage loadBufferedImage( String s ) {
        return resources.getImage( s );
    }
}
