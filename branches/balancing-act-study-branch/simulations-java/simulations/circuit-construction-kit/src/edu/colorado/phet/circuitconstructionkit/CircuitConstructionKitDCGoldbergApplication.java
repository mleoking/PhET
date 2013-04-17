// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;

/**
 * For Fred Goldberg, provide a version that uses default battery internal resistance as 1.0 Ohms, in July 2012
 * Will probably be removed soon
 */
public class CircuitConstructionKitDCGoldbergApplication extends CircuitConstructionKitApplication {
    public static boolean GOLDBERG = false;

    public CircuitConstructionKitDCGoldbergApplication( PhetApplicationConfig config ) {
        super( config, false, false );
    }

    public static void main( String[] args ) {
        GOLDBERG = true;
        new PhetApplicationLauncher().launchSim( new CircuitConstructionKitApplicationConfig( args, "circuit-construction-kit", "circuit-construction-kit-dc" ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new CircuitConstructionKitDCGoldbergApplication( config );
            }
        } );
    }
}