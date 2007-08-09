package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.torque.TorqueModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class TorqueGraphSet extends AbstractRotationGraphSet {

    public TorqueGraphSet( PhetPCanvas pSwingCanvas, final TorqueModel tm, AngleUnitModel angleUnitModel ) {
        super( pSwingCanvas, tm, angleUnitModel );

        RotationMinimizableControlGraph torqueGraph = new RotationMinimizableControlGraph( UnicodeUtil.TAU, new RotationGraph(
                pSwingCanvas, tm.getTorqueVariable(),
                UnicodeUtil.TAU, "torque", "units", -10, 10, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getTorqueDriven(), RotationModel.MAX_TIME, tm.getRotationPlatform() ) );
        torqueGraph.addSeries( "Torque", Color.blue, UnicodeUtil.TAU, "units", tm.getTorqueVariable(), tm.getTorqueTimeSeries(), new BasicStroke( 2 ) );

        RotationMinimizableControlGraph forceGraph = new RotationMinimizableControlGraph( "F", new RotationGraph(
                pSwingCanvas, tm.getForceVariable(),
                "F", "force", "units", -0.001 / 200.0, 0.001 / 200.0, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getForceDriven(), RotationModel.MAX_TIME, tm.getRotationPlatform() ) );
        forceGraph.addSeries( "Force", Color.blue, "force", "units", tm.getForceVariable(), tm.getForceTimeSeries(), new BasicStroke( 2 ) );

        RotationMinimizableControlGraph momentOfInertiaGraph = new RotationMinimizableControlGraph( "I", new RotationGraph(
                pSwingCanvas, tm.getMomentOfInertiaVariable(),
                "I", "Moment of Inertia", "units", -5, 5, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, false, tm.getTimeSeriesModel(), null, RotationModel.MAX_TIME, tm.getRotationPlatform() ) );
        momentOfInertiaGraph.addSeries( "Moment of Inertia", Color.green, "I", "kg*m^2", tm.getMomentOfInertiaVariable(), tm.getMomentOfInertiaTimeSeries(), new BasicStroke( 2 ) );

        RotationMinimizableControlGraph angularMomentumGraph = new RotationMinimizableControlGraph( "L", new RotationGraph(
                pSwingCanvas, tm.getAngularMomentumVariable(),
                "L", "Angular Momentum", "units", -0.1, 0.1, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, false, tm.getTimeSeriesModel(), null, RotationModel.MAX_TIME, tm.getRotationPlatform() ) );
        angularMomentumGraph.addSeries( "Angular Momentum", Color.red, "L", "units", tm.getAngularMomentumVariable(), tm.getAngularMomentumTimeSeries(), new BasicStroke( 2 ) );

        RotationMinimizableControlGraph angleGraph = createAngleGraph();
        RotationMinimizableControlGraph angVelGraph = createAngVelGraph();
        RotationMinimizableControlGraph angAccelGraph = createAngAccelGraph();

        addGraphSuite( new RotationMinimizableControlGraph[]{forceGraph, torqueGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{torqueGraph, angAccelGraph, angVelGraph, angleGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angVelGraph, momentOfInertiaGraph, angularMomentumGraph} );

        addSeriesSelectionPanels();
        updateBody1Series();
    }
}
