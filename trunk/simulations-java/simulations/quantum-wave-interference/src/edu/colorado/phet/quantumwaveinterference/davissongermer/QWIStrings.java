package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 1:16:01 PM
 */

public class QWIStrings {
    private static String bundleName = "quantum-wave-interference/localization/quantum-wave-interference-strings";

    public static String getString( String s ) {
        return SimStrings.getInstance().getString( s );
    }

    public static void init( String[] args ) {
        SimStrings.getInstance().init( args, bundleName );
    }
}
