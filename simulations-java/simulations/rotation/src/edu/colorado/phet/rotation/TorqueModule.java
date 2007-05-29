package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.AbstractRotationSimulationPanel;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:57:07 AM
 */
public class TorqueModule extends AbstractRotationModule {
    protected RotationModel createModel() {
        return new TorqueModel();
    }

    protected AbstractRotationSimulationPanel createSimulationPanel() {
        return new TorqueSimulationPanel( this );
    }
}
