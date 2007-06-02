/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.persistence.Point2DPersistenceDelegate;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.servicemanager.InputStreamFileContents;
import edu.colorado.phet.common.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.model.*;
import edu.colorado.phet.energyskatepark.plots.BarGraphCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyPositionPlotCanvas;
import edu.colorado.phet.energyskatepark.plots.EnergyVsTimePlot;
import edu.colorado.phet.energyskatepark.serialization.EnergySkateParkModuleBean;
import edu.colorado.phet.energyskatepark.view.*;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
    private TimeSeriesModel timeSeriesModel;
    private EnergyVsTimePlot energyVsTimePlot;
    private EnergySkateParkOptions options;

    public EnergySkateParkModule( String name, Clock clock, PhetFrame phetFrame, EnergySkateParkOptions options ) {
        super( name, clock );
        this.options = options;
        this.phetFrame = phetFrame;
        energyModel = new EnergySkateParkModel( floorY );
        energyModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void stateSet() {
                redrawAllGraphics();
            }
        } );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EnergySkateParkRecordableModel( getEnergySkateParkModel() );
        timeSeriesModel = new TimeSeriesModel( energyTimeSeriesModel, clock );
        timeSeriesModel.setMaxRecordTime( 200.0 );
        clock.addClockListener( timeSeriesModel );

        energyCanvas = new EnergySkateParkSimulationPanel( this );
        setSimulationPanel( energyCanvas );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );

        clock.addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent clockEvent ) {
                energySkateParkControlPanel.update();
            }
        } );

        barChartFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.bar-graph" ), false );
        barGraphCanvas = new BarGraphCanvas( this );
        barChartFrame.setContentPane( barGraphCanvas );

        barChartFrame.setSize( energyFrameWidth, 625 );
        barChartFrame.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - energyFrameWidth, 0 );

        energyVsTimePlot = new EnergyVsTimePlot( phetFrame, clock, energyModel, timeSeriesModel );

        init();
        energyPositionPlotFrame = new JDialog( phetFrame, EnergySkateParkStrings.getString( "plots.energy-vs-position" ), false );
        energyPositionCanvas = new EnergyPositionPlotCanvas( this );
        energyPositionPlotFrame.setContentPane( energyPositionCanvas );
        energyPositionPlotFrame.setSize( 400, 400 );

        getModulePanel().setClockControlPanel( new EnergySkateParkTimePanel( clock ) );

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

    public EnergySkateParkSimulationPanel getEnergyConservationCanvas() {
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
        setSkaterCharacter( getDefaultSkaterCharacter() );
    }

    public void resetSkater() {
        if( getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = getEnergySkateParkModel().getBody( 0 );
            resetSkater( body );
        }
    }

    public void resetSkater( Body body ) {
        body.reset();
        if( !body.isRestorePointSet() ) {
            initBodyOnTrack( body );
        }
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

    private void redrawAllGraphics() {
        energyCanvas.redrawAllGraphics();
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
        this.getEnergySkateParkModel().clearPaths();
    }

    public void setEnergyTimePlotVisible( boolean b ) {
        energyVsTimePlot.setVisible( b );
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
        int response = JOptionPane.showConfirmDialog( getSimulationPanel(), EnergySkateParkStrings.getString( "message.confirm-reset" ), EnergySkateParkStrings.getString( "message.confirm-reset-title" ), JOptionPane.YES_NO_OPTION );
        if( response == JOptionPane.OK_OPTION ) {
            reset();
        }
    }

    public void save() throws UnavailableServiceException, IOException {
        String xml = toXMLString();
        System.out.println( "xml = " + xml );
        InputStream stream = new ByteArrayInputStream( xml.getBytes() );
        FileContents data = new InputStreamFileContents( "esp_output", stream );

        FileSaveService fos = PhetServiceManager.getFileSaveService( getSimulationPanel() );
        FileContents out = fos.saveAsFileDialog( null, null, data );
        System.out.println( "Saved file." );
    }

    private String toXMLString() {
        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        XMLEncoder e = new XMLEncoder( out );
        e.setPersistenceDelegate( Point2D.Double.class, new Point2DPersistenceDelegate() );
        e.writeObject( new EnergySkateParkModuleBean( this ) );
        e.writeObject( new JButton( "My Button" ) );
        e.close();

        return out.toString();
    }

    public void open() throws UnavailableServiceException, IOException, ClassNotFoundException {
        FileOpenService fos = PhetServiceManager.getFileOpenService( getSimulationPanel() );
        FileContents open = fos.openFileDialog( null, null );
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
        System.out.println( "filename = " + filename );
        XMLDecoder xmlDecoder = new XMLDecoder( Thread.currentThread().getContextClassLoader().getResourceAsStream( filename ) );
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
