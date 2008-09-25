package edu.colorado.phet.conductivity;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Created by IntelliJ IDEA.
 * User: Sam
 * Date: Sep 25, 2008
 * Time: 9:20:14 AM
 */
public class ConductivityResources {
    private static PhetResources resources = new PhetResources( "conductivity" );

    public static String getString( String key ) {
        return resources.getLocalizedString( key );
    }
}
