// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;

/**
 * Author: Sam Reid
 * Apr 12, 2007, 11:48:01 PM
 */
public class CircuitConstructionKitDCVirtualLabApplication extends CircuitConstructionKitApplication {
    public CircuitConstructionKitDCVirtualLabApplication( PhetApplicationConfig config ) {
        super( config, false, true );
    }

    public static void main( String[] args ) {
        CircuitConstructionKitDCApplication.args = args;
        new PhetApplicationLauncher().launchSim( new CircuitConstructionKitApplicationConfig( args, "circuit-construction-kit", "circuit-construction-kit-dc-virtual-lab" ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new CircuitConstructionKitDCVirtualLabApplication( config );
            }
        } );
    }
}