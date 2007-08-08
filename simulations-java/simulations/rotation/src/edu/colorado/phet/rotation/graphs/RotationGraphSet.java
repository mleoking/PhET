package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.tests.ColorArrows;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.colorado.phet.rotation.view.RotationColorScheme;
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
    private Stroke platformStroke = new BasicStroke( 4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f );

    private RotationModel model;
    private String ANGLE_UNITS_RAD = "radians";
    private String ANGLE_UNITS_DEG = "degrees";
    private static final String ANG_VEL_UNITS_RAD = "rad/s";
    private static final String ANG_VEL_UNITS_DEG = "degrees/s";
    private static final String ANG_ACC_UNITS_RAD = "rad/s^2";
    private static final String ANG_ACC_UNITS_DEG = "degrees/s^2";
    private static final String POSITION_UNITS = "m";
    private static final String VELOCITY_UNITS = "m/s";
    private String ACCEL_UNITS = "m/s^2";

    private static final String CHARACTER_PLATFORM = "platform";
    private static final String CHARACTER_LADY = "ladybug";
    private static final String CHARACTER_BEETLE = "beetle";

    public RotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel model, AngleUnitModel angleUnitModel ) {
        this.model = model;

        final RotationBody b0 = model.getRotationBody( 0 );
        final RotationBody b1 = model.getRotationBody( 1 );

        final RotationMinimizableControlGraph angleGraph = new RotationMinimizableControlGraph( UnicodeUtil.THETA, new AngularUnitGraph(
                pSwingCanvas, model.getPlatformAngleVariable(),
                UnicodeUtil.THETA, "Angle", angleUnitModel, ANGLE_UNITS_RAD, ANGLE_UNITS_DEG, -Math.PI * 3, Math.PI * 3, createThumb( RotationColorScheme.ANGLE_SUITE.getPlatformColor() ),
                model, true, model.getTimeSeriesModel(), model.getPositionDriven(),
                RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        final ControlGraphSeries platformAngleSeries = new ControlGraphSeries( "Angle", RotationColorScheme.ANGLE_SUITE.getPlatformColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, model.getPlatformAngleVariable(), model.getPlatformAngleTimeSeries(), platformStroke, true, CHARACTER_PLATFORM );
        angleGraph.addSeries( platformAngleSeries );
        angleGraph.addSeriesPair( "Angle",
                                  new ControlGraphSeries( "Angle", RotationColorScheme.ANGLE_SUITE.getLadybugColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, b0.getAngleVariable(), b0.getAngleTimeSeries(), body0Stroke, CHARACTER_LADY ),
                                  new ControlGraphSeries( "Angle (2)", RotationColorScheme.ANGLE_SUITE.getBeetleColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, b1.getAngleVariable(), b1.getAngleTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                                  b0, b1 );

        RotationMinimizableControlGraph angVelGraph = new RotationMinimizableControlGraph( UnicodeUtil.OMEGA, new AngularUnitGraph(
                pSwingCanvas, model.getPlatformVelocityVariable(),
                UnicodeUtil.OMEGA, "Angular Velocity", angleUnitModel, ANG_VEL_UNITS_RAD, ANG_VEL_UNITS_DEG, -5, 5, createThumb( RotationColorScheme.ANG_VEL_SUITE.getPlatformColor() ),
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        final ControlGraphSeries platformVelSeries = new ControlGraphSeries( "Angular Velocity", RotationColorScheme.ANG_VEL_SUITE.getPlatformColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), platformStroke, true, CHARACTER_PLATFORM );
        angVelGraph.addSeries( platformVelSeries );
        angVelGraph.addSeriesPair( "Angular Velocity",
                                   new ControlGraphSeries( "Angular Velocity", RotationColorScheme.ANG_VEL_SUITE.getLadybugColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, b0.getAngularVelocityVariable(), b0.getAngularVelocityTimeSeries(), body0Stroke, CHARACTER_LADY ),
                                   new ControlGraphSeries( "Angular Velocity (2)", RotationColorScheme.ANG_VEL_SUITE.getBeetleColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, model.getPlatformVelocityVariable(), model.getPlatformVelocityTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                                   b0, b1 );

        RotationMinimizableControlGraph angAccelGraph = new RotationMinimizableControlGraph( UnicodeUtil.ALPHA, new AngularUnitGraph(
                pSwingCanvas, model.getPlatformAccelVariable(),
                UnicodeUtil.ALPHA, "Angular Acceleration", angleUnitModel, ANG_ACC_UNITS_RAD, ANG_ACC_UNITS_DEG, -1.1, 1.1, createThumb( RotationColorScheme.ANG_ACC_SUITE.getPlatformColor() ),
                model, true, model.getTimeSeriesModel(), model.getAccelDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        final ControlGraphSeries platformAccelSeries = new ControlGraphSeries( "Platform Ang Accel", RotationColorScheme.ANG_ACC_SUITE.getPlatformColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, model.getPlatformAccelVariable(), model.getPlatformAccelTimeSeries(), platformStroke, true, CHARACTER_PLATFORM );
        angAccelGraph.addSeries( platformAccelSeries );
        angAccelGraph.addSeriesPair( "Angular Acceleration",
                                     new ControlGraphSeries( "Angular Acceleration", RotationColorScheme.ANG_ACC_SUITE.getLadybugColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, b0.getAngularAccelerationVariable(), b0.getAngularAccelerationTimeSeries(), body0Stroke, CHARACTER_LADY ),
                                     new ControlGraphSeries( "Angular Acceleration (2) ", RotationColorScheme.ANG_ACC_SUITE.getBeetleColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, b1.getAngularAccelerationVariable(), b1.getAngularAccelerationTimeSeries(), body1Stroke, CHARACTER_BEETLE ), b0, b1 );

        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformAngleSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
                platformVelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
                platformAccelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );

        RotationMinimizableControlGraph xGraph = new RotationMinimizableControlGraph( "x & y", new RotationGraph(
                pSwingCanvas, b0.getXPositionVariable(), "x", "Position", POSITION_UNITS, -5, 5,
                null, model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        xGraph.addSeriesPair( "X-Position",
                              new ControlGraphSeries( "X-Position", RotationColorScheme.X_COLOR, "x", POSITION_UNITS, b0.getXPositionVariable(), b0.getXPositionTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "X-Position(2)", darken( RotationColorScheme.X_COLOR ), "x", POSITION_UNITS, b1.getXPositionVariable(), b1.getXPositionTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1 );
        xGraph.addSeriesPair( "Y-Position",
                              new ControlGraphSeries( "Y-Position", RotationColorScheme.Y_COLOR, "y", POSITION_UNITS, b0.getYPositionVariable(), b0.getYPositionTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "Y-Position(2)", darken( RotationColorScheme.Y_COLOR ), "y", POSITION_UNITS, b1.getYPositionVariable(), b1.getYPositionTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1 );

        final RotationMinimizableControlGraph vGraph = new RotationMinimizableControlGraph( "v", new RotationGraph(
                pSwingCanvas, b0.getXVelocityVariable(), "vx", "Velocity", VELOCITY_UNITS, -15, +15,
                null, model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        vGraph.addSeriesPair( "Speed",
                              new ControlGraphSeries( "Speed", RotationColorScheme.VM_COLOR, "|v|", VELOCITY_UNITS, b0.getSpeedVariable(), b0.getSpeedSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "Speed(2)", darken( RotationColorScheme.VM_COLOR ), "|v|", VELOCITY_UNITS, b1.getSpeedVariable(), b1.getSpeedSeries(), body1Stroke, CHARACTER_BEETLE ), b0, b1 );
        vGraph.addSeriesPair( "X-Velocity",
                              new ControlGraphSeries( "X-Velocity", RotationColorScheme.VX_COLOR, "vx", VELOCITY_UNITS, b0.getXVelocityVariable(), b0.getXVelocityTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "X-Velocity(2)", darken( RotationColorScheme.VX_COLOR ), "vx", VELOCITY_UNITS, b1.getXVelocityVariable(), b1.getXVelocityTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1, false );
        vGraph.addSeriesPair( "Y-Velocity",
                              new ControlGraphSeries( "Y-Velocity", RotationColorScheme.VY_COLOR, "vy", VELOCITY_UNITS, b0.getYVelocityVariable(), b0.getYVelocityTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "Y-Velocity(2)", darken( RotationColorScheme.VY_COLOR ), "vy", VELOCITY_UNITS, b1.getYVelocityVariable(), b1.getYVelocityTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1, false );

        RotationMinimizableControlGraph aGraph = new RotationMinimizableControlGraph( "a", new RotationGraph(
                pSwingCanvas, b0.getXAccelVariable(), "ax", "Acceleration", ACCEL_UNITS, -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0,
                null, model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );

        aGraph.addSeriesPair( "|Acceleration|",
                              new ControlGraphSeries( "|Acceleration|", RotationColorScheme.AM_COLOR, "a", ACCEL_UNITS, b0.getAccelMagnitudeVariable(), b0.getAccelMagnitudeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "|Acceleration|(2)", darken( RotationColorScheme.AM_COLOR ), "a", ACCEL_UNITS, b1.getAccelMagnitudeVariable(), b1.getAccelMagnitudeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1 );
        aGraph.addSeriesPair( "X-Acceleration",
                              new ControlGraphSeries( "X-Acceleration", RotationColorScheme.AX_COLOR, "ax", ACCEL_UNITS, b0.getXAccelVariable(), b0.getXAccelTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "X-Acceleration(2)", darken( RotationColorScheme.AX_COLOR ), "ax", ACCEL_UNITS, b1.getXAccelVariable(), b1.getXAccelTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1, false );
        aGraph.addSeriesPair( "Y-Acceleration",
                              new ControlGraphSeries( "Y-Acceleration", RotationColorScheme.AY_COLOR, "ay", ACCEL_UNITS, b0.getYAccelVariable(), b0.getYAccelTimeSeries(), body0Stroke, CHARACTER_LADY ),
                              new ControlGraphSeries( "Y-Acceleration(2)", darken( RotationColorScheme.AY_COLOR ), "ay", ACCEL_UNITS, b1.getYAccelVariable(), b1.getYAccelTimeSeries(), body1Stroke, CHARACTER_BEETLE ),
                              b0, b1, false );

        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, xGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, angAccelGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, vGraph} );
        addGraphSuite( new RotationMinimizableControlGraph[]{angleGraph, angVelGraph, aGraph} );

        addSeriesSelectionPanels();

        b1.addListener( new RotationBody.Adapter() {
            public void platformStateChanged() {
                updateBody1Series();
            }
        } );
        updateBody1Series();
    }

    private PImage createThumb( Color color ) {
        return new PImage( ColorArrows.createArrow( color ) );
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
                JPanel p = new VerticalLayoutPanel();
                RotationSeriesSelectionPanel selectionPanel = new RotationSeriesSelectionPanel( graph.getRotationControlGraph() );
                ShadowJLabel titleGraphic = new ShadowJLabel( graph.getRotationControlGraph().getTitle(),
                                                              graph.getControlGraph().getControlGraphSeries( 0 ).getColor(),
                                                              GraphControlSeriesNode.LABEL_FONT
                );
                p.add( titleGraphic );
                p.add( selectionPanel );
                graphs[i].getControlGraph().addControl( p );
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
        RotationGraphSet rotationGraphSet = new RotationGraphSet( new PhetPCanvas(), new RotationModel( new ConstantDtClock( 30, 1 ) ), new AngleUnitModel( false ) );
        MinimizableControlGraph[] graphs = rotationGraphSet.getAllGraphs();
        System.out.println( "graphs.length = " + graphs.length );
        System.out.println( "Arrays.asList( graphs ) = " + Arrays.asList( graphs ) );
    }
}
