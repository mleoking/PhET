package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * User: Sam Reid
 * Date: Aug 20, 2006
 * Time: 7:00:50 PM
 * Copyright (c) Aug 20, 2006 by Sam Reid
 */

public class EnergySkateParkStrings {

    private static final PhetResources RESOURCE_LOADER = PhetResources.forProject( "energy-skate-park" );

    public static String getString( String s ) {
        return RESOURCE_LOADER.getLocalizedString( s );
    }

}
