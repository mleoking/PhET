/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.ec3.model.Body;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.model.spline.AbstractSpline;
import edu.colorado.phet.ec3.model.spline.CubicSpline;
import edu.colorado.phet.ec3.model.spline.SplineSurface;
import edu.colorado.phet.ec3.plots.BarGraphCanvas;
import edu.colorado.phet.ec3.plots.EnergyPositionPlotCanvas;
import edu.colorado.phet.ec3.plots.EnergyTimePlotCanvas;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.timeseries.TimeSeriesModel;
import edu.colorado.phet.timeseries.TimeSeriesPlaybackPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EC3Module extends PiccoloModule {
    private EnergyConservationModel energyModel;
    private EC3Canvas energyCanvas;
    private EnergyLookAndFeel energyLookAndFeel = new EnergyLookAndFeel();
    private JDialog barChartFrame;
    private double floorY = 0.0;
    private TimeSeriesPlaybackPanel timeSeriesPlaybackPanel;
    private EC3TimeSeriesModel energyTimeSeriesModel;
    private JDialog chartFrame;
    private EnergyTimePlotCanvas energyTimePlotCanvas;
    public static final int energyFrameWidth = 200;
    public static final int chartFrameHeight = 200;
    private static final boolean DEFAULT_BAR_CHARTS_VISIBLE = false;
    private static final boolean DEFAULT_PLOT_VISIBLE = false;
    private Point2D.Double defaultBodyPosition = new Point2D.Double( 5, 5 );
    private JDialog energyPositionPlotFrame;
    private EnergyPositionPlotCanvas energyPositionCanvas;
    private PhetFrame phetFrame;

    /**
     * @param name
     * @param clock
     * @param phetFrame
     */
    public EC3Module( String name, IClock clock, PhetFrame phetFrame ) {
        super( name, clock );
        this.phetFrame = phetFrame;
//        clock.setTimeScalingConverter();
        energyModel = new EnergyConservationModel( floorY + 10 );

        addFloorSpline();

        Floor floor = new Floor( getEnergyConservationModel(), -AbstractSpline.SPLINE_THICKNESS );
        energyModel.addFloor( floor );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EC3TimeSeriesModel( this );
        clock.addClockListener( energyTimeSeriesModel );

        energyCanvas = new EC3Canvas( this );
        setSimulationPanel( energyCanvas );

        EC3ControlPanel EC3ControlPanel = new EC3ControlPanel( this );
        setControlPanel( EC3ControlPanel );

        barChartFrame = new JDialog( phetFrame, "Bar Charts", false );
        barChartFrame.setContentPane( new BarGraphCanvas( this ) );

        barChartFrame.setSize( energyFrameWidth, 600 );
        barChartFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - energyFrameWidth, 0 );

        chartFrame = new JDialog( phetFrame, "Energy vs. Time", false );
        energyTimePlotCanvas = new EnergyTimePlotCanvas( this );
        chartFrame.setContentPane( energyTimePlotCanvas );
        chartFrame.setSize( 800, chartFrameHeight );
        chartFrame.setLocation( 0, Toolkit.getDefaultToolkit().getScreenSize().height - chartFrame.getHeight() - 100 );

        init();
        timeSeriesPlaybackPanel = new TimeSeriesPlaybackPanel( energyTimeSeriesModel );
        energyPositionPlotFrame = new JDialog( phetFrame, "Energy vs. Position", false );
        energyPositionCanvas = new EnergyPositionPlotCanvas( this );
        energyPositionPlotFrame.setContentPane( energyPositionCanvas );
        energyPositionPlotFrame.setSize( 400, 400 );

//        new AutoPan( energyCanvas, this );
        getModulePanel().setClockControlPanel( timeSeriesPlaybackPanel );
    }

    private void addFloorSpline() {
        CubicSpline floorSpline = new CubicSpline( 4 );
        floorSpline.addControlPoint( -1000, 0 );
        floorSpline.addControlPoint( 1000, 0 );
//        floorSpline.addControlPoint( -1000, -AbstractSpline.SPLINE_THICKNESS/2+AbstractSpline.SPLINE_THICKNESS/10 );
//        floorSpline.addControlPoint( 1000, -AbstractSpline.SPLINE_THICKNESS/2+AbstractSpline.SPLINE_THICKNESS/10 );

        SplineSurface splineSurface = new SplineSurface( floorSpline );
        splineSurface.setInteractive( false );
        energyModel.addSplineSurface( splineSurface );
    }

    private void setDefaults() {
        setBarChartVisible( DEFAULT_BAR_CHARTS_VISIBLE );
        setEnergyTimePlotVisible( DEFAULT_PLOT_VISIBLE );
    }

    public void stepModel( double dt ) {
        energyModel.stepInTime( dt );
    }

    public void activate() {
        super.activate();
        barChartFrame.setVisible( true );
        chartFrame.setVisible( true );
//        getPhetFrame().getBasicPhetPanel().setAppControlPanel( timeSeriesPlaybackPanel );
        setDefaults();
    }

    public void deactivate() {
        super.deactivate();
        barChartFrame.setVisible( false );
        chartFrame.setVisible( false );
//        getPhetFrame().getBasicPhetPanel().setAppControlPanel( new JLabel( "This space for rent." ) );
    }

    private PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public EnergyConservationModel getEnergyConservationModel() {
        return energyModel;
    }

    public EnergyLookAndFeel getEnergyLookAndFeel() {
        return energyLookAndFeel;
    }

    public EC3Canvas getEnergyConservationCanvas() {
        return energyCanvas;
    }

    public void reset() {
        energyModel.reset();
        energyCanvas.reset();
        energyTimeSeriesModel.reset();
        energyTimeSeriesModel.setLiveMode();
        energyTimePlotCanvas.reset();
        init();
        addFloorSpline();
        energyTimeSeriesModel.startLiveMode();

    }


    public void resetSkater() {
        if( getEnergyConservationModel().numBodies() > 0 ) {
            Body body = getEnergyConservationModel().bodyAt( 0 );
            resetSkater( body );
        }
    }

    public void resetSkater( Body body ) {
        body.setPosition( getDefaultBodyPosition() );
        body.setAngle( getDefaultBodyAngle() );
        body.resetMode();
        body.setVelocity( 0, 0 );
    }

    private double getDefaultBodyAngle() {
        return Math.PI;
    }

    private void init() {
        Body body = new Body( Body.createDefaultBodyRect() );
        body.setPosition( getDefaultBodyPosition() );
        energyModel.addBody( body );

        for( int i = 0; i < energyModel.numBodies(); i++ ) {
            BodyGraphic bodyGraphic = new BodyGraphic( this, energyModel.bodyAt( i ) );
            energyCanvas.addBodyGraphic( bodyGraphic );
        }

        PreFabSplines preFabSplines = new PreFabSplines();
//        CubicSpline spline = preFabSplines.getParabolic();
        CubicSpline spline = preFabSplines.getTinyParabolic();
//        CubicSpline spline = preFabSplines.getBugDonut();
//        CubicSpline spline = preFabSplines.getLoop();
//        CubicSpline spline = preFabSplines.getTightParabolic();

        SplineSurface surface = new SplineSurface( spline );
        SplineGraphic splineGraphic = new SplineGraphic( energyCanvas, surface );
        energyModel.addSplineSurface( surface );
        energyCanvas.addSplineGraphic( splineGraphic );
        energyCanvas.initPieGraphic();
    }

    private Point2D getDefaultBodyPosition() {
        return defaultBodyPosition;
    }

    public Object getModelState() {
        return energyModel.copyState();
    }

    public void setState( EnergyConservationModel model ) {
        energyModel.setState( model );
        redrawAllGraphics();
    }

    private void redrawAllGraphics() {
        energyCanvas.redrawAllGraphics();
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return energyTimeSeriesModel;
    }

    public void setRecordPath( boolean selected ) {
        this.getEnergyConservationModel().setRecordPath( selected );
    }

    public boolean isMeasuringTapeVisible() {
        return energyCanvas.isMeasuringTapeVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        energyCanvas.setMeasuringTapeVisible( selected );
    }

    public boolean isPieChartVisible() {
        return energyCanvas.isPieChartVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        energyCanvas.setPieChartVisible( selected );
    }

    public void clearPaths() {
        this.getEnergyConservationModel().clearPaths();
    }

    public void setEnergyTimePlotVisible( boolean b ) {
        chartFrame.setVisible( b );
        if( b ) {
            getTimeSeriesModel().startRecording();
        }
    }

    public void setBarChartVisible( boolean b ) {
        barChartFrame.setVisible( b );
    }

    public void setCoefficientOfFriction( double value ) {
        for( int i = 0; i < getEnergyConservationModel().numBodies(); i++ ) {
            Body body = getEnergyConservationModel().bodyAt( i );
            body.setFrictionCoefficient( value );
        }
    }

    public void clearHeat() {
        getEnergyConservationModel().clearHeat();
    }

    public void setEnergyPositionPlotVisible( boolean b ) {
        energyPositionCanvas.reset();
        energyPositionPlotFrame.setVisible( b );
    }

    public void setCoefficientOfRestitution( double rest ) {
        EnergyConservationModel model = getEnergyConservationModel();
        for( int i = 0; i < model.numBodies(); i++ ) {
            Body b = model.bodyAt( i );
            b.setCoefficientOfRestitution( rest );
        }
    }

    public void confirmAndReset() {
        int response = JOptionPane.showConfirmDialog( getSimulationPanel(), "Are you sure you want to reset?" );
        if( response == JOptionPane.OK_OPTION || response == JOptionPane.YES_OPTION ) {
            reset();
        }
    }
}
