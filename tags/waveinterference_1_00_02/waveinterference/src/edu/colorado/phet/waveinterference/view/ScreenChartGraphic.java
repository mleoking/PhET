/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.umd.cs.piccolo.nodes.PPath;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Apr 12, 2006
 * Time: 8:01:59 PM
 * Copyright (c) Apr 12, 2006 by Sam Reid
 */

public class ScreenChartGraphic extends PhetPNode {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private WaveModel waveModel;
    private MutableColor strokeColor;
    private BrightnessScreenGraphic brightnessScreenGraphic;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private PPath path;

    public ScreenChartGraphic( String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel, MutableColor strokeColor, BrightnessScreenGraphic brightnessScreenGraphic ) {
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.waveModel = waveModel;
        this.strokeColor = strokeColor;
        this.brightnessScreenGraphic = brightnessScreenGraphic;//todo factor out a common model piece
        XYSeries series = new XYSeries( "0" );
        XYDataset dataset = new XYSeriesCollection( series );
        jFreeChart = ChartFactory.createXYLineChart( title, WIStrings.getString( "intensity1" ), WIStrings.getString( "position" ), dataset, PlotOrientation.VERTICAL, false, false, false );
//        jFreeChart.getTitle().setFont( new Font("Lucida Sans",Font.PLAIN, 14));
        jFreeChart.getXYPlot().getDomainAxis().setRange( 0, 1.0 );
        jFreeChartNode = new JFreeChartNode( jFreeChart, true );
        jFreeChartNode.setBounds( 0, 0, 150, 300 );
        jFreeChartNode.updateChartRenderingInfo();
        path = new PPath();
        path.setStroke( new BasicStroke( 3 ) );
        path.setStrokePaint( Color.blue );

        addChild( jFreeChartNode );
        addChild( path );
        latticeScreenCoordinates.addListener( new LatticeScreenCoordinates.Listener() {
            public void mappingChanged() {
                updateLocation();
            }
        } );
        updateLocation();

        strokeColor.addListener( new MutableColor.Listener() {
            public void colorChanged() {
                updateColor();
            }
        } );
        updateColor();
//        addChild( debug );
    }

    private void updateLocation() {
        synchronizeHeight();
        synchronizeHeight();//in case the first one changed the insets of the graph

        Rectangle2D r2 = jFreeChartNode.getBounds();
        Rectangle2D data = jFreeChartNode.getDataArea();
        double dataX = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() - 1, 0 ).getX();
        double dataY = latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getY();
        double dataInsetY = data.getY() - r2.getY();
        double dataInsetX = data.getX() - r2.getX();
//        jFreeChartNode.setBounds( dataX + dataInsetX, dataY - dataInsetY, r2.getWidth(), r2.getHeight() );
        jFreeChartNode.setBounds( 0, 0, r2.getWidth(), r2.getHeight() );
//        jFreeChartNode.setOffset( dataX + dataInsetX, dataY - dataInsetY );
        jFreeChartNode.setOffset( brightnessScreenGraphic.getFullBounds().getMaxX() + 2, dataY - dataInsetY );
//        jFreeChartNode.setBounds( 0, dataY - dataInsetY, r2.getWidth(), r2.getHeight() );
    }

    private void synchronizeHeight() {
        double diff = getDesiredDataHeight() - getActualDataHeight();
        changeDataHeight( (int)diff );
    }

    private void changeDataHeight( int increase ) {
        Rectangle2D r2 = jFreeChartNode.getBounds();
        jFreeChartNode.setBounds( r2.getX(), r2.getY(), r2.getWidth(), r2.getHeight() + increase );
        jFreeChartNode.updateChartRenderingInfo();
    }

    private double getActualDataHeight() {
        return jFreeChartNode.getDataArea().getHeight();
    }

    private double getDesiredDataHeight() {
        Point2D min = latticeScreenCoordinates.toScreenCoordinates( 0, 0 );
        Point2D max = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() - 1, waveModel.getHeight() - 1 );
        return max.getY() - min.getY();
    }

//    PText debug = new PText( "DEBUG" );

    public void updateChart() {
//        debug.setTextPaint( Color.red );
        GeneralPath generalPath = new GeneralPath();

//        fillPath( generalPath );
        fillPath2( generalPath );

        path.setPathTo( generalPath );
        Point2D nodeLoc = jFreeChartNode.plotToNode( new Point2D.Double( 0, jFreeChart.getXYPlot().getRangeAxis().getRange().getUpperBound() ) );
//        System.out.println( "nodeLocORIG = " + nodeLoc );
        jFreeChartNode.localToParent( nodeLoc );
//        System.out.println( "nodeLocPARENT = " + nodeLoc );
        path.setOffset( new Point2D.Double( nodeLoc.getX(), nodeLoc.getY() ) );
//        debug.setOffset( nodeLoc );
    }

    private double colorToMagnitude( Color color ) {
        return new ColorVector( color ).getMagnitude() * 80;
    }

    private void fillPath2( GeneralPath generalPath ) {
        generalPath.moveTo( (float)colorToMagnitude( brightnessScreenGraphic.getColor( 0 ) ), (float)( latticeScreenCoordinates.getCellWidth() * 0 ) );
        for( int j = 1; j < waveModel.getHeight(); j++ ) {
            generalPath.lineTo( (float)colorToMagnitude( brightnessScreenGraphic.getColor( j ) ), (float)( latticeScreenCoordinates.getCellWidth() * j ) );
        }
    }

    private void fillPath( GeneralPath generalPath ) {
        double dx = latticeScreenCoordinates.getCellWidth();
        Point2D[]pts = new WaveSampler( waveModel, -60, dx ).readValues();//todo this just assumes the chart transform matches perfectly
        if( pts.length > 0 ) {
            generalPath.moveTo( (float)pts[0].getY(), (float)pts[pts.length - 1].getX() );
        }
        for( int i = 1; i < pts.length; i++ ) {
            generalPath.lineTo( (float)pts[i].getY(), (float)pts[pts.length - 1 - i].getX() );
        }
    }

    public void setCurveColor( Color color ) {
        path.setStrokePaint( color );
    }

    private void updateColor() {
        setCurveColor( strokeColor.getColor() );
    }

    public Rectangle2D getChartBounds() {
        Rectangle2D bounds = jFreeChartNode.getFullBounds();
        localToParent( bounds );
        return bounds;
    }
}
