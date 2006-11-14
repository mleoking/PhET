/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.common.Legend;
import edu.colorado.phet.ec3.common.MeasuringTape;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.*;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
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
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class EnergySkateParkRootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode jetPackGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EnergySkateParkModule ec3Module;
    private EnergySkateParkSimulationPanel ec3Canvas;
    private PNode historyGraphics = new PNode();
    private MeasuringTape measuringTape;
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private static final boolean DEFAULT_PIE_CHART_VISIBLE = false;
    private PNode pieCharts = new PNode();
    private OffscreenManIndicator offscreenManIndicator;
    private boolean ignoreThermal = true;
    private PauseIndicator pauseIndicator;
    private Legend legend;
    private BackgroundScreenNode screenBackground;
    private SplineToolbox splineToolbox;
    //    private PNode toolboxPlaceholder;
    private FloorGraphic floorGraphic;
    private ZeroPointPotentialGraphic zeroPointPotentialGraphic;
    public static final Color SKY_COLOR = new Color( 170, 200, 220 );
    private GridNode gridNode;

    public EnergySkateParkRootNode( EnergySkateParkModule ec3Module, EnergySkateParkSimulationPanel ec3Canvas ) {
        this.ec3Module = ec3Module;
        this.ec3Canvas = ec3Canvas;
        EnergyConservationModel ec3Model = getModel();
        Floor floor = ec3Model.floorAt( 0 );

        ec3Canvas.setBackground( SKY_COLOR );
//        toolboxPlaceholder = new PNode();

//        screenBackground.addChild( new PPath( new Ellipse2D.Double( 50, 50, 300, 300 ) ) );
        splineToolbox = new SplineToolbox( ec3Canvas, this );

        double coordScale = 1.0 / 1.0;
        measuringTape = new MeasuringTape( coordScale, new Point2D.Double( 100, 100 ), bodyGraphics );//any world node should do here, no?
        pauseIndicator = new PauseIndicator( ec3Module, ec3Canvas, this );
        legend = new EC3Legend( ec3Module );
        floorGraphic = new FloorGraphic( floor );
        screenBackground = new BackgroundScreenNode( ec3Canvas, null, floorGraphic );
        zeroPointPotentialGraphic = new ZeroPointPotentialGraphic( ec3Canvas );
        offscreenManIndicator = new OffscreenManIndicator( ec3Canvas, ec3Module, numBodyGraphics() > 0 ? bodyGraphicAt( 0 ) : null );
        gridNode = new GridNode();

        addScreenChild( screenBackground );
        addScreenChild( splineToolbox );
        addWorldChild( floorGraphic );
        addWorldChild( splineGraphics );

        addWorldChild( jetPackGraphics );
        addWorldChild( bodyGraphics );

        addWorldChild( historyGraphics );
        addScreenChild( measuringTape );
        addScreenChild( pieCharts );
        addScreenChild( pauseIndicator );
        addScreenChild( legend );
//        addWorldChild( toolboxPlaceholder );
        addScreenChild( zeroPointPotentialGraphic );
        addScreenChild( offscreenManIndicator );
        addWorldChild( gridNode );
        setGridVisible( false );

        resetDefaults();
        ec3Canvas.addComponentListener( new ComponentListener() {
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

//        addClouds();
    }

    private void addClouds() {
        Random random = new Random();
        BufferedImage newImage = null;
        try {
            newImage = ImageLoader.loadBufferedImage( "images/cloud2.gif" );
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

//    public PNode getToolboxPlaceholder() {
//        return toolboxPlaceholder;
//    }


    public BackgroundScreenNode getBackground() {
        return screenBackground;
    }

    public void setBackground( Image image ) {
        screenBackground.setBackground( image );
    }

//    private void updateBackgroundImage() {
//        screenBackground.update();
//    }

    private void resetDefaults() {
        setPieChartVisible( DEFAULT_PIE_CHART_VISIBLE );
        setMeasuringTapeVisible( DEFAULT_TAPE_VISIBLE );
    }

    public void initPieChart() {
        PieChartIndicator pieChartIndicator = new PieChartIndicator( ec3Module, bodyGraphicAt( 0 ) );
        pieChartIndicator.setIgnoreThermal( ignoreThermal );
        pieCharts.addChild( pieChartIndicator );
    }

    private EnergyConservationModel getModel() {
        return ec3Module.getEnergyConservationModel();
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
                Floor floor = getModel().floorAt( 0 );
                BufferedImage newImage = ImageLoader.loadBufferedImage( "images/schoolbus200.gif" );
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

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.addChild( splineGraphic );
    }

    public void reset() {
        bodyGraphics.removeAllChildren();
        splineGraphics.removeAllChildren();
        jetPackGraphics.removeAllChildren();
        clearBuses();
        pieCharts.removeAllChildren();
        setZeroPointVisible( false );
//        resetDefaults();//needs MVC update before this will work.
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
        offscreenManIndicator.setBodyGraphic( bodyGraphic );
    }

    public void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.getChildrenReference().get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    public SplineGraphic splineGraphicAt( int i ) {
        return (SplineGraphic)splineGraphics.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void removeSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.removeChild( splineGraphic );
    }

    public void updateGraphics() {
        updateSplines();
        updateBodies();
        updateJetPacks();
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
//        System.out.println( "getModel().numHistoryPoints() = " + getModel().numHistoryPoints() );
        while( numHistoryGraphics() < getModel().numHistoryPoints() ) {
            addHistoryGraphic( new HistoryPointGraphic( getModel().historyPointAt( 0 ) ) );
        }
        while( numHistoryGraphics() > getModel().numHistoryPoints() ) {
            removeHistoryPointGraphic( historyGraphicAt( numHistoryGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numHistoryPoints(); i++ ) {
            historyGraphicAt( i ).setHistoryPoint( getModel().historyPointAt( i ) );
        }
    }

    private HistoryPointGraphic historyGraphicAt( int i ) {
        return (HistoryPointGraphic)historyGraphics.getChild( i );
    }

    private void removeHistoryPointGraphic( HistoryPointGraphic graphic ) {
        historyGraphics.removeChild( graphic );
    }

    private void addHistoryGraphic( HistoryPointGraphic historyPointGraphic ) {
        historyGraphics.addChild( historyPointGraphic );
    }

    private int numHistoryGraphics() {
        return historyGraphics.getChildrenCount();
    }

    private void updateBodies() {
        while( numBodyGraphics() < getModel().numBodies() ) {
            addBodyGraphic( new BodyGraphic( ec3Module, getModel().bodyAt( 0 ) ) );
        }
        while( numBodyGraphics() > getModel().numBodies() ) {
            removeBodyGraphic( bodyGraphicAt( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numBodies(); i++ ) {
            bodyGraphicAt( i ).setBody( getModel().bodyAt( i ) );
        }
    }

    private void updateJetPacks() {
        while( numJetPackGraphics() < getModel().numBodies() ) {
            addJetPackGraphic( new JetPackGraphic( ec3Module, getModel().bodyAt( 0 ) ) );
        }
        while( numJetPackGraphics() > getModel().numBodies() ) {
            removeJetPackGraphic( jetPackGraphicAt( numBodyGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numBodies(); i++ ) {
            jetPackGraphicAt( i ).setBody( getModel().bodyAt( i ) );
        }
    }

    private int numJetPackGraphics() {
        return jetPackGraphics.getChildrenCount();
    }

    private JetPackGraphic jetPackGraphicAt( int i ) {
        return (JetPackGraphic)jetPackGraphics.getChild( i );
    }

    private void removeJetPackGraphic( PNode bodyGraphic ) {
        jetPackGraphics.removeChild( bodyGraphic );
    }

    private void addJetPackGraphic( PNode jetPackGraphic ) {
        jetPackGraphics.addChild( jetPackGraphic );
        int[] x = {1, 2, 3};
        String[] s = {",", "b"};
    }

    private void updateSplines() {
        while( numSplineGraphics() < getModel().numSplineSurfaces() ) {
            addSplineGraphic( new SplineGraphic( ec3Canvas, getModel().splineSurfaceAt( 0 ) ) );
        }
        while( numSplineGraphics() > getModel().numSplineSurfaces() ) {
            removeSplineGraphic( splineGraphicAt( numSplineGraphics() - 1 ) );
        }
        for( int i = 0; i < getModel().numSplineSurfaces(); i++ ) {
            splineGraphicAt( i ).setSplineSurface( getModel().splineSurfaceAt( i ) );
        }
    }

    private void removeBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.removeChild( bodyGraphic );
    }

    public int numBodyGraphics() {
        return bodyGraphics.getChildrenCount();
    }

    public BodyGraphic bodyGraphicAt( int i ) {
        return (BodyGraphic)bodyGraphics.getChild( i );
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
//        legend.setOffset( getEC3Panel().getWidth() - legend.getFullBounds().getWidth() - insetX, insetY );
        legend.setOffset( getEC3Panel().getWidth() - legend.getFullBounds().getWidth() - insetX, getEC3Panel().getHeight() - legend.getFullBounds().getHeight() - insetY );
    }

    private EnergySkateParkSimulationPanel getEC3Panel() {
        return ec3Canvas;
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
}
