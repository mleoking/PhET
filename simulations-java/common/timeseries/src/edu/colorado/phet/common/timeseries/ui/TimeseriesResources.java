package edu.colorado.phet.common.timeseries.ui;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 15, 2007, 8:12:31 PM
 */
public class TimeseriesResources {
    private static PhetResources commonresources = PhetCommonResources.getInstance();
    private static PhetResources timeseriesresources = new PhetResources( "timeseries" );

    public static String getString( String s ) {
        return commonresources.getLocalizedString( "Common." + s );
    }

    public static BufferedImage loadBufferedImage( String s ) {
        return timeseriesresources.getImage( s );
    }
}
