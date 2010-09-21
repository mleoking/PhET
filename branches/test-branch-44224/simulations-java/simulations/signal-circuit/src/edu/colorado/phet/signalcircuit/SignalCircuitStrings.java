package edu.colorado.phet.signalcircuit;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class SignalCircuitStrings {
    public static final PhetResources INSTANCE = new PhetResources("signal-circuit");

    public static String getString( String s ) {
        return INSTANCE.getLocalizedString( s );
    }
}
