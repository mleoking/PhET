package edu.colorado.phet.rotation.graphs;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.colorado.phet.rotation.util.UnicodeUtil;
import edu.colorado.phet.rotation.view.RotationColorScheme;

/**
 * User: Sam Reid
 * Date: Dec 28, 2006
 * Time: 8:23:39 AM
 */
public abstract class AbstractRotationGraphSet extends GraphSuiteSet {
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
    private PhetPCanvas pSwingCanvas;
    private AngleUnitModel angleUnitModel;
    private RotationBody b0;
    private RotationBody b1;
    public static final int MAX_ANG_VEL = 5;
    public static final int MIN_ANG_VEL = -5;


    public AbstractRotationGraphSet( PhetPCanvas pSwingCanvas, final RotationModel model, AngleUnitModel angleUnitModel ) {
        this.pSwingCanvas = pSwingCanvas;
        this.angleUnitModel = angleUnitModel;
        this.model = model;

        b0 = model.getRotationBody( 0 );
        b1 = model.getRotationBody( 1 );
        b1.addListener( new RotationBody.Adapter() {
            public void platformStateChanged() {
                updateBody1Series();
            }
        } );
        updateBody1Series();
    }

    protected RotationMinimizableControlGraph createAGraph() {
        RotationMinimizableControlGraph aGraph = new RotationMinimizableControlGraph( "a", new RotationGraph(
                pSwingCanvas, null, "ax", "Acceleration", ACCEL_UNITS, -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0,
                model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );

        aGraph.addSeriesPair( "|Acceleration|",
                              new ControlGraphSeries( "|Acceleration|", RotationColorScheme.AM_COLOR, "a", ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelMagnitude() ),
                              new ControlGraphSeries( "|Acceleration|(2)", darken( RotationColorScheme.AM_COLOR ), "a", ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelMagnitude() ),
                              b0, b1 );
        aGraph.addSeriesPair( "X-Acceleration",
                              new ControlGraphSeries( "X-Acceleration", RotationColorScheme.AX_COLOR, "ax", ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelX() ),
                              new ControlGraphSeries( "X-Acceleration(2)", darken( RotationColorScheme.AX_COLOR ), "ax", ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelX() ),
                              b0, b1, false );
        aGraph.addSeriesPair( "Y-Acceleration",
                              new ControlGraphSeries( "Y-Acceleration", RotationColorScheme.AY_COLOR, "ay", ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelY() ),
                              new ControlGraphSeries( "Y-Acceleration(2)", darken( RotationColorScheme.AY_COLOR ), "ay", ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelY() ),
                              b0, b1, false );
        return aGraph;
    }

    protected RotationMinimizableControlGraph createVGraph() {
        final RotationMinimizableControlGraph vGraph = new RotationMinimizableControlGraph( "v", new RotationGraph(
                pSwingCanvas, null, "vx", "Velocity", VELOCITY_UNITS, -15, +15,
                model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        vGraph.addSeriesPair( "Speed",
                              new ControlGraphSeries( "Speed", RotationColorScheme.VM_COLOR, "|v|", VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getSpeed() ),
                              new ControlGraphSeries( "Speed(2)", darken( RotationColorScheme.VM_COLOR ), "|v|", VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getSpeed() )
                , b0, b1 );
        vGraph.addSeriesPair( "X-Velocity",
                              new ControlGraphSeries( "X-Velocity", RotationColorScheme.VX_COLOR, "vx", VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getVx() ),
                              new ControlGraphSeries( "X-Velocity(2)", darken( RotationColorScheme.VX_COLOR ), "vx", VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getVx() ),
                              b0, b1, false );
        vGraph.addSeriesPair( "Y-Velocity",
                              new ControlGraphSeries( "Y-Velocity", RotationColorScheme.VY_COLOR, "vy", VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getVy() ),
                              new ControlGraphSeries( "Y-Velocity(2)", darken( RotationColorScheme.VY_COLOR ), "vy", VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getVy() ),
                              b0, b1, false );
        return vGraph;
    }

    protected RotationMinimizableControlGraph createXGraph() {
        RotationMinimizableControlGraph xGraph = new RotationMinimizableControlGraph( "x & y", new RotationGraph(
                pSwingCanvas, null, "x", "Position", POSITION_UNITS, -5, 5,
                model, false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        xGraph.addSeriesPair( "X-Position",
                              new ControlGraphSeries( "X-Position", RotationColorScheme.X_COLOR, "x", POSITION_UNITS, body0Stroke, CHARACTER_LADY, b0.getPositionX() ),
                              new ControlGraphSeries( "X-Position(2)", darken( RotationColorScheme.X_COLOR ), "x", POSITION_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getPositionX() ),
                              b0, b1 );
        xGraph.addSeriesPair( "Y-Position",
                              new ControlGraphSeries( "Y-Position", RotationColorScheme.Y_COLOR, "y", POSITION_UNITS, body0Stroke, CHARACTER_LADY, b0.getPositionY() ),
                              new ControlGraphSeries( "Y-Position(2)", darken( RotationColorScheme.Y_COLOR ), "y", POSITION_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getPositionY() ),
                              b0, b1 );
        return xGraph;
    }

    protected RotationMinimizableControlGraph createAngAccelGraph( boolean editable ) {
        final ControlGraphSeries platformAccelSeries = new ControlGraphSeries( "Platform Ang Accel", RotationColorScheme.ANG_ACC_SUITE.getPlatformColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, platformStroke, editable, CHARACTER_PLATFORM, model.getRotationPlatform().getAngularAcceleration() );
        RotationMinimizableControlGraph angAccelGraph = new RotationMinimizableControlGraph( UnicodeUtil.ALPHA, new AngularUnitGraph(
                pSwingCanvas, platformAccelSeries,
                UnicodeUtil.ALPHA, "Angular Acceleration", angleUnitModel, ANG_ACC_UNITS_RAD, ANG_ACC_UNITS_DEG, -1.1, 1.1,
                model, editable, model.getTimeSeriesModel(), model.getAccelDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );

        //angAccelGraph.addSeries( platformAccelSeries );
        angAccelGraph.addSeriesPair( "Angular Acceleration",
                                     new ControlGraphSeries( "Angular Acceleration", RotationColorScheme.ANG_ACC_SUITE.getLadybugColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngularAcceleration() ),
                                     new ControlGraphSeries( "Angular Acceleration (2) ", RotationColorScheme.ANG_ACC_SUITE.getBeetleColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngularAcceleration() ), b0, b1 );

        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformAccelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );
        return angAccelGraph;
    }

    protected RotationMinimizableControlGraph createAngVelGraph() {
        final ControlGraphSeries platformVelSeries = new ControlGraphSeries( "Angular Velocity", RotationColorScheme.ANG_VEL_SUITE.getPlatformColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, platformStroke, true, CHARACTER_PLATFORM, model.getRotationPlatform().getAngularVelocity() );

        RotationMinimizableControlGraph angVelGraph = new RotationMinimizableControlGraph( UnicodeUtil.OMEGA, new AngularUnitGraph(
                pSwingCanvas, platformVelSeries,
                UnicodeUtil.OMEGA, "Angular Velocity", angleUnitModel, ANG_VEL_UNITS_RAD, ANG_VEL_UNITS_DEG, MIN_ANG_VEL, MAX_ANG_VEL,
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );

        //angVelGraph.addSeries( platformVelSeries );
        angVelGraph.addSeriesPair( "Angular Velocity",
                                   new ControlGraphSeries( "Angular Velocity", RotationColorScheme.ANG_VEL_SUITE.getLadybugColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngularVelocity() ),
                                   new ControlGraphSeries( "Angular Velocity (2)", RotationColorScheme.ANG_VEL_SUITE.getBeetleColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngularVelocity() ),
                                   b0, b1 );
        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformVelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );
        return angVelGraph;
    }

    protected RotationMinimizableControlGraph createAngleGraph() {
        final ControlGraphSeries platformAngleSeries = new ControlGraphSeries( "Angle", RotationColorScheme.ANGLE_SUITE.getPlatformColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, platformStroke, true, CHARACTER_PLATFORM, model.getRotationPlatform().getAngle() );
        final RotationMinimizableControlGraph angleGraph = new RotationMinimizableControlGraph( UnicodeUtil.THETA, new AngularUnitGraph(
                pSwingCanvas, platformAngleSeries,
                UnicodeUtil.THETA, "Angle", angleUnitModel, ANGLE_UNITS_RAD, ANGLE_UNITS_DEG, -Math.PI * 3, Math.PI * 3,
                model, true, model.getTimeSeriesModel(), model.getPositionDriven(),
                RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        angleGraph.addSeriesPair( "Angle",
                                  new ControlGraphSeries( "Angle", RotationColorScheme.ANGLE_SUITE.getLadybugColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngle() ),
                                  new ControlGraphSeries( "Angle (2)", RotationColorScheme.ANGLE_SUITE.getBeetleColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngle() ),
                                  b0, b1 );
        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformAngleSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );
        return angleGraph;
    }

    private Color darken( Color color ) {
        return darken( color, 150 );
    }

    private Color darken( Color color, int amount ) {
        return new Color( Math.max( color.getRed() - amount, 0 ),
                          Math.max( color.getGreen() - amount, 0 ),
                          Math.max( color.getBlue() - amount, 0 ) );
    }

    protected void addSeriesSelectionPanels() {
        MinimizableControlGraph[] graphs = getAllGraphs();
        for ( int i = 0; i < graphs.length; i++ ) {
            //todo: only show one checkbox for both characters, and have it apply to both characters
            //we'll need to pair up the series pairs to facilitate this.
            RotationMinimizableControlGraph graph = (RotationMinimizableControlGraph) graphs[i];
            if ( graph.getRotationControlGraph().getSeriesPairCount() > 1 ) {
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

    protected void updateBody1Series() {
        for ( int i = 0; i < getAllGraphs().length; i++ ) {
            RotationMinimizableControlGraph rotationMinimizableControlGraph = (RotationMinimizableControlGraph) getAllGraphs()[i];
            ( (RotationGraph) rotationMinimizableControlGraph.getControlGraph() ).setSecondarySeriesVisible( model.getRotationBody( 1 ).isOnPlatform() );
        }
    }

    public PhetPCanvas getCanvas() {
        return pSwingCanvas;
    }

    public RotationModel getTorqueModel() {
        return model;
    }
}
