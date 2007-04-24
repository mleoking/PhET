/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.SkaterCharacter;
import edu.colorado.phet.energyskatepark.common.MeasuringTape;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.colorado.phet.energyskatepark.model.Floor;
import edu.colorado.phet.common.piccolophet.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 *
 */

public class EnergySkateParkRootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode jetPackGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EnergySkateParkModule module;
    private EnergySkateParkSimulationPanel simulationPanel;
    private PNode historyGraphics = new PNode();
    private PNode historyReadouts = new PNode();
    private MeasuringTape measuringTape;
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private static final boolean DEFAULT_PIE_CHART_VISIBLE = false;
    private PNode pieCharts = new PNode();
    private OffscreenManIndicator offscreenManIndicator;
    private boolean ignoreThermal = true;
    private PauseIndicator pauseIndicator;
    private EnergySkateParkLegend legend;
    private BackgroundScreenNode screenBackground;
    private SplineToolbox splineToolbox;
    private FloorGraphic floorGraphic;
    private ZeroPointPotentialGraphic zeroPointPotentialGraphic;
    public static final Color SKY_COLOR = new Color( 170, 200, 220 );
    private GridNode gridNode;
    private PanZoomOnscreenControl panZoomControls;
    private SkaterCharacter skaterCharacter;
    private EnergyErrorIndicator energyErrorIndicator;

    public EnergySkateParkRootNode( final EnergySkateParkModule module, EnergySkateParkSimulationPanel simulationPanel ) {
        this.module = module;
        this.simulationPanel = simulationPanel;
        EnergySkateParkModel ec3Model = getModel();
        Floor floor = ec3Model.getFloor();

        simulationPanel.setBackground( SKY_COLOR );
//        toolboxPlaceholder = new PNode();

//        screenBackground.addChild( new PPath( new Ellipse2D.Double( 50, 50, 300, 300 ) ) );
        splineToolbox = new SplineToolbox( simulationPanel, this );

        double coordScale = 1.0 / 1.0;
        measuringTape = new MeasuringTape( coordScale, new Point2D.Double( 100, 100 ), bodyGraphics );//any world node should do here, no?
        pauseIndicator = new PauseIndicator( module, simulationPanel, this );
        legend = new EnergySkateParkLegend( module );
//        legend.addNegPEEntry();
        floorGraphic = new FloorGraphic( module, getModel(), floor );
        screenBackground = new BackgroundScreenNode( simulationPanel, null, floorGraphic, this );
        zeroPointPotentialGraphic = new ZeroPointPotentialGraphic( simulationPanel );
        offscreenManIndicator = new OffscreenManIndicator( simulationPanel, module, numBodyGraphics() > 0 ? bodyGraphicAt( 0 ) : null );
        gridNode = new GridNode();

        final HouseNode houseNode = new HouseNode();

        addScreenChild( screenBackground );
        addScreenChild( splineToolbox );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {

            public void gravityChanged() {
                houseNode.setVisible( module.getEnergySkateParkModel().getGravity() == EnergySkateParkModel.G_EARTH );
            }
        } );
        addWorldChild( houseNode );
        addWorldChild( floorGraphic );
        addWorldChild( splineGraphics );

        addWorldChild( jetPackGraphics );
        addWorldChild( bodyGraphics );

        addScreenChild( historyGraphics );
        addScreenChild( historyReadouts );

        addScreenChild( measuringTape );
        addScreenChild( pieCharts );
        addScreenChild( pauseIndicator );
        addScreenChild( legend );
        addScreenChild( zeroPointPotentialGraphic );
        addScreenChild( offscreenManIndicator );


        addWorldChild( gridNode );
        setGridVisible( false );

        resetDefaults();
        simulationPanel.addComponentListener( new ComponentListener() {
            public void componentHidden( ComponentEvent e ) {
            }

            public void componentMoved( ComponentEvent e ) {
            }

            public void componentResized( ComponentEvent e ) {
                screenBackground.update();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
        setZeroPointVisible( false );
        getModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void gravityChanged() {
                updateGraphics();
            }
        } );
//        addClouds();
        panZoomControls = new PanZoomOnscreenControl( simulationPanel );
        addScreenChild( panZoomControls );

//        PDebug.debugRegionManagement=true;
//        PDebug.debugBounds=true;
//        PDebug.debugPaintCalls=true;

        energyErrorIndicator = new EnergyErrorIndicator( module.getEnergySkateParkModel() );
        addScreenChild( energyErrorIndicator );
        simulationPanel.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                updateEnergyIndicator();
            }

            public void componentResized( ComponentEvent e ) {
                updateEnergyIndicator();
            }
        } );
    }

    private void updateEnergyIndicator() {
        double insetX = 20;
        double insetY = 20;
        energyErrorIndicator.setOffset( insetX, simulationPanel.getHeight() - insetY - energyErrorIndicator.getFullBounds().getHeight() );
    }

    private void addClouds() {
        Random random = new Random();
        BufferedImage newImage = null;
        try {
            newImage = ImageLoader.loadBufferedImage( "energy-skate-park/images/cloud2.gif" );
            newImage = BufferedImageUtils.flipY( newImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        for( int i = 0; i < 10; i++ ) {

            PImage image = new PImage( newImage );
            image.setOffset( ( random.nextDouble() - 0.5 ) * 30, random.nextDouble() * 20 + 5 );
            image.setScale( 0.01 );

            addWorldChild( image );
        }
    }

    public BackgroundScreenNode getBackground() {
        return screenBackground;
    }

    public void setBackground( Image image ) {
        screenBackground.setBackground( image );
    }

    private void resetDefaults() {
        setPieChartVisible( DEFAULT_PIE_CHART_VISIBLE );
        setMeasuringTapeVisible( DEFAULT_TAPE_VISIBLE );
    }

    public void initPieChart() {
        PieChartIndicator pieChartIndicator = new PieChartIndicator( module, bodyGraphicAt( 0 ) );
        pieChartIndicator.setIgnoreThermal( ignoreThermal );
        pieCharts.addChild( pieChartIndicator );
    }

    private EnergySkateParkModel getModel() {
        return module.getEnergySkateParkModel();
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
    }

    public void clearBuses() {
        if( buses != null ) {
            buses.removeAllChildren();
            removeChild( buses );
            buses = null;
        }
    }

    public void addBuses() {
        if( buses == null ) {
            try {
                buses = new PNode();
                Floor floor = getModel().getFloor();
                BufferedImage newImage = ImageLoader.loadBufferedImage( "energy-skate-park/images/schoolbus200.gif" );
                PImage schoolBus = new PImage( newImage );
                double y = floor.getY() - schoolBus.getFullBounds().getHeight() + 10;
                schoolBus.setOffset( 0, y );
                double busStart = 500;
                for( int i = 0; i < 10; i++ ) {
                    PImage bus = new PImage( newImage );
                    double dbus = 2;
                    bus.setOffset( busStart + i * ( bus.getFullBounds().getWidth() + dbus ), y );
                    buses.addChild( bus );
                }
                addWorldChild( buses );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void addSplineGraphic( SplineNode splineNode ) {
        splineGraphics.addChild( splineNode );
    }

    public void reset() {
        bodyGraphics.removeAllChildren();
        splineGraphics.removeAllChildren();
        jetPackGraphics.removeAllChildren();
        clearBuses();
        pieCharts.removeAllChildren();
        setZeroPointVisible( false );
        setMeasuringTapeVisible( false );
        panZoomControls.reset();
//        resetDefaults();//needs MVC update before this will work.
    }

    public void addBodyGraphic( SkaterNode skaterNode ) {
        bodyGraphics.addChild( skaterNode );
        if( bodyGraphics.getChildrenCount() == 1 ) {
            offscreenManIndicator.setBodyGraphic( skaterNode );
        }
    }

    public void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (SkaterNode)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                SkaterNode skaterNode = (SkaterNode)bodyGraphics.getChildrenReference().get( i );
                skaterNode.setBoxVisible( !state );
            }
        }
    }

    public SplineNode splineGraphicAt( int i ) {
        return (SplineNode)splineGraphics.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void removeSplineGraphic( SplineNode splineNode ) {
        splineGraphics.removeChild( splineNode );
    }

    public void updateGraphics() {
        updateSplines();
        updateBodies();
        updateHistory();
        updatePieChart();
        updateZeroPotential();
        offscreenManIndicator.update();
    }

    private void updateZeroPotential() {
        zeroPointPotentialGraphic.setZeroPointPotential( getModel().getZeroPointPotentialY() );
    }

    private void updatePieChart() {
        for( int i = 0; i < pieCharts.getChildrenCount(); i++ ) {
            PieChartIndicator pieChartIndicator = (PieChartIndicator)pieCharts.getChild( i );
            pieChartIndicator.update();
        }
    }

    private void updateHistory() {
//        System.out.println( "numHistoryGraphics() = " + numHistoryGraphics() );
//        System.out.println( "getModel().getNumHistoryPoints() = " + getModel().getNumHistoryPoints() );
        while( numHistoryGraphics() < getModel().getNumHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointGraphic( getModel().historyPointAt( 0 ), this ) );
        }
        while( numHistoryGraphics() > getModel().getNumHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointGraphic historyGraphicAt( int i ) {
        return (HistoryPointGraphic)historyGraphics.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointGraphic graphic ) {
        historyGraphics.removeChild( graphic );
        historyReadouts.removeChild( graphic.getReadoutGraphic() );
    }

    private void addHistoryGraphic( HistoryPointGraphic historyPointGraphic ) {
        historyGraphics.addChild( historyPointGraphic );
        historyReadouts.addChild( historyPointGraphic.getReadoutGraphic() );
    }

    private int numHistoryGraphics() {
        return historyGraphics.getChildrenCount();
    }

    private void updateBodies() {
        while( numBodyGraphics() < getModel().getNumBodies() ) {
            addBodyGraphic( new SkaterNode( getModel().getBody( 0 ) ) );
        }
        while( numBodyGraphics() > getModel().getNumBodies() ) {
            removeBodyGraphic( bodyGraphicAt( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumBodies(); i++ ) {
            bodyGraphicAt( i ).setBody( getModel().getBody( i ) );
        }
    }

    private void updateSplines() {
        while( numSplineGraphics() < getModel().getNumSplines() ) {
            addSplineGraphic( new SplineNode( simulationPanel, getModel().getSpline( 0 ), simulationPanel ) );
        }
        while( numSplineGraphics() > getModel().getNumSplines() ) {
            removeSplineGraphic( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().getNumSplines(); i++ ) {
            splineGraphicAt( i ).setSpline( getModel().getSpline( i ) );
        }
    }

    private void removeBodyGraphic( SkaterNode skaterNode ) {
        bodyGraphics.removeChild( skaterNode );
    }

    public int numBodyGraphics() {
        return bodyGraphics.getChildrenCount();
    }

    public SkaterNode bodyGraphicAt( int i ) {
        return (SkaterNode)bodyGraphics.getChild( i );
    }

    public boolean isMeasuringTapeVisible() {
        return measuringTape.getVisible();
    }

    public void setMeasuringTapeVisible( boolean selected ) {
        measuringTape.setVisible( selected );
        measuringTape.setPickable( selected );
        measuringTape.setChildrenPickable( selected );
    }

    public boolean isPieChartVisible() {
        return pieCharts.getVisible();
    }

    public void setPieChartVisible( boolean selected ) {
        pieCharts.setVisible( selected );
        legend.setVisible( selected );
    }

    public boolean getIgnoreThermal() {
        return ignoreThermal;
    }

    public void setIgnoreThermal( boolean selected ) {
        this.ignoreThermal = selected;
        for( int i = 0; i < pieCharts.getChildrenCount(); i++ ) {
            PieChartIndicator pieChartIndicator = (PieChartIndicator)pieCharts.getChild( i );
            pieChartIndicator.setIgnoreThermal( ignoreThermal );
        }
    }

    public void clearBackground() {
        BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        g2.setColor( new Color( 0, 0, 0, 255 ) );
        g2.fillRect( 0, 0, 1, 1 );
        setBackground( null );
    }

    protected void layoutChildren() {
        super.layoutChildren();
        pauseIndicator.relayout();
        double insetX = 10;
        double insetY = 10;
        legend.setOffset( getSimulationPanel().getWidth() - legend.getFullBounds().getWidth() - insetX, insetY );
        if( panZoomControls != null ) {
            panZoomControls.setOffset( getSimulationPanel().getWidth() - panZoomControls.getFullBounds().getWidth() - insetX, getSimulationPanel().getHeight() - panZoomControls.getFullBounds().getHeight() - insetY );
        }
    }

    private EnergySkateParkSimulationPanel getSimulationPanel() {
        return simulationPanel;
    }

    public void setZeroPointVisible( boolean selected ) {
        zeroPointPotentialGraphic.setVisible( selected );
    }

    public boolean isZeroPointVisible() {
        return zeroPointPotentialGraphic.getVisible();
    }

    public boolean isGridVisible() {
        return gridNode.getVisible();
    }

    public void setGridVisible( boolean selected ) {
        gridNode.setVisible( selected );
    }

    public PNode getMeasuringTapeNode() {
        return measuringTape;
    }

    public void updateScale() {
        panZoomControls.updateScale();
    }

    public void setSkaterCharacter( SkaterCharacter skaterCharacter ) {
        this.skaterCharacter = skaterCharacter;
        for( int i = 0; i < bodyGraphics.getChildrenCount(); i++ ) {
            bodyGraphicAt( i ).setSkaterCharacter( skaterCharacter );
        }
    }
}
