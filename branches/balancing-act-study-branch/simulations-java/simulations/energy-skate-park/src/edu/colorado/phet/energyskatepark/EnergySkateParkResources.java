// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * User: Sam Reid
 * Date: Aug 20, 2006
 * Time: 7:00:50 PM
 */

public class EnergySkateParkResources {

    private static final PhetResources RESOURCE_LOADER = new PhetResources( "energy-skate-park" );

    public static String getString( String s ) {
        return RESOURCE_LOADER.getLocalizedString( s );
//        return "<t>"+RESOURCE_LOADER.getLocalizedString( s )+"</t>";
    }

    public static BufferedImage getImage( String resourceName ) {
        return RESOURCE_LOADER.getImage( resourceName );
    }
}
