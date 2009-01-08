/*  */
package edu.colorado.phet.waveinterference.util;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;


/**
 * User: Sam Reid
 * Date: May 22, 2006
 * Time: 2:21:30 PM
 */

public class WIStrings {
    private static PhetResources phetResources = new PhetResources( "wave-interference" );

    public static String getString( String s ) {
        return phetResources.getLocalizedString( s );
    }
}
