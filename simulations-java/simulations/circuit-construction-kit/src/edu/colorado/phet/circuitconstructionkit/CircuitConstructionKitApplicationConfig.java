// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit;

import edu.colorado.phet.circuitconstructionkit.view.CCKPhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;

public class CircuitConstructionKitApplicationConfig extends PhetApplicationConfig {
    public CircuitConstructionKitApplicationConfig(String[] commandLineArgs, String project, String flavor) {
        super(commandLineArgs, project, flavor);
        setLookAndFeel(new CCKPhetLookAndFeel());
    }
}
