// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.graphs.AbstractTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;
import edu.colorado.phet.rotation.model.AngleUnitModel;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:42:38 PM
 */
public class TorqueGraphSet extends AbstractTorqueGraphSet {
    public TorqueGraphSet( TorqueSimulationPanel torqueSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( torqueSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createForceGraph(), createRadiusGraph(), createTorqueGraph()} );
    }
}
