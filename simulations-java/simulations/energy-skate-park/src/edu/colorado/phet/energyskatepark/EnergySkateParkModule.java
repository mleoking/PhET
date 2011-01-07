// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.geom.Point2D;

import javax.swing.JOptionPane;

import edu.colorado.phet.common.phetcommon.math.SerializablePoint2D;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.help.HelpBalloon;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.model.*;
import edu.colorado.phet.energyskatepark.plots.BarChartDialog;
import edu.colorado.phet.energyskatepark.plots.EnergyPositionPlotDialog;
import edu.colorado.phet.energyskatepark.plots.EnergyTimePlot;
import edu.colorado.phet.energyskatepark.view.EnergyLookAndFeel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkControlPanel;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkSimulationPanel;
import edu.colorado.phet.energyskatepark.view.WiggleMeInSpace;
import edu.colorado.phet.energyskatepark.view.swing.EnergySkateParkTimePanel;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * User: Sam Reid
 * Date: Sep 21, 2005
 * Time: 3:06:31 AM
 */

public class EnergySkateParkModule extends PiccoloModule {
    private EnergySkateParkModel energyModel;
    private EnergySkateParkSimulationPanel energySkateParkSimulationPanel;
    private EnergyLookAndFeel energyLookAndFeel = new EnergyLookAndFeel();
    private BarChartDialog barChartDialog;
    private double floorY = 0.0;
    private EnergySkateParkRecordableModel energyTimeSeriesModel;
    private SkaterCharacterSet skaterCharacterSet = new SkaterCharacterSet();

    private PhetFrame phetFrame;

    private EnergySkateParkControlPanel energySkateParkControlPanel;
    private TimeSeriesModel timeSeriesModel;

    private EnergyTimePlot energyTimePlot;
    private EnergyPositionPlotDialog energyPositionPlotDialog;

    private EnergySkateParkOptions options;

    private static final boolean DEFAULT_BAR_CHARTS_VISIBLE = false;
    private static final boolean DEFAULT_PLOT_VISIBLE = false;
    private static final boolean DEFAULT_ENERGY_POSITION_PLOT_VISIBLE = false;

    public EnergySkateParkModule( String name, ConstantDtClock clock, PhetFrame phetFrame, EnergySkateParkOptions options ) {
        super( name, clock );
        this.options = options;
        this.phetFrame = phetFrame;
        energyModel = new EnergySkateParkModel( floorY );
        setModel( new BaseModel() );

        energyTimeSeriesModel = new EnergySkateParkRecordableModel( getEnergySkateParkModel() );
        timeSeriesModel = new TimeSeriesModel( energyTimeSeriesModel, clock );
        timeSeriesModel.setMaxAllowedRecordTime( EnergyTimePlot.MAX_TIME );
        clock.addClockListener( timeSeriesModel );

        energySkateParkSimulationPanel = new EnergySkateParkSimulationPanel( this );
        setSimulationPanel( energySkateParkSimulationPanel );

        energySkateParkControlPanel = new EnergySkateParkControlPanel( this );
        setControlPanel( energySkateParkControlPanel );

        barChartDialog = new BarChartDialog( phetFrame, EnergySkateParkStrings.getString( "plots.bar-graph" ), false, this );
        barChartDialog.setSize( 200, 625 );
        barChartDialog.setLocation( Toolkit.getDefaultToolkit().getScreenSize().width - barChartDialog.getWidth(), 0 );

        energyTimePlot = new EnergyTimePlot( this, phetFrame, clock, energyModel, timeSeriesModel );
        energyTimePlot.addListener( new EnergyTimePlot.Listener() {
            public void visibilityChanged() {
                setRecordOrLiveMode();
            }
        } );

        addDefaultBody();
        energyPositionPlotDialog = new EnergyPositionPlotDialog( phetFrame, EnergySkateParkStrings.getString( "plots.energy-vs-position" ), false, this );
        energyPositionPlotDialog.setSize( 400, 400 );

        EnergySkateParkTimePanel timePanel = new EnergySkateParkTimePanel( this, clock );
        getModulePanel().setClockControlPanel( timePanel );

        setDefaults();
        setLogoPanelVisible( false );
        new WiggleMeInSpace( this ).start();

        final HelpBalloon trackHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkStrings.getString("help.grab-a-track"), HelpBalloon.TOP_CENTER, 20 );
        getDefaultHelpPane().add( trackHelp );
        trackHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSplineToolbox(), energySkateParkSimulationPanel );

        final HelpBalloon skateHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkStrings.getString("help.drag-the-skater"), HelpBalloon.RIGHT_CENTER, 20 ) {
            public Point2D mapLocation( PNode node, PCanvas canvas ) {
                if( node == null || node.getParent() == null ) {
                    return new Point2D.Double( 0, 0 );
                }
                else {
                    return super.mapLocation( node, canvas );
                }
            }
        };
        getDefaultHelpPane().add( skateHelp );
        skateHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSkaterNode( 0 ), energySkateParkSimulationPanel );
        energyModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void bodyCountChanged() {
                if( energySkateParkSimulationPanel.getRootNode().numBodyGraphics() > 0 ) {
                    skateHelp.pointAt( energySkateParkSimulationPanel.getRootNode().getSkaterNode( 0 ), energySkateParkSimulationPanel );
                }
            }
        } );

        final HelpBalloon trackClickHelp = new HelpBalloon( getDefaultHelpPane(), EnergySkateParkStrings.getString("help.right-click-for-options"), HelpBalloon.BOTTOM_CENTER, 20 );
        getDefaultHelpPane().add( trackClickHelp );
        trackClickHelp.pointAt( 0, 0 );
        energyModel.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void preStep() {//todo: won't update while paused
                if( energyModel.getNumSplines() > 0 && isHelpEnabled() ) {
                    trackHelp.setVisible( isHelpEnabled() );
                    SerializablePoint2D pt = energyModel.getSpline( 0 ).getParametricFunction2D().evaluate( 0.25 );
                    Point2D w = new Point2D.Double( pt.getX(), pt.getY() );
                    energySkateParkSimulationPanel.getRootNode().worldToScreen( w );
                    trackClickHelp.pointAt( w );
                }
                else {
                    trackHelp.setVisible( false );
                }
            }
        } );
    }

    public boolean hasHelp() {
        return true;
    }

    private void setDefaults() {
        setBarChartVisible( DEFAULT_BAR_CHARTS_VISIBLE );
        setEnergyTimePlotVisible( DEFAULT_PLOT_VISIBLE );
        setEnergyPositionPlotVisible( DEFAULT_ENERGY_POSITION_PLOT_VISIBLE );
    }

    public EnergySkateParkModel getEnergySkateParkModel() {
        return energyModel;
    }

    public EnergyLookAndFeel getEnergyLookAndFeel() {
        return energyLookAndFeel;
    }

    public EnergySkateParkSimulationPanel getEnergySkateParkSimulationPanel() {
        return energySkateParkSimulationPanel;
    }

    public void reset() {
        energyModel.reset();
        energySkateParkSimulationPanel.reset();
        energySkateParkControlPanel.reset();
        timeSeriesModel.reset();
        timeSeriesModel.setLiveMode();
        timeSeriesModel.startLiveMode();
        energyTimePlot.reset();
        barChartDialog.reset();
        energyPositionPlotDialog.reset();
        addDefaultBody();
        setPieChartVisible( false );
    }

    public void returnOrResetSkater() {
        if( getEnergySkateParkModel().getNumBodies() > 0 ) {
            Body body = getEnergySkateParkModel().getBody( 0 );
            returnOrResetSkater( body );
        }
    }

    private void returnOrResetSkater( Body body ) {
        if( body.isRestorePointSet() ) {
            returnSkateToRestorePoint( body );
        }
        else {
            reinitializeSkater( body );
        }
    }

    public void reinitializeSkater( Body body ) {
        body.deleteRestorePoint();
        body.reset();
        initBodyOnTrack( body );
    }

    public void returnSkateToRestorePoint( Body body ) {
        body.reset();
        if( !body.isRestorePointSet() ) {
            initBodyOnTrack( body );
        }
        if( !getEnergySkateParkSimulationPanel().isSkaterOnscreen( body ) ) {
            body.deleteRestorePoint();
            body.reset();
            initBodyOnTrack( body );
        }
    }

    private void addDefaultBody() {
        Body body = energyModel.createBody();
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
        return energySkateParkSimulationPanel.isMeasuringTapeVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        energySkateParkSimulationPanel.setMeasuringTapeVisible( selected );
    }

    public boolean isPieChartVisible() {
        return energySkateParkSimulationPanel.isPieChartVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        energySkateParkSimulationPanel.setPieChartVisible( selected );
    }

    public void clearPaths() {
        this.getEnergySkateParkModel().clearHistory();
    }

    public void setEnergyTimePlotVisible( boolean b ) {
        energyTimePlot.setVisible( b );
    }

    public void setBarChartVisible( boolean b ) {
        barChartDialog.setVisible( b );
    }

    public void setCoefficientOfFriction( double value ) {
        for( int i = 0; i < getEnergySkateParkModel().getNumBodies(); i++ ) {
            getEnergySkateParkModel().getBody( i ).setFrictionCoefficient( value );
        }
    }

    public void setEnergyPositionPlotVisible( boolean b ) {
        energyPositionPlotDialog.setVisible( b );
    }

    public void setBounciness( double bounciness ) {
        EnergySkateParkModel model = getEnergySkateParkModel();
        for( int i = 0; i < model.getNumBodies(); i++ ) {
            model.getBody( i ).setBounciness( bounciness );
        }
    }

    public void confirmAndReset() {
        int response = PhetOptionPane.showYesNoDialog( getSimulationPanel(), EnergySkateParkStrings.getString( "message.confirm-reset" ) );
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
    }

    public SkaterCharacter getSkaterCharacter() {
        return energyModel.getSkaterCharacter();
    }

    public SkaterCharacter[] getSkaterCharacters() {
        return skaterCharacterSet.getSkaterCharacters();
    }

    public void setEnergyErrorVisible( boolean selected ) {
        energySkateParkSimulationPanel.setEnergyErrorVisible( selected );
    }

    public boolean isEnergyErrorVisible() {
        return energySkateParkSimulationPanel.isEnergyErrorVisible();
    }

    public void showNewEnergyVsTimePlot() {
        energyTimePlot.setVisible( true );
    }

    public EnergySkateParkOptions getOptions() {
        return options;
    }

    private boolean isEnergyVsTimeGraphVisible() {
        return energyTimePlot.isVisible();
    }

    public void setRecordOrLiveMode() {
        if( isEnergyVsTimeGraphVisible() ) {
            timeSeriesModel.setRecordMode();
        }
        else {
            timeSeriesModel.setLiveMode();
        }
    }

    public Body createBody() {
        return energyModel.createBody();
    }

}
