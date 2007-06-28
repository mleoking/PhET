package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.graphs.MotionControlGraph;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeModelClock;
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
        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformAngleVariable(), rotationModel.getPlatformAngleTimeSeries(), UnicodeUtil.THETA, "Angular Position", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getPositionDriven(), rotationModel.getRotationPlatform() ) );
        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformVelocityVariable(), rotationModel.getPlatformVelocityTimeSeries(), UnicodeUtil.OMEGA, "Angular Velocity", -0.1, 0.1, Color.red, new PImage( loadArrow( "red-arrow.png" ) ), rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getVelocityDriven(), rotationModel.getRotationPlatform() ) );
        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new MotionControlGraph(
                pSwingCanvas, rotationModel.getPlatformAccelVariable(), rotationModel.getPlatformAccelTimeSeries(), UnicodeUtil.ALPHA, "Angular Acceleration", -0.001, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getAccelDriven(), rotationModel.getRotationPlatform() ) );
//        //todo: add graph suites after series available
        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new MotionControlGraph(
                pSwingCanvas, rotationModel.getRotationBody( 0 ).getXPositionVariable(), rotationModel.getRotationBody( 0 ).getXPositionTimeSeries(), "x", "Position", 0, 500, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null ) );
        linearPositionGraph.getControlGraph().addSeries( "Position", Color.red, "y", rotationModel.getRotationBody( 0 ).getYPositionVariable(), rotationModel.getRotationBody( 0 ).getYPositionTimeSeries() );

        MinimizableControlGraph speedGraph = new MinimizableControlGraph( "vx", new MotionControlGraph(
                pSwingCanvas, rotationModel.getBody( 0 ).getXVelocityVariable(), rotationModel.getBody( 0 ).getXVelocityTimeSeries(), "vx", "Linear Speed (x)", -10, 10, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null ) );
        speedGraph.getControlGraph().addSeries( "Linear Speed(y)", Color.red, "vy", rotationModel.getRotationBody( 0 ).getYVelocityVariable(), rotationModel.getRotationBody( 0 ).getYVelocityTimeSeries() );
//        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a", new MotionControlGraph(
//                pSwingCanvas, rotationModel.getBody( 0).getCentripetalAcceleration(), "a", "Centripetal Acceleration", 0, 0.001, Color.green, new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(),null) );

        //new GraphSuite( new GraphComponent[]{getAngleGraph(), getAngularVelocityGraph(), getPositionGraph()} ),//todo: remove after testing
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearPositionGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, speedGraph} );
//        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, centripetalAccelGraph} );
//        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph, linearPositionGraph, speedGraph, centripetalAccelGraph} );

//        addGraphSuite( new MinimizableControlGraph[]{linearPositionGraph, platformAngleGraph, platformVelocityGraph, platformAccelGraph} );//todo: remove after testing
        addGraphSuite( new MinimizableControlGraph[]{linearPositionGraph, angleGraph, speedGraph, velocityGraph, accelGraph} );//todo: remove after testing
    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new TimeModelClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
