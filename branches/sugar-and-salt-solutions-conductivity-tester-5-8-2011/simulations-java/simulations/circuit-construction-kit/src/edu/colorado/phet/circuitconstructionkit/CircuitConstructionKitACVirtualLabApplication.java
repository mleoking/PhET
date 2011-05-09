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
public class CircuitConstructionKitACVirtualLabApplication extends CircuitConstructionKitApplication {
    public CircuitConstructionKitACVirtualLabApplication(PhetApplicationConfig config) {
        super(config, true, true);
    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(new CircuitConstructionKitApplicationConfig(args, "circuit-construction-kit", "circuit-construction-kit-ac-virtual-lab"), new ApplicationConstructor() {
            public PhetApplication getApplication(PhetApplicationConfig config) {
                return new CircuitConstructionKitACVirtualLabApplication(config);
            }
        });
    }
}