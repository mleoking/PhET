package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;
import edu.colorado.phet.rotation.model.RotationModel;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public class TorqueModule extends AbstractRotationModule {
    protected RotationModel createModel( ConstantDtClock clock ) {
        return new TorqueModel( clock );
    }

    protected AbstractRotationSimulationPanel createSimulationPanel() {
        return new TorqueSimulationPanel( this );
    }
}
