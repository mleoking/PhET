package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class SignalCircuitStrings {
    private static final PhetResources phetResources = new PhetResources("signal-circuit");

    public static String getString( String s ) {
        return phetResources.getLocalizedString( s );
    }
}
