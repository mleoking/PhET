package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.ISimulationVariable;
import edu.colorado.phet.common.motion.model.ITimeSeries;

import java.awt.*;

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

    public void addSeries2( String title, Color color, String abbr, ISimulationVariable simulationVariable, ITimeSeries timeSeries, Stroke stroke ) {
        controlGraph.addSecondarySeries( title, color, abbr, simulationVariable, timeSeries, stroke );
    }
}
