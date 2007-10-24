package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.graphs.FullTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:42:38 PM
 */
public class TorqueGraphSet extends FullTorqueGraphSet {
    public TorqueGraphSet( TorqueSimulationPanel torqueSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( torqueSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createForceGraph(), createRadiusGraph(), createTorqueGraph()} );
    }
}
