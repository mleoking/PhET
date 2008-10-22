package edu.colorado.phet.microwaves;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class MicrowavesResources {
    static PhetResources phetResources = new PhetResources( "microwaves" );

    public static String getString( String s ) {
        return phetResources.getLocalizedString( s );
    }
}
