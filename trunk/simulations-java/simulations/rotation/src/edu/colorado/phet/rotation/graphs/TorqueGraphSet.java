package edu.colorado.phet.rotation.graphs;

import java.awt.*;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.JFreeChartSliderNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.torque.TorqueModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class TorqueGraphSet extends AbstractRotationGraphSet {

    public TorqueGraphSet( PhetPCanvas pSwingCanvas, final TorqueModel tm, AngleUnitModel angleUnitModel ) {
        super( pSwingCanvas, tm, angleUnitModel );

        final RotationMinimizableControlGraph forceGraph = new RotationMinimizableControlGraph( "F", new RotationGraph(
                pSwingCanvas, new ControlGraphSeries( "Force", Color.blue, "force", "units", new BasicStroke( 2 ), true, null, tm.getForceTimeSeries() ),
                "F", "force", "units", -2.5, 2.5,
                tm, true, tm.getTimeSeriesModel(), tm.getForceDriven(), RotationModel.MAX_TIME, tm.getRotationPlatform() ) );
        forceGraph.getControlGraph().addSliderListener( new JFreeChartSliderNode.Listener() {
            public void valueChanged() {
            }

            public void sliderThumbGrabbed() {
            }

            public void sliderDragged( double value ) {
                tm.setAppliedForce( new Line2D.Double( tm.getRotationPlatform().getCenter().getX(),
                                                       tm.getRotationPlatform().getCenter().getY() - tm.getRotationPlatform().getRadius(),
                                                       tm.getRotationPlatform().getCenter().getX() + forceGraph.getControlGraph().getModelValue(),
                                                       tm.getRotationPlatform().getCenter().getY() - tm.getRotationPlatform().getRadius() ) );
            }
        } );

        RotationMinimizableControlGraph radiusGraph = new RotationMinimizableControlGraph( "r", new RotationGraph(
                pSwingCanvas, new ControlGraphSeries( "Radius", Color.green, "r", "m", new BasicStroke( 2 ), true, null, tm.getRadiusSeries() ),
                "r", "Radius", "m", 0, RotationPlatform.MAX_RADIUS,
                tm, true, tm.getTimeSeriesModel(), tm.getForceDriven(), RotationModel.MAX_TIME, tm.getRotationPlatform() ) );

        RotationMinimizableControlGraph torqueGraph = new RotationMinimizableControlGraph( UnicodeUtil.TAU, new RotationGraph(
                pSwingCanvas, new ControlGraphSeries( "Torque", Color.blue, UnicodeUtil.TAU, "units", new BasicStroke( 2 ), null, tm.getTorqueTimeSeries() ),
                UnicodeUtil.TAU, "torque", "units", -10, 10,
                tm, false, tm.getTimeSeriesModel(), null, RotationModel.MAX_TIME, tm.getRotationPlatform() ) );

        RotationMinimizableControlGraph momentOfInertiaGraph = new RotationMinimizableControlGraph( "I", new RotationGraph(
                pSwingCanvas, new ControlGraphSeries( "Moment of Inertia", Color.green, "I", "kg*m^2", new BasicStroke( 2 ), null, tm.getMomentOfInertiaTimeSeries() ),
                "I", "Moment of Inertia", "units", -5, 5,
                tm, false, tm.getTimeSeriesModel(), null, RotationModel.MAX_TIME, tm.getRotationPlatform() ) );

        RotationMinimizableControlGraph angularMomentumGraph = new RotationMinimizableControlGraph( "L", new RotationGraph(
                pSwingCanvas, new ControlGraphSeries( "Angular Momentum", Color.red, "L", "units", new BasicStroke( 2 ), null, tm.getAngularMomentumTimeSeries() ),
                "L", "Angular Momentum", "units", -0.1, 0.1,
                tm, false, tm.getTimeSeriesModel(), null, RotationModel.MAX_TIME, tm.getRotationPlatform() ) );

        RotationMinimizableControlGraph angleGraph = createAngleGraph();
        RotationMinimizableControlGraph angVelGraph = createAngVelGraph();
        RotationMinimizableControlGraph angAccelGraph = createAngAccelGraph();

        addGraphSuite( new RotationMinimizableControlGraph[]{forceGraph, radiusGraph, torqueGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{torqueGraph, angAccelGraph, angVelGraph, angleGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angVelGraph, momentOfInertiaGraph, angularMomentumGraph} );

        addSeriesSelectionPanels();
        updateBody1Series();
    }
}
