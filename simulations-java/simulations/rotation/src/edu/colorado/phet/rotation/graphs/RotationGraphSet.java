package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
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
    private Stroke body0Stroke = new BasicStroke( 3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );
    private Stroke body1Stroke = new BasicStroke( 4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[]{10, 10}, 0 );
    private RotationModel model;

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel model ) {
        this.model = model;
        double maxTime = 30.0;
        final RotationBody b0 = model.getRotationBody( 0 );
        final RotationBody b1 = model.getRotationBody( 1 );

        final RotationMinimizableControlGraph angleGraph = new RotationMinimizableControlGraph( UnicodeUtil.THETA, new RotationGraph(
                pSwingCanvas, b0.getAngleVariable(),
                UnicodeUtil.THETA, "Angular Position", "radians", -Math.PI * 3, Math.PI * 3, new PImage( loadArrow( "blue-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), null,
                maxTime, model.getRotationPlatform() ) {

            //workaround for controlling the platform angle via the character angle
            protected void handleControlFocusGrabbed() {
                model.setPositionDriven();
            }

            //workaround for controlling the platform angle via the character angle
            protected void handleValueChanged() {
                //this is very fragile;if the wrong value is set here, it will cause the angular velocity & angle to quickly blow up
                //since there is a bidirectional causality between bug and platform 
                model.getRotationPlatform().setAngle( getSliderValue() - b0.getInitialAngleOnPlatform() );
            }
        } );
        angleGraph.addSeriesPair( "Angular Position",
                                  new ControlGraphSeries( "Angular Position", Color.blue, "body1", b0.getAngleVariable(), b0.getAngleTimeSeries(), body0Stroke ),
                                  new ControlGraphSeries( "Angular Position (2)", Color.blue, "body2", b1.getAngleVariable(), b1.getAngleTimeSeries(), body1Stroke ) );

        RotationMinimizableControlGraph velocityGraph = new RotationMinimizableControlGraph( UnicodeUtil.OMEGA, new RotationGraph(
                pSwingCanvas, model.getPlatformVelocityVariable(),
                UnicodeUtil.OMEGA, "Angular Velocity", "radians/sec", -5, 5, new PImage( loadArrow( "red-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), maxTime, model.getRotationPlatform() ) );
        velocityGraph.addSeriesPair( "Angular Velocity",
                                     new ControlGraphSeries( "Angular Velocity", Color.red, "w", model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body0Stroke ),
                                     new ControlGraphSeries( "Angular Velocity (2)", Color.red, "w", model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body1Stroke ) );

        RotationMinimizableControlGraph accelGraph = new RotationMinimizableControlGraph( UnicodeUtil.ALPHA, new RotationGraph(
                pSwingCanvas, model.getPlatformAccelVariable(),
                UnicodeUtil.ALPHA, "Angular Acceleration", "radians/sec^2", -1.1, 1.1, new PImage( loadArrow( "green-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getAccelDriven(), maxTime, model.getRotationPlatform() ) );
        accelGraph.addSeriesPair( "Angular Acceleration",
                                  new ControlGraphSeries( "Angular Acceleration", Color.green, UnicodeUtil.ALPHA, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(), body0Stroke ),
                                  new ControlGraphSeries( "Angular Acceleration (2) ", Color.green, UnicodeUtil.ALPHA, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(), body1Stroke ) );

        RotationMinimizableControlGraph linearPositionGraph = new RotationMinimizableControlGraph( "x,y", new RotationGraph(
                pSwingCanvas, b0.getXPositionVariable(), "x", "X-Position", "m", -5, 5,
                new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        linearPositionGraph.addSeriesPair( "X-Position",
                                           new ControlGraphSeries( "X-Position", Color.red, "x", b0.getXPositionVariable(), b0.getXPositionTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "X-Position(2)", Color.red, "x", b1.getXPositionVariable(), b1.getXPositionTimeSeries(), body1Stroke ) );
        linearPositionGraph.addSeriesPair( "Y-Position",
                                           new ControlGraphSeries( "Y-Position", Color.red, "y", b0.getYPositionVariable(), b0.getYPositionTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Y-Position(2)", Color.red, "y", b1.getYPositionVariable(), b1.getYPositionTimeSeries(), body1Stroke ) );

        final RotationMinimizableControlGraph linearVelocityGraph = new RotationMinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b0.getXVelocityVariable(), "vx", "X-Velocity", "m/s", -15, +15,
                new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        linearVelocityGraph.addSeriesPair( "X-Velocity",
                                           new ControlGraphSeries( "X-Velocity", Color.red, "vx", b0.getXVelocityVariable(), b0.getXVelocityTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "X-Velocity(2)", Color.red, "vx", b1.getXVelocityVariable(), b1.getXVelocityTimeSeries(), body1Stroke ) );
        linearVelocityGraph.addSeriesPair( "Y-Velocity",
                                           new ControlGraphSeries( "Y-Velocity", Color.red, "vy", b0.getYVelocityVariable(), b0.getYVelocityTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Y-Velocity(2)", Color.red, "vy", b1.getYVelocityVariable(), b1.getYVelocityTimeSeries(), body1Stroke ) );
        linearVelocityGraph.addSeriesPair( "Speed",
                                           new ControlGraphSeries( "Speed", Color.green, "|v|", b0.getSpeedVariable(), b0.getSpeedSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Speed(2)", Color.green, "|v|", b1.getSpeedVariable(), b1.getSpeedSeries(), body1Stroke ) );

        RotationMinimizableControlGraph centripetalAccelGraph = new RotationMinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b0.getXAccelVariable(), "ax", "Acceleration (x)", "m/s^2", -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0,
                new PImage( loadArrow( "green-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        centripetalAccelGraph.addSeriesPair( "X-Acceleration",
                                             new ControlGraphSeries( "X-Acceleration", Color.green, "ax", b0.getXAccelVariable(), b0.getXAccelTimeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "X-Acceleration(2)", Color.green, "ax", b1.getXAccelVariable(), b1.getXAccelTimeSeries(), body1Stroke ) );
        centripetalAccelGraph.addSeriesPair( "Y-Acceleration",
                                             new ControlGraphSeries( "Y-Acceleration", Color.red, "ay", b0.getYAccelVariable(), b0.getYAccelTimeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "Y-Acceleration(2)", Color.red, "ay", b1.getYAccelVariable(), b1.getYAccelTimeSeries(), body1Stroke ) );
        centripetalAccelGraph.addSeriesPair( "|Acceleration|",
                                             new ControlGraphSeries( "|Acceleration|", Color.blue, "a", b0.getAccelMagnitudeVariable(), b0.getAccelMagnitudeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "|Acceleration|(2)", Color.blue, "a", b1.getAccelMagnitudeVariable(), b1.getAccelMagnitudeSeries(), body1Stroke ) );

//The following graph suites are for testing only
//        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph, linearPositionGraph, linearVelocityGraph, centripetalAccelGraph} );
//        addGraphSuite( new RotationMinimizableControlGraph[]{linearPositionGraph, angleGraph, linearVelocityGraph, velocityGraph, accelGraph} );

        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, velocityGraph, linearPositionGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, velocityGraph, linearVelocityGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, velocityGraph, centripetalAccelGraph} );

        addSeriesSelectionPanels();

        b1.addListener( new RotationBody.Adapter() {
            public void platformStateChanged() {
                updateBody1Series();
            }
        } );
        updateBody1Series();
    }

    private void addSeriesSelectionPanels() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            if( graphs[i].getControlGraph().getSeriesCount() > 1 ) {
                //todo: only show one checkbox for both characters, and have it apply to both characters
                //we'll need to pair up the series pairs to facilitate this.
                RotationMinimizableControlGraph graph = (RotationMinimizableControlGraph)graphs[i];
                graphs[i].getControlGraph().addControl( new RotationSeriesSelectionPanel( graph.getRotationControlGraph() ) );
            }
        }
    }

    private void updateBody1Series() {
        for( int i = 0; i < getAllGraphs().length; i++ ) {
            RotationMinimizableControlGraph rotationMinimizableControlGraph = (RotationMinimizableControlGraph)getAllGraphs()[i];
            ( (RotationGraph)rotationMinimizableControlGraph.getControlGraph() ).setSecondarySeriesVisible( model.getRotationBody( 1 ).isOnPlatform() );
        }
    }

//    private void addSeriesSelectionPanels() {
//        MinimizableControlGraph[] graphs = getAllGraphs();
//        for( int i = 0; i < graphs.length; i++ ) {
//            if( graphs[i].getControlGraph().getSeriesCount() > 1 ) {
//                //todo: only show one checkbox for both characters, and have it apply to both characters
//                //we'll need to pair up the series pairs to facilitate this.
//                graphs[i].getControlGraph().addControl( new SeriesSelectionPanel( graphs[i].getControlGraph() ) );
//            }
//        }
//    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
