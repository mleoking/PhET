package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.torque.TorqueModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class TorqueGraphSet extends GraphSuiteSet {

    public TorqueGraphSet( PhetPCanvas pSwingCanvas, final TorqueModel tm ) {
        int maxDomainValue = 500;
        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new RotationGraph(
                pSwingCanvas, tm.getPlatformAngleVariable(), tm.getPlatformAngleTimeSeries(),
                UnicodeUtil.THETA, "Angular Position", "units", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getPositionDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new RotationGraph(
                pSwingCanvas, tm.getPlatformVelocityVariable(), tm.getPlatformVelocityTimeSeries(),
                UnicodeUtil.OMEGA, "Angular Velocity", "units", -0.1, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getVelocityDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new RotationGraph(
                pSwingCanvas, tm.getPlatformAccelVariable(), tm.getPlatformAccelTimeSeries(),
                UnicodeUtil.ALPHA, "Angular Acceleration", "units", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getAccelDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph torqueGraph = new MinimizableControlGraph( UnicodeUtil.TAU, new RotationGraph(
                pSwingCanvas, tm.getTorqueVariable(), tm.getTorqueTimeSeries(),
                UnicodeUtil.TAU, "torque", "units", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getTorqueDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph forceGraph = new MinimizableControlGraph( "F", new RotationGraph(
                pSwingCanvas, tm.getForceVariable(), tm.getForceTimeSeries(),
                "F", "force", "units", -0.001 / 200.0, 0.001 / 200.0, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getForceDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph momentOfInertiaGraph = new MinimizableControlGraph( "I", new RotationGraph(
                pSwingCanvas, tm.getMomentOfInertiaVariable(), tm.getMomentOfInertiaTimeSeries(),
                "I", "Moment of Inertia", "units", -5, 5, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, false, tm.getTimeSeriesModel(), null, maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph angularMomentumGraph = new MinimizableControlGraph( "L", new RotationGraph(
                pSwingCanvas, tm.getAngularMomentumVariable(), tm.getAngularMomentumTimeSeries(),
                "L", "Angular Momentum", "units", -0.1, 0.1, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, false, tm.getTimeSeriesModel(), null, maxDomainValue, tm.getRotationPlatform() ) );

        addGraphSuite( new MinimizableControlGraph[]{forceGraph, torqueGraph} );
        addGraphSuite( new MinimizableControlGraph[]{torqueGraph, accelGraph, velocityGraph, angleGraph} );
        addGraphSuite( new MinimizableControlGraph[]{velocityGraph, momentOfInertiaGraph, angularMomentumGraph} );

    }

    public static void main( String[] args ) {
        TorqueGraphSet rotationGraphSet = new TorqueGraphSet( new PhetPCanvas(), new TorqueModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
