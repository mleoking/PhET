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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */

public class RotationGraphSet extends GraphSuiteSet {

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel rotationModel ) {
        double maxDomainValue = 30.0;
        RotationBody b = rotationModel.getRotationBody( 0 );

        MinimizableControlGraph angleGraph = new MinimizableControlGraph( UnicodeUtil.THETA, new RotationGraph(
                pSwingCanvas, b.getAngleVariable(), b.getAngleTimeSeries(),
                UnicodeUtil.THETA, "Angular Position", "radians", -Math.PI * 3, Math.PI * 3, Color.blue, new PImage( loadArrow( "blue-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), b.getAngleDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );

        MinimizableControlGraph velocityGraph = new MinimizableControlGraph( UnicodeUtil.OMEGA, new RotationGraph(
                pSwingCanvas, rotationModel.getPlatformVelocityVariable(), rotationModel.getPlatformVelocityTimeSeries(),
                UnicodeUtil.OMEGA, "Angular Velocity", "radians/sec", -5, 5, Color.red, new PImage( loadArrow( "red-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getVelocityDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );

        MinimizableControlGraph accelGraph = new MinimizableControlGraph( UnicodeUtil.ALPHA, new RotationGraph(
                pSwingCanvas, rotationModel.getPlatformAccelVariable(), rotationModel.getPlatformAccelTimeSeries(),
                UnicodeUtil.ALPHA, "Angular Acceleration", "radians/sec^2", -0.001 / 0.03 / 0.03, 0.001 / 0.03 / 0.03, Color.green, new PImage( loadArrow( "green-arrow.png" ) ),
                rotationModel, true, rotationModel.getTimeSeriesModel(), rotationModel.getAccelDriven(), maxDomainValue, rotationModel.getRotationPlatform() ) );


        MinimizableControlGraph linearPositionGraph = new MinimizableControlGraph( "x,y", new RotationGraph(
                pSwingCanvas, b.getXPositionVariable(), b.getXPositionTimeSeries(), "x", "Position", "m", 0, 500, Color.blue,
                new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null, maxDomainValue, null ) );
        linearPositionGraph.getControlGraph().addSeries( "Position", Color.red, "y", b.getYPositionVariable(), b.getYPositionTimeSeries() );

        final MinimizableControlGraph linearVelocityGraph = new MinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b.getXVelocityVariable(), b.getXVelocityTimeSeries(), "vx", "X-Velocity", "m/s", -10 / 0.03 * 3, 10 / 0.03 * 3, Color.blue,
                new PImage( loadArrow( "blue-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null, maxDomainValue, null ) );
        final ControlGraph.ControlGraphSeries yvel0 = new ControlGraph.ControlGraphSeries( "Y-Velocity", Color.red, "vy", b.getYVelocityVariable(), b.getYVelocityTimeSeries() );
        linearVelocityGraph.getControlGraph().addSeries( yvel0 );
        linearVelocityGraph.getControlGraph().addSeries( new ControlGraph.ControlGraphSeries( "Speed", Color.green, "|v|", b.getSpeedVariable(), b.getSpeedSeries() ) );

        JPanel linearVelocitySeriesSelectionPanel = new JPanel();
        linearVelocitySeriesSelectionPanel.setLayout( new BoxLayout( linearVelocitySeriesSelectionPanel, BoxLayout.Y_AXIS ) );
        final JCheckBox jCheckBox = new JCheckBox( "Y-Velocity", true );
        jCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                linearVelocityGraph.getControlGraph().setSeriesVisible( yvel0,jCheckBox.isSelected() );
            }
        } );
        linearVelocitySeriesSelectionPanel.add( jCheckBox );
        linearVelocitySeriesSelectionPanel.add( new JCheckBox( "X-Velocity", true ) );
        linearVelocitySeriesSelectionPanel.add( new JCheckBox( "Speed", true ) );
        linearVelocityGraph.addControl( linearVelocitySeriesSelectionPanel );

        MinimizableControlGraph centripetalAccelGraph = new MinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b.getXAccelVariable(), b.getXAccelTimeSeries(), "ax", "Acceleration (x)", "m/s^2", -1 / 0.03 / 0.03, 1 / 0.03 / 0.03, Color.green,
                new PImage( loadArrow( "green-arrow.png" ) ), rotationModel, false, rotationModel.getTimeSeriesModel(), null, maxDomainValue, null ) );
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
