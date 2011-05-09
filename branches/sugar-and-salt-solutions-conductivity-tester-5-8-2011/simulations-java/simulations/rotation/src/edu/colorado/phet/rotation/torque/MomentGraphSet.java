// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.torque;

import edu.colorado.phet.rotation.graphs.AbstractTorqueGraphSet;
import edu.colorado.phet.rotation.graphs.RotationMinimizableControlGraph;
import edu.colorado.phet.rotation.model.AngleUnitModel;

/**
 * Created by: Sam
 * Oct 24, 2007 at 1:38:53 PM
 */
public class MomentGraphSet extends AbstractTorqueGraphSet {
    public MomentGraphSet( MomentOfInertiaSimulationPanel momentOfInertiaSimulationPanel, TorqueModel torqueModel, AngleUnitModel angleUnitModel ) {
        super( momentOfInertiaSimulationPanel, torqueModel, angleUnitModel );
        addGraphSuite( new RotationMinimizableControlGraph[]{createTorqueGraph(), createMomentGraph(), createAngAccelGraph( false )} );
    }

}
