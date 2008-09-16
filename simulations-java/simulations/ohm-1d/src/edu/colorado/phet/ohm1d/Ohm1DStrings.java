package edu.colorado.phet.ohm1d;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class Ohm1DStrings {
    private static PhetResources phetResources = new PhetResources( "ohm-1d" );

    public static String get( String s ) {
        return phetResources.getLocalizedString( s );
    }
}
