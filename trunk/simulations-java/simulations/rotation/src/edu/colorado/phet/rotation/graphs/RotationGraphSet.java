package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
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

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel model ) {
        double maxTime = 30.0;
        final RotationBody b = model.getRotationBody( 0 );


        final MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new RotationGraph(
                pSwingCanvas, b.getAngleVariable(), b.getAngleTimeSeries(),
                UnicodeUtil.THETA, "Angular Position", "radians", -Math.PI * 3, Math.PI * 3, Color.blue, body0Stroke, new PImage( loadArrow( "blue-arrow.png" ) ),
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
                model.getRotationPlatform().setAngle( getSliderValue() - b.getInitialAngleOnPlatform() );
            }
        } );

        ( (RotationGraph)angleGraph.getControlGraph() ).addSecondarySeries( "Angular Position (2)", Color.blue, "body2", model.getRotationBody( 1 ).getAngleVariable(), model.getRotationBody( 1 ).getAngleTimeSeries(), body1Stroke );
        model.getRotationBody( 1 ).addListener( new RotationBody.Adapter() {
            public void movedOffPlatform() {
                ( (RotationGraph)angleGraph.getControlGraph() ).setSecondarySeriesVisible( false );
            }

            public void movedOntoPlatform() {
                ( (RotationGraph)angleGraph.getControlGraph() ).setSecondarySeriesVisible( true );
            }
        } );

        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new RotationGraph(
                pSwingCanvas, model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(),
                UnicodeUtil.OMEGA, "Angular Velocity", "radians/sec", -5, 5, Color.red, body0Stroke, new PImage( loadArrow( "red-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), maxTime, model.getRotationPlatform() ) );
        ( (RotationGraph)velocityGraph.getControlGraph() ).addSecondarySeries( "Angular Velocity (2)", Color.red, "w", model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body1Stroke );

        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new RotationGraph(
                pSwingCanvas, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(),
                UnicodeUtil.ALPHA, "Angular Acceleration", "radians/sec^2", -1.1, 1.1, Color.green, body0Stroke, new PImage( loadArrow( "green-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getAccelDriven(), maxTime, model.getRotationPlatform() ) );


        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new RotationGraph(
                pSwingCanvas, b.getXPositionVariable(), b.getXPositionTimeSeries(), "x", "X-Position", "m", -5, 5, Color.blue,
                body0Stroke, new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        linearPositionGraph.getControlGraph().addSeries( "Y-Position", Color.red, "y", b.getYPositionVariable(), b.getYPositionTimeSeries(), body0Stroke );

        final MinimizableControlGraph linearVelocityGraph = new MinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b.getXVelocityVariable(), b.getXVelocityTimeSeries(), "vx", "X-Velocity", "m/s", -15, +15, Color.blue,
                body0Stroke, new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        linearVelocityGraph.getControlGraph().addSeries( new ControlGraph.ControlGraphSeries( "Y-Velocity", Color.red, "vy", b.getYVelocityVariable(), b.getYVelocityTimeSeries(), body0Stroke ) );
        linearVelocityGraph.getControlGraph().addSeries( new ControlGraph.ControlGraphSeries( "Speed", Color.green, "|v|", b.getSpeedVariable(), b.getSpeedSeries(), body0Stroke ) );


        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b.getXAccelVariable(), b.getXAccelTimeSeries(), "ax", "Acceleration (x)", "m/s^2", -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0, Color.green,
                body0Stroke, new PImage( loadArrow( "green-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, maxTime, null ) );
        centripetalAccelGraph.getControlGraph().addSeries( "Acceleration (y)", Color.red, "ay", b.getYAccelVariable(), b.getYAccelTimeSeries(), body0Stroke );
        centripetalAccelGraph.getControlGraph().addSeries( "|Acceleration|", Color.blue, "a", b.getAccelMagnitudeVariable(), b.getAccelMagnitudeSeries(), body0Stroke );

//The following graph suites are for testing only
//        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph, linearPositionGraph, linearVelocityGraph, centripetalAccelGraph} );
//        addGraphSuite( new MinimizableControlGraph[]{linearPositionGraph, angleGraph, linearVelocityGraph, velocityGraph, accelGraph} );

        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearPositionGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, accelGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, linearVelocityGraph} );
        addGraphSuite( new MinimizableControlGraph[]{angleGraph, velocityGraph, centripetalAccelGraph} );

        addSeriesSelectionPanels();
    }

    private void addSeriesSelectionPanels() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            MinimizableControlGraph graph = graphs[i];
            if( graph.getControlGraph().getSeriesCount() > 1 ) {
                JPanel seriesSelectionPanel = new SeriesSelectionPanel( graph.getControlGraph() );
                graph.getControlGraph().addControl( seriesSelectionPanel );
            }
        }
    }

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
