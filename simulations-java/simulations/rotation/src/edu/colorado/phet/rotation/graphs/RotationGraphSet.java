package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.RotationColorScheme;
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
    private String ANGLE_UNITS = "radians";
    private static final String ANG_VEL_UNITS = "rad/s";
    private static final String ANG_ACC_UNITS = "rad/s^2";
    private static final String POSITION_UNITS = "m";
    private static final String VELOCITY_UNITS = "m/s";
    private String ACCEL_UNITS = "m/s^2";

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel model ) {
        this.model = model;

        final RotationBody b0 = model.getRotationBody( 0 );
        final RotationBody b1 = model.getRotationBody( 1 );

        //todo: clearer separation between platform series and body series
        final RotationMinimizableControlGraph angleGraph = new RotationMinimizableControlGraph( UnicodeUtil.THETA, new RotationGraph(
                pSwingCanvas, b0.getAngleVariable(),
                UnicodeUtil.THETA, "Angle", ANGLE_UNITS, -Math.PI * 3, Math.PI * 3, new PImage( loadArrow( "blue-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), null,
                RotationModel.MAX_TIME, model.getRotationPlatform() ) {

            //workaround for controlling the platform angle via the character angle
            protected void handleControlFocusGrabbed() {
                model.setPositionDriven();
            }

            //workaround for controlling the platform angle via the character angle
            protected void handleValueChanged() {
                //this is very fragile;if the wrong value is set here, it will cause the angular velocity & angle to quickly blow up
                //since there is a bidirectional causality between bug and platform 
                model.getRotationPlatform().setAngle( getSliderValue() - b0.getInitialAngleOnPlatform() );

                //workaround for angle variable not updating correctly when simulation is paused
                b0.getAngleVariable().setValue( getSliderValue() );
            }

        } );
        angleGraph.addSeriesPair( "Angle",
                                  new ControlGraphSeries( "Angle", RotationColorScheme.ANGLE_COLOR, UnicodeUtil.THETA, ANGLE_UNITS, b0.getAngleVariable(), b0.getAngleTimeSeries(), body0Stroke, true ),
                                  new ControlGraphSeries( "Angle (2)", darken( RotationColorScheme.ANGLE_COLOR ), UnicodeUtil.THETA, ANGLE_UNITS, b1.getAngleVariable(), b1.getAngleTimeSeries(), body1Stroke ),
                                  b1 );

        RotationMinimizableControlGraph velocityGraph = new RotationMinimizableControlGraph( UnicodeUtil.OMEGA, new RotationGraph(
                pSwingCanvas, model.getPlatformVelocityVariable(),
                UnicodeUtil.OMEGA, "Angular Velocity", ANG_VEL_UNITS, -5, 5, new PImage( loadArrow( "red-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        velocityGraph.addSeriesPair( "Angular Velocity",
                                     new ControlGraphSeries( "Angular Velocity", RotationColorScheme.ANGULAR_VELOCITY_COLOR, UnicodeUtil.OMEGA, ANG_VEL_UNITS, model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body0Stroke, true ),
                                     new ControlGraphSeries( "Angular Velocity (2)", darken( RotationColorScheme.ANGULAR_VELOCITY_COLOR ), UnicodeUtil.OMEGA, ANG_VEL_UNITS, model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body1Stroke ),
                                     b1 );

        RotationMinimizableControlGraph accelGraph = new RotationMinimizableControlGraph( UnicodeUtil.ALPHA, new RotationGraph(
                pSwingCanvas, model.getPlatformAccelVariable(),
                UnicodeUtil.ALPHA, "Angular Acceleration", ANG_ACC_UNITS, -1.1, 1.1, new PImage( loadArrow( "green-arrow.png" ) ),
                model, true, model.getTimeSeriesModel(), model.getAccelDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        accelGraph.addSeriesPair( "Angular Acceleration",
                                  new ControlGraphSeries( "Angular Acceleration", RotationColorScheme.ANGULAR_ACCELERATION_COLOR, UnicodeUtil.ALPHA, ANG_ACC_UNITS, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(), body0Stroke, true ),
                                  new ControlGraphSeries( "Angular Acceleration (2) ", darken( RotationColorScheme.ANGULAR_ACCELERATION_COLOR ), UnicodeUtil.ALPHA, ANG_ACC_UNITS, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(), body1Stroke ), b1 );

        RotationMinimizableControlGraph linearPositionGraph = new RotationMinimizableControlGraph( "x,y", new RotationGraph(
                pSwingCanvas, b0.getXPositionVariable(), "x", "Position", POSITION_UNITS, -5, 5,
                new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        linearPositionGraph.addSeriesPair( "X-Position",
                                           new ControlGraphSeries( "X-Position", RotationColorScheme.X_COLOR, "x", POSITION_UNITS, b0.getXPositionVariable(), b0.getXPositionTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "X-Position(2)", darken( RotationColorScheme.X_COLOR ), "x", POSITION_UNITS, b1.getXPositionVariable(), b1.getXPositionTimeSeries(), body1Stroke ),
                                           b1 );
        linearPositionGraph.addSeriesPair( "Y-Position",
                                           new ControlGraphSeries( "Y-Position", RotationColorScheme.Y_COLOR, "y", POSITION_UNITS, b0.getYPositionVariable(), b0.getYPositionTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Y-Position(2)", darken( RotationColorScheme.Y_COLOR ), "y", POSITION_UNITS, b1.getYPositionVariable(), b1.getYPositionTimeSeries(), body1Stroke ),
                                           b1 );

        final RotationMinimizableControlGraph linearVelocityGraph = new RotationMinimizableControlGraph( "v<sub>x</sub>,v<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b0.getXVelocityVariable(), "vx", "Velocity", VELOCITY_UNITS, -15, +15,
                new PImage( loadArrow( "blue-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        linearVelocityGraph.addSeriesPair( "Speed",
                                           new ControlGraphSeries( "Speed", RotationColorScheme.VM_COLOR, "|v|", VELOCITY_UNITS, b0.getSpeedVariable(), b0.getSpeedSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Speed(2)", darken( RotationColorScheme.VM_COLOR ), "|v|", VELOCITY_UNITS, b1.getSpeedVariable(), b1.getSpeedSeries(), body1Stroke ), b1 );
        linearVelocityGraph.addSeriesPair( "X-Velocity",
                                           new ControlGraphSeries( "X-Velocity", RotationColorScheme.VX_COLOR, "vx", VELOCITY_UNITS, b0.getXVelocityVariable(), b0.getXVelocityTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "X-Velocity(2)", darken( RotationColorScheme.VX_COLOR ), "vx", VELOCITY_UNITS, b1.getXVelocityVariable(), b1.getXVelocityTimeSeries(), body1Stroke ),
                                           b1 );
        linearVelocityGraph.addSeriesPair( "Y-Velocity",
                                           new ControlGraphSeries( "Y-Velocity", RotationColorScheme.VY_COLOR, "vy", VELOCITY_UNITS, b0.getYVelocityVariable(), b0.getYVelocityTimeSeries(), body0Stroke ),
                                           new ControlGraphSeries( "Y-Velocity(2)", darken( RotationColorScheme.VY_COLOR ), "vy", VELOCITY_UNITS, b1.getYVelocityVariable(), b1.getYVelocityTimeSeries(), body1Stroke ),
                                           b1 );

        RotationMinimizableControlGraph centripetalAccelGraph = new RotationMinimizableControlGraph( "a<sub>x</sub>,a<sub>y</sub>", new RotationGraph(
                pSwingCanvas, b0.getXAccelVariable(), "ax", "Acceleration", ACCEL_UNITS, -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0,
                new PImage( loadArrow( "green-arrow.png" ) ), model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );

        centripetalAccelGraph.addSeriesPair( "|Acceleration|",
                                             new ControlGraphSeries( "|Acceleration|", RotationColorScheme.AM_COLOR, "a", ACCEL_UNITS, b0.getAccelMagnitudeVariable(), b0.getAccelMagnitudeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "|Acceleration|(2)", darken( RotationColorScheme.AM_COLOR ), "a", ACCEL_UNITS, b1.getAccelMagnitudeVariable(), b1.getAccelMagnitudeSeries(), body1Stroke ),
                                             b1 );
        centripetalAccelGraph.addSeriesPair( "X-Acceleration",
                                             new ControlGraphSeries( "X-Acceleration", RotationColorScheme.AX_COLOR, "ax", ACCEL_UNITS, b0.getXAccelVariable(), b0.getXAccelTimeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "X-Acceleration(2)", darken( RotationColorScheme.AX_COLOR ), "ax", ACCEL_UNITS, b1.getXAccelVariable(), b1.getXAccelTimeSeries(), body1Stroke ),
                                             b1 );
        centripetalAccelGraph.addSeriesPair( "Y-Acceleration",
                                             new ControlGraphSeries( "Y-Acceleration", RotationColorScheme.AY_COLOR, "ay", ACCEL_UNITS, b0.getYAccelVariable(), b0.getYAccelTimeSeries(), body0Stroke ),
                                             new ControlGraphSeries( "Y-Acceleration(2)", darken( RotationColorScheme.AY_COLOR ), "ay", ACCEL_UNITS, b1.getYAccelVariable(), b1.getYAccelTimeSeries(), body1Stroke ), b1 );

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

    private Color darken( Color color ) {
        return darken( color, 150 );
    }

    private Color darken( Color color, int amount ) {
        return new Color( Math.max( color.getRed() - amount, 0 ),
                          Math.max( color.getGreen() - amount, 0 ),
                          Math.max( color.getBlue() - amount, 0 ) );
    }

    private void addSeriesSelectionPanels() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for( int i = 0; i < graphs.length; i++ ) {
            //todo: only show one checkbox for both characters, and have it apply to both characters
            //we'll need to pair up the series pairs to facilitate this.
            RotationMinimizableControlGraph graph = (RotationMinimizableControlGraph)graphs[i];
            if( graph.getRotationControlGraph().getSeriesPairCount() > 1 ) {
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

    public static void main( String[] args ) {
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
