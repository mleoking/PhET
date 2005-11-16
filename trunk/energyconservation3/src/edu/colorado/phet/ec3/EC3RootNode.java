/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.common.MeasuringTape;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.FloorGraphic;
import edu.colorado.phet.ec3.view.PieChartIndicator;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class EC3RootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EC3Module ec3Module;
    private EC3Canvas ec3Canvas;
    private PNode historyGraphics = new PNode();
    private MeasuringTape measuringTape;
    private static final boolean DEFAULT_TAPE_VISIBLE = false;
    private static final boolean DEFAULT_PIE_CHART_VISIBLE = false;
    private PNode pieCharts = new PNode();
    private OffscreenManIndicator offscreenManIndicator;
    private boolean ignoreThermal = true;
    private PImage ec3Background;

    public EC3RootNode( EC3Module ec3Module, EC3Canvas ec3Canvas ) {
        this.ec3Module = ec3Module;
        this.ec3Canvas = ec3Canvas;
        EnergyConservationModel ec3Model = getModel();
        Floor floor = ec3Model.floorAt( 0 );

        addLayer();
        addLayer();
        ec3Canvas.setBackground( new Color( 170, 200, 220 ) );
//        layerAt( 0 ).getWorldNode().addChild( new SkyGraphic( floor.getY() ) );
//        layerAt( 0 ).getScreenNode().addChild( new SkyGraphic( floor.getY() ) );
        layerAt( 0 ).getWorldNode().addChild( new FloorGraphic( floor ) );

        final SplineToolbox splineToolbox = new SplineToolbox( ec3Canvas, this );
        layerAt( 0 ).getScreenNode().addChild( splineToolbox );

        layerAt( 1 ).getWorldNode().addChild( splineGraphics );
        layerAt( 1 ).getWorldNode().addChild( bodyGraphics );
        layerAt( 1 ).getWorldNode().addChild( historyGraphics );

//        double coordScale = 1.0 / 55.0;
        double coordScale = 1.0 / 1.0;
        measuringTape = new MeasuringTape( coordScale, new Point2D.Double( 100, 100 ), getWorldNode() );
        layerAt( 1 ).getScreenNode().addChild( measuringTape );

        layerAt( 1 ).addChild( pieCharts );
        resetDefaults();
//        offscreenManIndicator = new OffscreenManIndicator( ec3Module );
//        layerAt( 1 ).addChild( offscreenManIndicator );

        ec3Background = new PImage();
//        ec3Background.scale( 1.3);
        layerAt( 0 ).getWorldNode().addChild( ec3Background );

    }

    public PNode getBackground() {
        return ec3Background;
    }

    public void setBackground( Image image, double scale ) {
        ec3Background.setImage( image );
        ec3Background.setTransform( new AffineTransform() );
        ec3Background.scale( scale );
    }

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
        EnergyConservationModel ec3Model = ec3Module.getEnergyConservationModel();
        return ec3Model;
    }

    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
    }

    public void clearBuses() {
        if( buses != null ) {
            buses.removeAllChildren();
            removeWorldChild( buses );
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
        clearBuses();
        pieCharts.removeAllChildren();
//        resetDefaults();//needs MVC update before this will work.
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
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
        updateHistory();
        updatePieChart();
//        updateOffscreenManIndicator();
    }

//    private void updateOffscreenManIndicator() {
//        offscreenManIndicator.update();
//    }

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
}
