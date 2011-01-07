// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.rotation.RotationStrings;
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
    private String ANGLE_UNITS_RAD = RotationStrings.RADIANS;
    private String ANGLE_UNITS_DEG = RotationStrings.DEGREES;
    private static final String ANG_VEL_UNITS_RAD = RotationStrings.ANG_VEL_ABBR;
    private static final String ANG_VEL_UNITS_DEG = RotationStrings.ANG_VEL_DEG_ABBR;
    private static final String ANG_ACC_UNITS_RAD = RotationStrings.ANG_ACC_ABBR;
    private static final String ANG_ACC_UNITS_DEG = RotationStrings.ANG_ACC_DEG_ABBR;
    private static final String POSITION_UNITS = RotationStrings.METERS_ABBR;
    private static final String VELOCITY_UNITS = RotationStrings.VELOCITY_ABBR;
    private static final String ACCEL_UNITS = RotationStrings.ACCEL_ABBR;

    private static final String CHARACTER_PLATFORM = RotationStrings.getString( "object.platform" );
    private static final String CHARACTER_LADY = RotationStrings.getString( "object.ladybug" );
    private static final String CHARACTER_BEETLE = RotationStrings.getString( "object.beetle" );
    private PhetPCanvas pSwingCanvas;
    private AngleUnitModel angleUnitModel;
    private RotationBody b0;
    private RotationBody b1;
    public static final int MAX_ANG_VEL = 5;
    public static final int MIN_ANG_VEL = -5;
    private static final String ANG_ACCEL = RotationStrings.getString( "variable.angular.acceleration" );
    private final String ANG_VEL = RotationStrings.getString( "variable.angular.velocity" );
    private final String ANGLE = RotationStrings.getString( "variable.angle" );
    private final String PLATFORM_ANG_ACCEL = RotationStrings.getString( "variable.platform.ang.accel" );

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
        RotationMinimizableControlGraph aGraph = new RotationMinimizableControlGraph( RotationStrings.ACCELERATION_ABBR, new RotationGraph(
                pSwingCanvas, null, RotationStrings.twoChar( "ax" ), RotationStrings.ACCELERATION, ACCEL_UNITS, -1 / 0.03 / 0.03 * 3.0 / 200.0, 1 / 0.03 / 0.03 * 3.0 / 200.0,
                false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );

        aGraph.addSeriesPair( RotationStrings.abs( RotationStrings.ACCELERATION ),
                              new ControlGraphSeries( RotationStrings.abs( RotationStrings.ACCELERATION ), RotationColorScheme.AM_COLOR, RotationStrings.ACCELERATION_ABBR, ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelMagnitude() ),
                              new ControlGraphSeries( RotationStrings.abs( RotationStrings.ACCELERATION ) + "(2)", darken( RotationColorScheme.AM_COLOR ), RotationStrings.ACCELERATION_ABBR, ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelMagnitude() ),
                              b0, b1 );
        aGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.ACCELERATION ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.ACCELERATION ), RotationColorScheme.AX_COLOR, RotationStrings.twoChar( "ax" ), ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelX() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.ACCELERATION ) + "(2)", darken( RotationColorScheme.AX_COLOR ), RotationStrings.twoChar( "ax" ), ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelX() ),
                              b0, b1, false );
        aGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.ACCELERATION ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.ACCELERATION ), RotationColorScheme.AY_COLOR, RotationStrings.twoChar( "ay" ), ACCEL_UNITS, body0Stroke, CHARACTER_LADY, b0.getAccelY() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.ACCELERATION ) + "(2)", darken( RotationColorScheme.AY_COLOR ), RotationStrings.twoChar( "ay" ), ACCEL_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getAccelY() ),
                              b0, b1, false );
        return aGraph;
    }

    protected RotationMinimizableControlGraph createVGraph() {
        final RotationMinimizableControlGraph vGraph = new RotationMinimizableControlGraph( RotationStrings.V, new RotationGraph(
                pSwingCanvas, null, RotationStrings.V + "" + RotationStrings.X, RotationStrings.VELOCITY, VELOCITY_UNITS, -15, +15,
                false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        vGraph.addSeriesPair( RotationStrings.SPEED,
                              new ControlGraphSeries( RotationStrings.SPEED, RotationColorScheme.VM_COLOR, RotationStrings.abs( RotationStrings.V ), VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getSpeed() ),
                              new ControlGraphSeries( RotationStrings.SPEED + "(2)", darken( RotationColorScheme.VM_COLOR ), RotationStrings.abs( RotationStrings.V ), VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getSpeed() )
                , b0, b1 );
        vGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.VELOCITY ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.VELOCITY ), RotationColorScheme.VX_COLOR, RotationStrings.twoChar( "vx" ), VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getVx() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.VELOCITY ) + "(2)", darken( RotationColorScheme.VX_COLOR ), RotationStrings.twoChar( "vx" ), VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getVx() ),
                              b0, b1, false );
        vGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.VELOCITY ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.VELOCITY ), RotationColorScheme.VY_COLOR, RotationStrings.twoChar( "vy" ), VELOCITY_UNITS, body0Stroke, CHARACTER_LADY, b0.getVy() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.VELOCITY ) + "(2)", darken( RotationColorScheme.VY_COLOR ), RotationStrings.twoChar( "vy" ), VELOCITY_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getVy() ),
                              b0, b1, false );
        return vGraph;
    }

    protected RotationMinimizableControlGraph createXGraph() {
        RotationMinimizableControlGraph xGraph = new RotationMinimizableControlGraph( RotationStrings.X + " & " + RotationStrings.Y, new RotationGraph(
                pSwingCanvas, null, RotationStrings.X, RotationStrings.POSITION, POSITION_UNITS, -5, 5,
                false, model.getTimeSeriesModel(), null, RotationModel.MAX_TIME, null ) );
        xGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.POSITION ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.POSITION ), RotationColorScheme.X_COLOR, RotationStrings.X, POSITION_UNITS, body0Stroke, CHARACTER_LADY, b0.getPositionX() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.POSITION ) + "(2)", darken( RotationColorScheme.X_COLOR ), RotationStrings.X, POSITION_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getPositionX() ),
                              b0, b1 );
        xGraph.addSeriesPair( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.POSITION ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.Y, RotationStrings.POSITION ), RotationColorScheme.Y_COLOR, RotationStrings.Y, POSITION_UNITS, body0Stroke, CHARACTER_LADY, b0.getPositionY() ),
                              new ControlGraphSeries( RotationStrings.formatForChart( RotationStrings.X, RotationStrings.POSITION ) + "(2)", darken( RotationColorScheme.Y_COLOR ), RotationStrings.Y, POSITION_UNITS, body1Stroke, CHARACTER_BEETLE, b1.getPositionY() ),
                              b0, b1 );
        return xGraph;
    }

    protected RotationMinimizableControlGraph createAngAccelGraph( boolean editable ) {
        final ControlGraphSeries platformAccelSeries = new ControlGraphSeries( PLATFORM_ANG_ACCEL, RotationColorScheme.ANG_ACC_SUITE.getPlatformColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, platformStroke, CHARACTER_PLATFORM, model.getRotationPlatform().getAngularAcceleration(), editable );

        RotationMinimizableControlGraph angAccelGraph = new RotationMinimizableControlGraph( UnicodeUtil.ALPHA, new AngularUnitGraph(
                pSwingCanvas, platformAccelSeries,
                UnicodeUtil.ALPHA, ANG_ACCEL, angleUnitModel, ANG_ACC_UNITS_RAD, ANG_ACC_UNITS_DEG, -1.1, 1.1,
                model, editable, model.getTimeSeriesModel(), model.getAccelDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );

        //angAccelGraph.addSeries( platformAccelSeries );
        angAccelGraph.addSeriesPair( ANG_ACCEL,
                                     new ControlGraphSeries( ANG_ACCEL, RotationColorScheme.ANG_ACC_SUITE.getLadybugColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngularAcceleration() ),
                                     new ControlGraphSeries( ANG_ACCEL + "(2) ", RotationColorScheme.ANG_ACC_SUITE.getBeetleColor(), UnicodeUtil.ALPHA, ANG_ACC_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngularAcceleration() ), b0, b1 );

        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformAccelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );
        return angAccelGraph;
    }

    protected RotationMinimizableControlGraph createAngVelGraph() {
        final ControlGraphSeries platformVelSeries = new ControlGraphSeries( ANG_VEL, RotationColorScheme.ANG_VEL_SUITE.getPlatformColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, platformStroke, CHARACTER_PLATFORM, model.getRotationPlatform().getAngularVelocity(), true );

        RotationMinimizableControlGraph angVelGraph = new RotationMinimizableControlGraph( UnicodeUtil.OMEGA, new AngularUnitGraph(
                pSwingCanvas, platformVelSeries,
                UnicodeUtil.OMEGA, ANG_VEL, angleUnitModel, ANG_VEL_UNITS_RAD, ANG_VEL_UNITS_DEG, MIN_ANG_VEL, MAX_ANG_VEL,
                model, true, model.getTimeSeriesModel(), model.getVelocityDriven(), RotationModel.MAX_TIME, model.getRotationPlatform() ) );

        //angVelGraph.addSeries( platformVelSeries );
        angVelGraph.addSeriesPair( ANG_VEL,
                                   new ControlGraphSeries( ANG_VEL, RotationColorScheme.ANG_VEL_SUITE.getLadybugColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngularVelocity() ),
                                   new ControlGraphSeries( ANG_VEL + "(2)", RotationColorScheme.ANG_VEL_SUITE.getBeetleColor(), UnicodeUtil.OMEGA, ANG_VEL_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngularVelocity() ),
                                   b0, b1 );
        model.getRotationPlatform().addListener( new RotationPlatform.Adapter() {
            public void displayGraphChanged() {
                platformVelSeries.setVisible( model.getRotationPlatform().getDisplayGraph() );
            }
        } );
        return angVelGraph;
    }

    protected RotationMinimizableControlGraph createAngleGraph() {
        final ControlGraphSeries platformAngleSeries = new ControlGraphSeries( ANGLE, RotationColorScheme.ANGLE_SUITE.getPlatformColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, platformStroke, CHARACTER_PLATFORM, model.getRotationPlatform().getAngle(), true );
        final RotationMinimizableControlGraph angleGraph = new RotationMinimizableControlGraph( UnicodeUtil.THETA, new AngularUnitGraph(
                pSwingCanvas, platformAngleSeries,
                UnicodeUtil.THETA, ANGLE, angleUnitModel, ANGLE_UNITS_RAD, ANGLE_UNITS_DEG, -Math.PI * 3, Math.PI * 3,
                model, true, model.getTimeSeriesModel(), model.getPositionDriven(),
                RotationModel.MAX_TIME, model.getRotationPlatform() ) );
        angleGraph.addSeriesPair( ANGLE,
                                  new ControlGraphSeries( ANGLE, RotationColorScheme.ANGLE_SUITE.getLadybugColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, body0Stroke, CHARACTER_LADY, b0.getAngle() ),
                                  new ControlGraphSeries( ANGLE + "(2)", RotationColorScheme.ANGLE_SUITE.getBeetleColor(), UnicodeUtil.THETA, ANGLE_UNITS_RAD, body1Stroke, CHARACTER_BEETLE, b1.getAngle() ),
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
