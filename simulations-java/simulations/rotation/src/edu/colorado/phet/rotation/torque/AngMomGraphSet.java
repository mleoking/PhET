package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.graphs.FullTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:55:17 PM
 */
public class AngMomGraphSet extends FullTorqueGraphSet {
    public AngMomGraphSet( AngMomSimulationPanel angMomSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( angMomSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createAngVelGraph(), createMomentGraph(), createAngMomGraph()} );
    }
}
