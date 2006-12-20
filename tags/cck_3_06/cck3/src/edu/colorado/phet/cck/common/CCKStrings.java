/* Copyright 2004, Sam Reid */
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Jun 24, 2006
 * Time: 10:11:33 PM
 * Copyright (c) Jun 24, 2006 by Sam Reid
 */

public class CCKStrings {
    public static String getString( String s ) {
        return SimStrings.get( s );
    }

    public static String toHTML( String key ) {
        String text = getString( key );
        return "<html>" + text.replaceAll( "\n", "<br>" ) + "</html>";
    }
}
