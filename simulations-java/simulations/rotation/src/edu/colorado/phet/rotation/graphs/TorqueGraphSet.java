package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.colorado.phet.rotation.torque.TorqueModel;
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
        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new MotionControlGraph(
                pSwingCanvas, tm.getPlatformAngleVariable(), tm.getPlatformAngleTimeSeries(),
                UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getPositionDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new MotionControlGraph(
                pSwingCanvas, tm.getPlatformVelocityVariable(), tm.getPlatformVelocityTimeSeries(),
                UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getVelocityDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new MotionControlGraph(
                pSwingCanvas, tm.getPlatformAccelVariable(), tm.getPlatformAccelTimeSeries(),
                UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getAccelDriven(), maxDomainValue, tm.getRotationPlatform() ) );

        MinimizableControlGraph torqueGraph = new MinimizableControlGraph( UnicodeUtil.TAU, new MotionControlGraph(
                pSwingCanvas, tm.getTorqueVariable(), tm.getTorqueTimeSeries(),
                UnicodeUtil.TAU, "torque", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                tm, true, tm.getTimeSeriesModel(), tm.getTorqueDriven(), maxDomainValue, tm.getRotationPlatform() ) );
//        RotationBody b = tm.getRotationBody( 0 );
//        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new MotionControlGraph(
//                pSwingCanvas, b.getXPositionVariable(), b.getXPositionTimeSeries(), "x", "Position", 0, 500, Color.blue,
//                new PImage( loadArrow( "blue-arrow.png" ) ), tm, false, tm.getTimeSeriesModel(), null, maxDomainValue, null ) );
//        linearPositionGraph.getControlGraph().addSeries( "Position", Color.red, "y", b.getYPositionVariable(), b.getYPositionTimeSeries() );
//
//        MinimizableControlGraph forceGraph = new MinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new MotionControlGraph(
//                pSwingCanvas, b.getXVelocityVariable(), b.getXVelocityTimeSeries(), "vx", "Velocity (x)", -10, 10, Color.blue,
//                new PImage( loadArrow( "blue-arrow.png" ) ), tm, false, tm.getTimeSeriesModel(), null, maxDomainValue, null ) );
//        forceGraph.getControlGraph().addSeries( "Velocity (y)", Color.red, "vy", b.getYVelocityVariable(), b.getYVelocityTimeSeries() );
//        forceGraph.getControlGraph().addSeries( "|Velocity|", Color.green, "|v|", b.getSpeedVariable(), b.getSpeedSeries() );
//
//        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new MotionControlGraph(
//                pSwingCanvas, b.getXAccelVariable(), b.getXAccelTimeSeries(), "ax", "Acceleration (x)", -1, 1, Color.green,
//                new PImage( loadArrow( "green-arrow.png" ) ), tm, false, tm.getTimeSeriesModel(), null, maxDomainValue, null ) );
//        centripetalAccelGraph.getControlGraph().addSeries( "Acceleration (y)", Color.red, "ay", b.getYAccelVariable(), b.getYAccelTimeSeries() );
//        centripetalAccelGraph.getControlGraph().addSeries( "|Acceleration|", Color.blue, "a", b.getAccelMagnitudeVariable(), b.getAccelMagnitudeSeries() );

//        addGraphSuite(new MinimizableControlGraph[]{forceGraph,torqueGraph});
        addGraphSuite(new MinimizableControlGraph[]{torqueGraph,accelGraph,velocityGraph,angleGraph});
//        addGraphSuite(new MinimizableControlGraph[]{velocityGraph,momentOfInertiaGraph,angularMomentumGraph});

    }

    public static void main( String[] args ) {
        TorqueGraphSet rotationGraphSet = new TorqueGraphSet( new PhetPCanvas(), new TorqueModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
