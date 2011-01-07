// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.rotation.model.RotationBody;

/**
 * Author: Sam Reid
 * Jul 13, 2007, 6:08:14 PM
 */
public class RotationMinimizableControlGraph extends MinimizableControlGraph {
    private RotationGraph controlGraph;

    public RotationMinimizableControlGraph( String label, RotationGraph controlGraph ) {
        super( label, controlGraph );
        this.controlGraph = controlGraph;
    }

    public void addSeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody bodyA, RotationBody bodyB ) {
        addSeriesPair( name, a, b, bodyA, bodyB, true );
    }

    public void addSeriesPair( String name, ControlGraphSeries a, ControlGraphSeries b, RotationBody bodyA, RotationBody bodyB, boolean visible ) {
        RotationGraph.SeriesPair pair = controlGraph.addSeriesPair( name, a, b, bodyA, bodyB );
        pair.setVisible( visible );
//        return pair;
    }

    public RotationGraph getRotationControlGraph() {
        return controlGraph;
    }

}
