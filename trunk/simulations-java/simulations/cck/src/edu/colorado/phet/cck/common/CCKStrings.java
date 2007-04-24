/*  */
package edu.colorado.phet.cck.common;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

/**
 * User: Sam Reid
 * Date: Jun 24, 2006
 * Time: 10:11:33 PM
 *
 */

public class CCKStrings {
    public static String getString( String s ) {
        return SimStrings.getInstance().getString( s );
    }

    public static String toHTML( String key ) {
        String text = getString( key );
        return "<html>" + text.replaceAll( "\n", "<br>" ) + "</html>";
    }
}
