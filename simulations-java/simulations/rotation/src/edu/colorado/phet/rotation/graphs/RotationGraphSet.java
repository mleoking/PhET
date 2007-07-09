package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.*;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class RotationGraphSet extends GraphSuiteSet {

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        int maxDomainValue=500;
        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformAngleVariable(), rotationModel.getPlatformAngleTimeSeries(),
                UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getPositionDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );

        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformVelocityVariable(), rotationModel.getPlatformVelocityTimeSeries(),
                UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getVelocityDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );

        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformAccelVariable(), rotationModel.getPlatformAccelTimeSeries(),
                UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getAccelDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );

        RotationBody b = rotationModel.getRotationBody( 0 );
        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new MotionControlGraph(
                pSwingCanvas, b.getXPositionVariable(), b.getXPositionTimeSeries(), "x", "Position", 0, 500, Color.blue,
                new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null ,maxDomainValue, null) );
        linearPositionGraph.getControlGraph().addSeries( "Position", Color.red, "y", b.getYPositionVariable(), b.getYPositionTimeSeries() );

        MinimizableControlGraph linearVelocityGraph = new MinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new MotionControlGraph(
                pSwingCanvas, b.getXVelocityVariable(), b.getXVelocityTimeSeries(), "vx", "Velocity (x)", -10, 10, Color.blue,
                new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null,maxDomainValue, null) );
        linearVelocityGraph.getControlGraph().addSeries( "Velocity (y)", Color.red, "vy", b.getYVelocityVariable(), b.getYVelocityTimeSeries() );
        linearVelocityGraph.getControlGraph().addSeries( "|Velocity|", Color.green, "|v|", b.getSpeedVariable(), b.getSpeedSeries() );

        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new MotionControlGraph(
                pSwingCanvas, b.getXAccelVariable(), b.getXAccelTimeSeries(), "ax", "Acceleration (x)", -1, 1, Color.green,
                new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null,maxDomainValue, null) );
        centripetalAccelGraph.getControlGraph().addSeries( "Acceleration (y)", Color.red, "ay", b.getYAccelVariable(), b.getYAccelTimeSeries() );
        centripetalAccelGraph.getControlGraph().addSeries( "|Acceleration|", Color.blue, "a", b.getAccelMagnitudeVariable(), b.getAccelMagnitudeSeries() );

//The following graph suites are for testing only
//        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph, linearPositionGraph, linearVelocityGraph, centripetalAccelGraph} );
//        addGraphSuite( new MinimizableControlGraph[]{linearPositionGraph, angleGraph, linearVelocityGraph, velocityGraph, accelGraph} );

        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearPositionGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearVelocityGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, centripetalAccelGraph} );
    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
