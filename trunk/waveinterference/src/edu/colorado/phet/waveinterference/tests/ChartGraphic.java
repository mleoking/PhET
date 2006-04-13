/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.jfreechart.piccolo.JFreeChartNode;
import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.view.LatticeScreenCoordinates;
import edu.colorado.phet.waveinterference.view.WaveSampler;
import edu.umd.cs.piccolo.PNode;
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

public class ChartGraphic extends PNode {
    private LatticeScreenCoordinates latticeScreenCoordinates;
    private WaveModel waveModel;
    private JFreeChart jFreeChart;
    private JFreeChartNode jFreeChartNode;
    private PPath path;

    public ChartGraphic( String title, LatticeScreenCoordinates latticeScreenCoordinates, WaveModel waveModel ) {
        this.latticeScreenCoordinates = latticeScreenCoordinates;
        this.waveModel = waveModel;
        XYSeries series = new XYSeries( "0" );
        XYDataset dataset = new XYSeriesCollection( series );
        jFreeChart = ChartFactory.createXYLineChart( title, "position", title, dataset, PlotOrientation.VERTICAL, false, false, false );
        jFreeChart.getXYPlot().getRangeAxis().setRange( -1.0, 1.0 );
        jFreeChartNode = new JFreeChartNode( jFreeChart, true );
        jFreeChartNode.setBounds( 0, 0, 500, 300 );
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
    }

    private void updateLocation() {
        synchronizeWidth();
        synchronizeWidth();//in case the first one changed the insets of the graph

        Rectangle2D r2 = jFreeChartNode.getBounds();
        Rectangle2D data = jFreeChartNode.getDataArea();
        double dataX = latticeScreenCoordinates.toScreenCoordinates( 0, 0 ).getX();
        double dataY = latticeScreenCoordinates.toScreenCoordinates( 0, waveModel.getHeight() - 1 ).getY();
        double dataInsetX = data.getX() - r2.getX();
        jFreeChartNode.setBounds( dataX - dataInsetX, dataY, r2.getWidth(), r2.getHeight() );
    }

    private void synchronizeWidth() {
        double diff = getDesiredDataWidth() - getActualDataWidth();
        changeDataWidth( (int)diff );
    }

    private void changeDataWidth( int increase ) {
        Rectangle2D r2 = jFreeChartNode.getBounds();
        jFreeChartNode.setBounds( r2.getX(), r2.getY(), r2.getWidth() + increase, r2.getHeight() );
        jFreeChartNode.updateChartRenderingInfo();
    }

    private double getActualDataWidth() {
        return jFreeChartNode.getDataArea().getWidth();
    }

    private double getDesiredDataWidth() {
        Point2D min = latticeScreenCoordinates.toScreenCoordinates( 0, 0 );
        Point2D max = latticeScreenCoordinates.toScreenCoordinates( waveModel.getWidth() - 1, waveModel.getHeight() - 1 );
        return max.getX() - min.getX();
    }

    public void updateChart() {
        GeneralPath generalPath = new GeneralPath();
        double dx = latticeScreenCoordinates.getCellWidth();
        Point2D[]pts = new WaveSampler( waveModel, -60, dx ).readValues();//todo this just assumes the chart transform matches perfectly
        if( pts.length > 0 ) {
            generalPath.moveTo( (float)pts[0].getX(), (float)pts[0].getY() );
        }
        for( int i = 1; i < pts.length; i++ ) {
            generalPath.lineTo( (float)pts[i].getX(), (float)pts[i].getY() );
        }
        path.setPathTo( generalPath );
        Point2D nodeLoc = jFreeChartNode.plotToNode( new Point2D.Double( 0, 0 ) );
        path.setOffset( nodeLoc );
    }
}
