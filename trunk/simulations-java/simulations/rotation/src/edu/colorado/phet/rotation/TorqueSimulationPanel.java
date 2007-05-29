package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.view.RotationPlayAreaNode;

/**
 * Author: Sam Reid
 * May 29, 2007, 1:02:17 AM
 */
public class TorqueSimulationPanel extends AbstractRotationSimulationPanel {
    public TorqueSimulationPanel( TorqueModule torqueModule ) {
        super( torqueModule );
    }

    protected RotationControlPanel createControlPanel() {
        return new RotationControlPanel( getRotationGraphSet(), getGraphSetModel(), new VectorViewModel() );
    }

    protected RotationPlayAreaNode createPlayAreaNode() {
        return new RotationPlayAreaNode( getRotationModel(), new VectorViewModel() );
    }
}
