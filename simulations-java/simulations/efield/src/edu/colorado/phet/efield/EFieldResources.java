package edu.colorado.phet.efield;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class EFieldResources {
    private static PhetResources INSTANCE = new PhetResources( "efield" );

    public static String getString( String s ) {
        return INSTANCE.getLocalizedString( s );
    }
}
