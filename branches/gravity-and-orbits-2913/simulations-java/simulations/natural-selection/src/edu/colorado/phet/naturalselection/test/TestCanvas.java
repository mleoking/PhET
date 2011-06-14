// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.naturalselection.test;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import edu.colorado.phet.common.jfreechartphet.piccolo.JFreeChartNode;
import edu.colorado.phet.common.jfreechartphet.piccolo.XYPlotNode;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.naturalselection.NaturalSelectionConstants;
import edu.colorado.phet.naturalselection.NaturalSelectionResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

public class TestCanvas extends PhetPCanvas {

    private JFreeChartNode chartNode;
    private XYPlotNode plotNode;

    private XYSeriesCollection mainDataset;
    private XYSeriesCollection emptyDataset;

    private XYPlot emptyPlot;
    private XYPlot mainPlot;

    private static final int RANGE = 300;
    private static final int TOTAL_INDEX = 0;
    private static final int FUR_WHITE_INDEX = 1;
    private static final int FUR_BROWN_INDEX = 2;
    private static final int TAIL_SHORT_INDEX = 3;
    private static final int TAIL_LONG_INDEX = 4;
    private static final int TEETH_SHORT_INDEX = 5;
    private static final int TEETH_LONG_INDEX = 6;

    private static final int NUM_SERIES = 7;
    private PSwing zoomHolder;

    private final int[] zoomBounds = new int[]{5, 15, 30, 50, 75, 100, 150, 200, 250, 350, 500, 1000, 2000, 3000, 5000};
    private int zoomIndex = 3;

    public TestCanvas( Dimension2D renderingSize ) {
        super( renderingSize );

        PNode root = new PNode();
        addScreenChild( root );

        mainDataset = createDataset();
        emptyDataset = createDataset();

        emptyPlot = createPlot( emptyDataset );
        mainPlot = createPlot( mainDataset );

        JFreeChart chart = new JFreeChart( emptyPlot );

        chartNode = new JFreeChartNode( chart );
        root.addChild( chartNode );

        plotNode = new XYPlotNode( mainPlot );
        root.addChild( plotNode );

        JPanel zoomPanel = new JPanel( new FlowLayout() );
        zoomPanel.setOpaque( false );

        JButton zoomInButton = new JButton( new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_ZOOM_IN ) ) );
        JButton zoomOutButton = new JButton( new ImageIcon( NaturalSelectionResources.getImage( NaturalSelectionConstants.IMAGE_ZOOM_OUT ) ) );

        zoomInButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                zoomIndex--;
                updateZoom();
            }
        } );

        zoomOutButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                zoomIndex++;
                updateZoom();
            }
        } );

        zoomInButton.setOpaque( true );
        zoomOutButton.setOpaque( true );

        zoomInButton.setMargin( new Insets( 0, 0, 0, 0 ) );
        zoomOutButton.setMargin( new Insets( 0, 0, 0, 0 ) );

        zoomPanel.add( zoomInButton );
        zoomPanel.add( zoomOutButton );

        zoomHolder = new PSwing( zoomPanel );

        root.addChild( zoomHolder );

        updateLayout();

        updateZoom();
    }

    private XYSeriesCollection createDataset() {
        XYSeriesCollection ret = new XYSeriesCollection();

        ret.addSeries( new XYSeries( "Total" ) );
        ret.addSeries( new XYSeries( "White fur" ) );
        ret.addSeries( new XYSeries( "Brown fur" ) );
        ret.addSeries( new XYSeries( "Short tail" ) );
        ret.addSeries( new XYSeries( "Long tail" ) );
        ret.addSeries( new XYSeries( "Short teeth" ) );
        ret.addSeries( new XYSeries( "Long teeth" ) );

        return ret;
    }

    private XYPlot createPlot( XYSeriesCollection dataset ) {
        XYPlot plot = new XYPlot();

        ValueAxis domainAxis = new NumberAxis( "Time" );
        domainAxis.setTickLabelsVisible( false );
        domainAxis.setRange( 0, RANGE );
        plot.setDomainAxis( domainAxis );

        ValueAxis rangeAxis = new NumberAxis( "Population" );
        rangeAxis.setStandardTickUnits( NumberAxis.createIntegerTickUnits() );
        plot.setRangeAxis( rangeAxis );
        rangeAxis.setRange( 0, 50 );

        plot.setRenderer( new StandardXYItemRenderer() );

        final int seriesIndex = 0;
        plot.setDataset( seriesIndex, dataset );
        XYItemRenderer renderer = new StandardXYItemRenderer(); // TODO: maybe use XYLineAndShapeRenderer?
        renderer.setStroke( new BasicStroke( 2f ) );
        renderer.setSeriesPaint( TOTAL_INDEX, Color.BLACK );
        renderer.setSeriesPaint( FUR_WHITE_INDEX, Color.RED );
        plot.setRenderer( seriesIndex, renderer );

        return plot;
    }

    @Override
    protected void updateLayout() {

        // chart
        final double margin = 10;
        double x = margin;
        double y = margin;
        double w = getWidth() - 2 * margin;
        double h = getHeight() - 2 * margin;
        chartNode.setBounds( 0, 0, w, h );
        chartNode.setOffset( x, y );
        chartNode.updateChartRenderingInfo();

        // Plot bounds
        ChartRenderingInfo chartInfo = chartNode.getChartRenderingInfo();
        PlotRenderingInfo plotInfo = chartInfo.getPlotInfo();

        // Careful! getDataArea returns a direct reference!
        Rectangle2D dataAreaRef = plotInfo.getDataArea();
        Rectangle2D localBounds = new Rectangle2D.Double();
        localBounds.setRect( dataAreaRef );
        Rectangle2D plotBounds = chartNode.localToGlobal( localBounds );

        // Plot node
        plotNode.setOffset( 0, 0 );
        plotNode.setDataArea( plotBounds );

        zoomHolder.setOffset( plotBounds.getX(), plotBounds.getY() );
    }

    private synchronized void updateZoom() {
        if ( zoomIndex < 0 ) {
            zoomIndex = 0;
        }
        if ( zoomIndex >= zoomBounds.length ) {
            zoomIndex = zoomBounds.length - 1;
        }
        mainPlot.getRangeAxis().setRange( 0, zoomBounds[zoomIndex] );
        emptyPlot.getRangeAxis().setRange( 0, zoomBounds[zoomIndex] );

        updateLayout();
    }

    private int pos = 0;
    private int low = 0;
    private int MIN_Y = 0;
    private int MAX_Y = 50;

    private int[] positions = new int[]{0, 5, 10, 15, 20, 25, 30};

    public synchronized void addDataPoint() {
        // TODO: possibly move this up if we for sure don't need it
        /*
        for ( int i = 0; i < NUM_SERIES; i++ ) {
            mainDataset.getSeries( i ).setNotify( false );
        }
        */

        for ( int i = 0; i < positions.length; i++ ) {
            int y = positions[i];
            y += (int) ( Math.random() * 4 - 2 );
            if ( y > MAX_Y ) { y = MAX_Y; }
            if ( y < MIN_Y ) { y = MIN_Y; }
            positions[i] = y;

            // does not send the event to listeners
            mainDataset.getSeries( i ).add( pos, y, false );
        }

        pos++;

        if ( pos >= RANGE + 1 ) {
            // TODO: possibly remove old data points?
            low++;

        }
        mainPlot.getDomainAxis().setRange( low, low + RANGE );

        /*
        for ( int i = 0; i < NUM_SERIES; i++ ) {
            // TODO: make sure this doesn't cause 7 redraws?
            //mainDataset.getSeries( i ).setNotify( true );
        }
        */
    }

}
