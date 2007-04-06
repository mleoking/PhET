package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.PhetProjectConfig;

/**
 * User: Sam Reid
 * Date: Aug 20, 2006
 * Time: 7:00:50 PM
 * Copyright (c) Aug 20, 2006 by Sam Reid
 */

public class EnergySkateParkStrings {

    private static PhetProjectConfig phetProjectConfig = PhetProjectConfig.forProject( "energy-skate-park" );

    public static String getString( String s ) {
        return phetProjectConfig.getString( s );
    }

}
