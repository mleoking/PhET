// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.graphs.AbstractTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;
import edu.colorado.phet.rotation.model.AngleUnitModel;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:55:17 PM
 */
public class AngMomGraphSet extends AbstractTorqueGraphSet {
    public AngMomGraphSet( AngMomSimulationPanel angMomSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( angMomSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createAngVelGraph(), createMomentGraph(), createAngMomGraph()} );
    }
}
