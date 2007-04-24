package edu.colorado.phet.theramp;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 2:21:41 PM
 *
 */

public class TheRampStrings {     
    private static String bundleName = "localization/TheRampStrings";

    public static void init( String[]args ) {
        SimStrings.getInstance().init( args, bundleName );
    }

    public static String getString( String s ) {
        return SimStrings.getInstance().getString( s );
    }

}
