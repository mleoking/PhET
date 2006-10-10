package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Aug 20, 2006
 * Time: 7:00:50 PM
 * Copyright (c) Aug 20, 2006 by Sam Reid
 */

public class EnergySkateParkStrings {
    public static void init( String[] args, String bundlename ) {
        SimStrings.init( args, bundlename );
    }

    public static String getString( String s ) {
//        return "$" + SimStrings.get( s ) + "$";
        return SimStrings.get( s );
    }
}
