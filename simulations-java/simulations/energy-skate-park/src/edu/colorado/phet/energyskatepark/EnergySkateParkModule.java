/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.persistence.Point2DPersistenceDelegate;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.servicemanager.InputStreamFileContents;
import edu.colorado.phet.common.servicemanager.PhetServiceManager;
import edu.colorado.phet.energyskatepark.common.StringOutputStream;
import edu.colorado.phet.energyskatepark.model.*;
import edu.colorado.phet.energyskatepark.plots.BarGraphCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyPositionPlotCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyTimePlotCanvas;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkModuleBean;
import edu.colorado.phet.energyskatepark.timeseries.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.timeseries.TimeSeriesPlaybackPanel;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.WiggleMeInSpace;

import javax.jnlp.FileContents;
import javax.jnlp.FileOpenService;
import javax.jnlp.FileSaveService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 * Copyright (c) Sep 21, 2005 by Sam Reid
 */

public class EnergySkateParkModule extends PiccoloModule {
    private EnergySkateParkModel energyModel;
    private EnergySkateParkSimulationPanel energyCanvas;
    private EnergyLookAndFeel energyLookAndFeel = new EnergyLookAndFeel();
    private JDialog barChartFrame;
    private double floorY = 0.0;
    private TimeSeriesPlaybackPanel timeSeriesPlaybackPanel;
    private EC3TimeSeriesModel energyTimeSeriesModel;
    private JDialog chartFrame;
    private EnergyTimePlotCanvas energyTimePlotCanvas;

    private JDialog energyPositionPlotFrame;
    private EnergyPositionPlotCanvas energyPositionCanvas;
    private PhetFrame phetFrame;

    private BarGraphCanvas barGraphCanvas;
    private EnergySkateParkControlPanel energySkateParkControlPanel;

    private SkaterCharacterSet skaterCharacterSet = new SkaterCharacterSet();
    private SkaterCharacter skaterCharacter = skaterCharacterSet.getSkaterCharacters()[0];

    private ArrayList listeners = new ArrayList();

    public static final int energyFrameWidth = 200;
    public static final int chartFrameHeight = 250;

    private static final boolean DEFAULT_BAR_CHARTS_VISIBLE = false;
    private static final boolean DEFAULT_PLOT_VISIBLE = false;

    public EnergySkateParkModule( String name, IClock clock, PhetFrame phetFrame ) {
        super( name, clock );
        this.phetFrame = phetFrame;
        energyModel = new EnergySkateParkModel( floorY );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EC3TimeSeriesModel( this );
        clock.addClockListener( energyTimeSeriesModel );

        energyCanvas = new EnergySkateParkSimulationPanel( this );
        setSimulationPanel( energyCanvas );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                energySkateParkControlPanel.update();
            }
        } );

        barChartFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "bar.charts" ), false );
        barGraphCanvas = new BarGraphCanvas( this );
        barChartFrame.setContentPane( barGraphCanvas );

        barChartFrame.setSize( energyFrameWidth, 625 );
        barChartFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - energyFrameWidth, 0 );

        chartFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "energy.vs.time" ), false );
        energyTimePlotCanvas = new EnergyTimePlotCanvas( this );
        chartFrame.setContentPane( energyTimePlotCanvas );
        chartFrame.setSize( 800, chartFrameHeight );
        chartFrame.setLocation( 0, Toolkit.getDefaultToolkit().getScreenSize().height - chartFrame.getHeight() - 100 );

        init();
        timeSeriesPlaybackPanel = new TimeSeriesPlaybackPanel( energyTimeSeriesModel );
        energyPositionPlotFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "energy.vs.position" ), false );
        energyPositionCanvas = new EnergyPositionPlotCanvas( this );
        energyPositionPlotFrame.setContentPane( energyPositionCanvas );
        energyPositionPlotFrame.setSize( 400, 400 );

        getModulePanel().setClockControlPanel( timeSeriesPlaybackPanel );
        setDefaults();
        setLogoPanelVisible( Toolkit.getDefaultToolkit().getScreenSize().height > 768 );
        new WiggleMeInSpace( this ).start();
    }

    private void setDefaults() {
        setBarChartVisible( DEFAULT_BAR_CHARTS_VISIBLE );
        setEnergyTimePlotVisible( DEFAULT_PLOT_VISIBLE );
    }

    public void stepModel( double dt ) {
        energyModel.stepInTime( dt );
    }

    public EnergySkateParkModel getEnergySkateParkModel() {
        return energyModel;
    }

    public EnergyLookAndFeel getEnergyLookAndFeel() {
        return energyLookAndFeel;
    }

    public EnergySkateParkSimulationPanel getEnergyConservationCanvas() {
        return energyCanvas;
    }

    public void reset() {
        energyModel.reset();
        energyCanvas.reset();
        energyTimeSeriesModel.reset();
        energyTimeSeriesModel.setLiveMode();
        energyTimePlotCanvas.reset();
        init();
        energyTimeSeriesModel.startLiveMode();
        barGraphCanvas.reset();
    }

    public void resetSkater() {
        if( getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = getEnergySkateParkModel().getBody( 0 );
            resetSkater( body );
        }
    }

    public void resetSkater( Body body ) {
        body.reset();
        initBodyOnTrack( body );
    }

    private void init() {
        final Body body = createBody();
        body.reset();
        energyModel.addBody( body );
        energyCanvas.getRootNode().updateGraphics();

        EnergySkateParkSpline espspline = createDefaultTrack();
        energyModel.addSplineSurface( espspline );
        energyCanvas.initPieGraphic();
        energyCanvas.removeAllAttachmentPointGraphics();
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

    public Object getModelState() {
        return energyModel.copyState();
    }

    public void setState( EnergySkateParkModel model ) {
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
        this.getEnergySkateParkModel().clearPaths();
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
        int response = JOptionPane.showConfirmDialog( getSimulationPanel(), EnergySkateParkStrings.getString( "are.you.sure.you.want.to.reset" ) );
        if( response == JOptionPane.OK_OPTION ) {
            reset();
        }
    }

    public void save() throws UnavailableServiceException, IOException {
        FileSaveService fos = PhetServiceManager.getFileSaveService( getSimulationPanel() );
        StringOutputStream stringOutputStream = new StringOutputStream();
        XMLEncoder xmlEncoder = new XMLEncoder( stringOutputStream );
        xmlEncoder.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
        xmlEncoder.writeObject( new EnergySkateParkModuleBean( this ) );
        xmlEncoder.close();

        InputStream stream = new ByteArrayInputStream( stringOutputStream.toString().getBytes() );
        FileContents data = new InputStreamFileContents( "esp_output", stream );
        FileContents out = fos.saveAsFileDialog( null, new String[]{"esp"}, data );
        System.out.println( "Saved file." );
    }

    public void open() throws UnavailableServiceException, IOException, ClassNotFoundException {
        FileOpenService fos = PhetServiceManager.getFileOpenService( getSimulationPanel() );
        FileContents open = fos.openFileDialog( null, new String[]{"esp"} );
        if( open == null ) {
            return;
        }

        XMLDecoder xmlDecoder = new XMLDecoder( open.getInputStream() );
        Object obj = xmlDecoder.readObject();
        if( obj instanceof EnergySkateParkModuleBean ) {
            EnergySkateParkModuleBean energySkateParkModelBean = (EnergySkateParkModuleBean)obj;
            energySkateParkModelBean.apply( this );
        }
    }

    public void open( String filename ) {
        XMLDecoder xmlDecoder = new XMLDecoder( getClass().getClassLoader().getResourceAsStream( filename ) );
        Object obj = xmlDecoder.readObject();
        if( obj instanceof EnergySkateParkModuleBean ) {
            EnergySkateParkModuleBean energySkateParkModelBean = (EnergySkateParkModuleBean)obj;
            energySkateParkModelBean.apply( this );
        }
    }

    public Planet[] getPlanets() {
        return new Planet[]{new Planet.Space(), new Planet.Moon(), new Planet.Earth(), new Planet.Jupiter()};
    }

    public Frame getPhetFrame() {
        return phetFrame;
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        this.skaterCharacter = skaterCharacter;
        energyModel.setSkaterCharacter( skaterCharacter );
        energyCanvas.setSkaterCharacter( skaterCharacter );
        notifySkaterCharacterChanged();
    }

    public SkaterCharacter getSkaterCharacter() {
        return skaterCharacter;
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return skaterCharacterSet.getSkaterCharacters();
    }


    public SkaterCharacter getDefaultSkaterCharacter() {
        return skaterCharacterSet.getSkaterCharacters()[0];
    }

    public Body createBody() {
        return new Body( getSkaterCharacter().getModelWidth(), getSkaterCharacter().getModelHeight(), getEnergySkateParkModel().getParticleStage(), getEnergySkateParkModel().getGravity(), getEnergySkateParkModel().getZeroPointPotentialY() );
    }

    public static interface Listener {
        void skaterCharacterChanged();
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
}
