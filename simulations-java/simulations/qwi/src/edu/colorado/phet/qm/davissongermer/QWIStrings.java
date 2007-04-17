package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Aug 21, 2006
 * Time: 1:16:01 PM
 * Copyright (c) Aug 21, 2006 by Sam Reid
 */

public class QWIStrings {
    private static String bundleName = "localization/QWIStrings";

    public static String getString( String s ) {
        return SimStrings.getInstance().getString( s );
    }

    public static void init( String[] args ) {
        SimStrings.getInstance().init( args, bundleName );
    }
}
