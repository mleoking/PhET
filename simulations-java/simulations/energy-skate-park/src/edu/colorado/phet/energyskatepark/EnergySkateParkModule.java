/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.model.*;
import edu.colorado.phet.energyskatepark.plots.BarGraphCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyPositionPlotCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyVsTimePlot;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.WiggleMeInSpace;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTimePanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 */

public class EnergySkateParkModule extends PiccoloModule {
    private EnergySkateParkModel energyModel;
    private EnergySkateParkSimulationPanel energyCanvas;
    private EnergyLookAndFeel energyLookAndFeel = new EnergyLookAndFeel();
    private JDialog barChartFrame;
    private double floorY = 0.0;
    private EnergySkateParkRecordableModel energyTimeSeriesModel;
    private SkaterCharacterSet skaterCharacterSet = new SkaterCharacterSet();

    private JDialog energyPositionPlotFrame;
    private EnergyPositionPlotCanvas energyPositionCanvas;
    private PhetFrame phetFrame;

    private BarGraphCanvas barGraphCanvas;
    private EnergySkateParkControlPanel energySkateParkControlPanel;
    private TimeSeriesModel timeSeriesModel;
    private EnergyVsTimePlot energyVsTimePlot;
    private EnergySkateParkOptions options;
    private ArrayList listeners = new ArrayList();

    private static final boolean DEFAULT_BAR_CHARTS_VISIBLE = false;
    private static final boolean DEFAULT_PLOT_VISIBLE = false;

    public EnergySkateParkModule( String name, Clock clock, PhetFrame phetFrame, EnergySkateParkOptions options ) {
        super( name, clock );
        this.options = options;
        this.phetFrame = phetFrame;
        energyModel = new EnergySkateParkModel( floorY );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EnergySkateParkRecordableModel( getEnergySkateParkModel() );
        timeSeriesModel = new TimeSeriesModel( energyTimeSeriesModel, clock );
        timeSeriesModel.setMaxRecordTime( EnergyVsTimePlot.MAX_TIME );
        clock.addClockListener( timeSeriesModel );

        energyCanvas = new EnergySkateParkSimulationPanel( this );
        setSimulationPanel( energyCanvas );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );

        barChartFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.bar-graph" ), false );
        barGraphCanvas = new BarGraphCanvas( this );
        barChartFrame.setContentPane( barGraphCanvas );

        barChartFrame.setSize( 200, 625 );
        barChartFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - barChartFrame.getWidth(), 0 );

        energyVsTimePlot = new EnergyVsTimePlot( phetFrame, clock, energyModel, timeSeriesModel );
        energyVsTimePlot.addListener( new EnergyVsTimePlot.Listener() {
            public void visibilityChanged() {
                setRecordOrLiveMode();
            }
        } );

        init();
        energyPositionPlotFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.energy-vs-position" ), false );
        energyPositionCanvas = new EnergyPositionPlotCanvas( this );
        energyPositionPlotFrame.setContentPane( energyPositionCanvas );
        energyPositionPlotFrame.setSize( 400, 400 );

        EnergySkateParkTimePanel timePanel = new EnergySkateParkTimePanel( this, timeSeriesModel, clock );
        getModulePanel().setClockControlPanel( timePanel );

        setDefaults();
        setLogoPanelVisible( Toolkit.getDefaultToolkit().getScreenSize().height > 768 );
        new WiggleMeInSpace( this ).start();
    }

    private void setDefaults() {
        setBarChartVisible( DEFAULT_BAR_CHARTS_VISIBLE );
        setEnergyTimePlotVisible( DEFAULT_PLOT_VISIBLE );
    }

    public EnergySkateParkModel getEnergySkateParkModel() {
        return energyModel;
    }

    public EnergyLookAndFeel getEnergyLookAndFeel() {
        return energyLookAndFeel;
    }

    public EnergySkateParkSimulationPanel getEnergySkateParkSimulationPanel() {
        return energyCanvas;
    }

    public void reset() {
        energyModel.reset();
        energyCanvas.reset();
        timeSeriesModel.reset();
        timeSeriesModel.setLiveMode();
        energyVsTimePlot.reset();
        init();
        timeSeriesModel.startLiveMode();
        barGraphCanvas.reset();
    }

    public void resetSkater() {
        if( getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = getEnergySkateParkModel().getBody( 0 );
            returnSkater( body );
        }
    }

    public void returnSkater( Body body ) {
        body.reset();
        if( !body.isRestorePointSet() ) {
            initBodyOnTrack( body );
        }
    }

    private void init() {
        final Body body = energyModel.createBody();
        energyModel.addBody( body );
        energyModel.addSplineSurface( createDefaultTrack() );
        initBodyOnTrack( body );
    }

    private EnergySkateParkSpline createDefaultTrack() {
        return new EnergySkateParkSpline( new PreFabSplines().getParabolic().getControlPoints() );
    }

    private void initBodyOnTrack( Body body ) {
        if( isTrackDefaultState() ) {
            body.setSpline( energyModel.getSpline( 0 ), false, 0.1 );
            body.clearHeat();
            body.clearEnergyError();
        }
    }

    private boolean isTrackDefaultState() {
        if( energyModel.getNumSplines() > 0 ) {
            if( energyModel.getSpline( 0 ).equals( createDefaultTrack() ) ) {
                return true;
            }
        }
        return false;
    }

    public TimeSeriesModel getTimeSeriesModel() {
        return timeSeriesModel;
    }

    public void setRecordPath( boolean selected ) {
        this.getEnergySkateParkModel().setRecordPath( selected );
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
        this.getEnergySkateParkModel().clearHistory();
    }

    public void setEnergyTimePlotVisible( boolean b ) {
        energyVsTimePlot.setVisible( b );
    }

    public void setBarChartVisible( boolean b ) {
        barChartFrame.setVisible( b );
    }

    public void setCoefficientOfFriction( double value ) {
        for( int i = 0; i < getEnergySkateParkModel().getNumBodies(); i++ ) {
            Body body = getEnergySkateParkModel().getBody( i );
            body.setFrictionCoefficient( value );
        }
    }

    public void clearHeat() {
        getEnergySkateParkModel().clearHeat();
    }

    public void setEnergyPositionPlotVisible( boolean b ) {
        energyPositionCanvas.reset();
        energyPositionPlotFrame.setVisible( b );
    }

    public void setBounciness( double bounciness ) {
        EnergySkateParkModel model = getEnergySkateParkModel();
        for( int i = 0; i < model.getNumBodies(); i++ ) {
            Body b = model.getBody( i );
            b.setBounciness( bounciness );
        }
    }

    public void confirmAndReset() {
        int response = JOptionPane.showConfirmDialog( getSimulationPanel(), EnergySkateParkStrings.getString( "message.confirm-reset" ), EnergySkateParkStrings.getString( "message.confirm-reset-title" ), JOptionPane.YES_NO_OPTION );
        if( response == JOptionPane.OK_OPTION ) {
            reset();
        }
    }

    public Planet[] getPlanets() {
        return new Planet[]{new Planet.Space(), new Planet.Moon(), new Planet.Earth(), new Planet.Jupiter()};
    }

    public Frame getPhetFrame() {
        return phetFrame;
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        energyModel.setSkaterCharacter( skaterCharacter );
        notifySkaterCharacterChanged();
    }

    public SkaterCharacter getSkaterCharacter() {
        return energyModel.getSkaterCharacter();
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return skaterCharacterSet.getSkaterCharacters();
    }

    public void setEnergyErrorVisible( boolean selected ) {
        energyCanvas.setEnergyErrorVisible( selected );
    }

    public boolean isEnergyErrorVisible() {
        return energyCanvas.isEnergyErrorVisible();
    }

    public void showNewEnergyVsTimePlot() {
        energyVsTimePlot.setVisible( true );
    }

    public EnergySkateParkOptions getOptions() {
        return options;
    }

    private boolean isEnergyVsTimeGraphVisible() {
        return energyVsTimePlot.isVisible();
    }

    public void setRecordOrLiveMode() {
        if( isEnergyVsTimeGraphVisible() ) {
            timeSeriesModel.setRecordMode();
        }
        else {
            timeSeriesModel.setLiveMode();
        }
    }

    public void removeListener( Listener moduleListener ) {
        listeners.remove( moduleListener );
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private void notifySkaterCharacterChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.skaterCharacterChanged();
        }
    }

    public Body createBody() {
        return energyModel.createBody();
    }

    public static interface Listener {
        void skaterCharacterChanged();
    }
}
